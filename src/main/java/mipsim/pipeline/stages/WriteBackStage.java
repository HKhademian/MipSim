package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;
import sim.real.MuxKt;
import sim.test.TestKt;

public class WriteBackStage extends Stage {
	public WriteBackStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var memwb = processor.memwb;
		final var MEMWB = processor.memwb.next;
		final var regFile = processor.registerFile;
		final var REGFILE = processor.registerFile;
		//choice data memory and alu --> to write data
		final var writeData = MuxKt.mux2(MEMWB.memToReg, memwb.aluData, memwb.memoryData);
		REGFILE.regWrite.set(memwb.regWrite);
		BusKt.set(REGFILE.writeReg, memwb.rdRegister);
		BusKt.set(REGFILE.writeData, writeData);
	}

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String... args) {
		final var processor = new Processor();
		final var MemWbReg = processor.memwb;
		final var regFile = processor.registerFile;

		TestKt.testOn(regFile, "init", () -> {
			final var time = System.currentTimeMillis();

			BusKt.set(MemWbReg.aluData, 4005);
			BusKt.set(MemWbReg.memoryData, 12044);
			BusKt.set(MemWbReg.rdRegister, 10); //write in $s9
			processor.wbStage.init();
		});

		TestKt.testOn(regFile, "Don't write anything", () -> {
			final var time = System.currentTimeMillis();

			MemWbReg.regWrite.set(false);
			MemWbReg.memToReg.set(false);
			MemWbReg.eval(time);
			regFile.eval(time);
		});

		TestKt.testOn(regFile, "write something from memory", () -> {
			final var time = System.currentTimeMillis();

			MemWbReg.regWrite.set(true);
			MemWbReg.memToReg.set(true);
			MemWbReg.eval(time);
			regFile.eval(time);
		});

		TestKt.testOn(regFile, "write something from aluResult", () -> {
			final var time = System.currentTimeMillis();

			MemWbReg.regWrite.set(true);
			MemWbReg.memToReg.set(false);
			MemWbReg.eval(time);
			regFile.eval(time);
		});

	}
}
