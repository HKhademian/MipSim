package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;
import sim.base.ValueKt;
import sim.real.MuxKt;
import sim.test.TestKt;

import static mipsim.sim.InstructionParserKt.parseInstructionToBin;

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
	 * test by Mehdi Fe
	 */
	//todo check it with help of hossain :)  
	public static void main(final String... args) {
		final var processor = new Processor();
		final var MemWbReg = processor.memwb;
		final var regFile = processor.registerFile;
		processor.wbStage.init();
		TestKt.testOn(processor.registerFile, "Don't write anything", () -> {

			BusKt.set(MemWbReg.aluData,BusKt.toBus(4005));
			BusKt.set(MemWbReg.memoryData,BusKt.toBus(12044));
			BusKt.set(MemWbReg.rdRegister,BusKt.toBus(10));//write in $s9
			MemWbReg.regWrite = ValueKt.mut(false);
			MemWbReg.memToReg = ValueKt.mut(true);
			MemWbReg.eval();
			regFile.eval();
		});


		TestKt.testOn(processor.registerFile, "write something from memory", () -> {
			BusKt.set(MemWbReg.aluData,BusKt.toBus(4005));
			BusKt.set(MemWbReg.memoryData,BusKt.toBus(12044));
			BusKt.set(MemWbReg.rdRegister,BusKt.toBus(10));//write in $s9
			MemWbReg.regWrite = ValueKt.mut(true);
			MemWbReg.memToReg = ValueKt.mut(true);
			MemWbReg.eval();
			regFile.eval();
		});

		TestKt.testOn(processor.registerFile, "write something from aluResult", () -> {
			BusKt.set(MemWbReg.aluData,BusKt.toBus(4005));
			BusKt.set(MemWbReg.memoryData,BusKt.toBus(12044));
			BusKt.set(MemWbReg.rdRegister,BusKt.toBus(10));//write in $s9
			MemWbReg.regWrite = ValueKt.mut(true);
			MemWbReg.memToReg = ValueKt.mut(false);
			MemWbReg.eval();
			regFile.eval();
		});



	}

	}
