package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;
import sim.base.ValueKt;
import sim.real.MuxKt;
import sim.test.TestKt;

public class WriteBackStage extends Stage {
	public WriteBackStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var memwb = processor.memwb;
		final var regFile = processor.registerFile;


		//choice data memory and alu --> to write data
		final var writeData = MuxKt.mux2(memwb.memToReg, memwb.aluData, memwb.memoryData);
		regFile.regWrite.set(memwb.regWrite);
		BusKt.set(regFile.writeReg, memwb.rdRegister);
		BusKt.set(regFile.writeData, writeData);

	}

	@Override
	public void eval() {

	}

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String... args) {
		final var processor = new Processor();
		final var MemWbReg = processor.memwb;
		final var regFile = processor.registerFile;

		TestKt.testOn(regFile, "init", () -> {
			BusKt.set(MemWbReg.aluData, 4005);
			BusKt.set(MemWbReg.memoryData, 12044);
			BusKt.set(MemWbReg.rdRegister, 10);//write in $s9
			processor.wbStage.init();
		});

		TestKt.testOn(regFile, "Don't write anything", () -> {
			MemWbReg.regWrite = ValueKt.mut(false);
			MemWbReg.memToReg = ValueKt.mut(false);
			MemWbReg.eval();
			regFile.eval();
		});

		TestKt.testOn(regFile, "write something from memory", () -> {
			MemWbReg.regWrite = ValueKt.mut(true);
			MemWbReg.memToReg = ValueKt.mut(true);
			MemWbReg.eval();
			regFile.eval();
		});

		//todo check it with help of hossain :)
		TestKt.testOn(regFile, "write something from aluResult", () -> {
			MemWbReg.regWrite = ValueKt.mut(true);
			MemWbReg.memToReg = ValueKt.mut(false);
			MemWbReg.eval();
			regFile.eval();
		});

	}
}
