package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.real.MuxKt;
import sim.test.TestKt;

import java.util.List;

@SuppressWarnings("unchecked")
public class WriteBackStage extends Stage {
	public WriteBackStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var memwb = processor.memwb;
		final var regFile = processor.registerFile;
		final var MEMWB = processor.memwb.next;
		final var REGFILE = processor.registerFile;
		assert MEMWB != null;
		assert REGFILE != null;

		//choice data memory and alu --> to write data
		final var writeData = MuxKt.mux2(MEMWB.memToReg, memwb.aluData, memwb.memoryData);

		// todo: check regFile
		REGFILE.regWrite.set(memwb.regWrite);
		BusKt.set(REGFILE.writeReg, memwb.rdRegister);
		BusKt.set(REGFILE.writeData, writeData);
	}

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String... args) {
		final var processor = new Processor();
		final var memwb = processor.memwb;
		final var regFile = processor.registerFile;
		final var MEMWB = processor.memwb.next;
		final var REGFILE = processor.registerFile;
		assert MEMWB != null;
		assert REGFILE != null;

		TestKt.testOn(regFile, "init", () -> {
			BusKt.set((List) MEMWB.aluData, 4005);
			BusKt.set((List) MEMWB.memoryData, 12044);
			BusKt.set((List) MEMWB.rdRegister, 10); //write in $s9
			processor.wbStage.init();
		});

		TestKt.testOn(regFile, "Don't write anything", () -> {
			final var time = System.currentTimeMillis();

			((MutableValue) MEMWB.regWrite).set(false);
			((MutableValue) MEMWB.memToReg).set(false);
			memwb.eval(time);
			regFile.eval(time);
		});

		TestKt.testOn(regFile, "write something from memory", () -> {
			final var time = System.currentTimeMillis();

			((MutableValue) MEMWB.regWrite).set(true);
			((MutableValue) MEMWB.memToReg).set(true);
			memwb.eval(time);
			regFile.eval(time);
		});

		TestKt.testOn(regFile, "write something from aluResult", () -> {
			final var time = System.currentTimeMillis();

			((MutableValue) MEMWB.regWrite).set(true);
			((MutableValue) MEMWB.memToReg).set(false);
			memwb.eval(time);
			regFile.eval(time);
		});

	}
}
