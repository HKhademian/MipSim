package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;
import mipsim.module.Multiplexer;
import mipsim.units.AluControlUnit;
import mipsim.units.ForwardingUnit;
import sim.base.BusKt;
import sim.test.TestKt;

import static sim.base.GateKt.or;


public class ExecutionStage extends Stage {
	public ExecutionStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var idex = processor.idex;
		final var exmem = processor.exmem;
		final var memwb = processor.memwb;

		//alu control unit
		var aluOp = BusKt.bus(4);
		AluControlUnit.aluControlUnit(idex.aluOp, idex.function, aluOp);

		//first alu src
		var resultOneOfAlu = BusKt.bus(32);
		var forwardingEx1 = BusKt.bus(2);
		var forwardingMem1 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rsRegister, forwardingEx1);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite, memwb.rdRegister, idex.rsRegister, exmem.regWrite, exmem.rtRegister, forwardingMem1);
		Multiplexer.aluInput(or(forwardingEx1, forwardingMem1), idex.rsData, exmem.aluData, memwb.memoryData, resultOneOfAlu);
		BusKt.set(exmem.test2,or(forwardingEx1, forwardingMem1));

		//second alu  src
		var forwardingResult2 = BusKt.bus(32);
		var forwardingExe2 = BusKt.bus(2);
		var forwardingMem2 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rtRegister, forwardingExe2);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite, memwb.rdRegister, idex.rsRegister, exmem.regWrite, exmem.rtRegister, forwardingMem2);
		Multiplexer.aluInput(or(forwardingExe2, forwardingMem2), idex.rtData, exmem.aluData, memwb.memoryData, forwardingResult2);

		var resultTwoOfAlu = BusKt.bus(32);
		Multiplexer.aluSrc(idex.aluSrc, forwardingResult2, idex.immediate, resultTwoOfAlu);

		//alu result
		var aluData = BusKt.bus(32);
		LogicALU.AluInStage(resultOneOfAlu, resultTwoOfAlu, aluOp, idex.shiftMa, aluData);
		BusKt.set(exmem.test1,resultOneOfAlu);
		BusKt.set(exmem.test2,resultTwoOfAlu);
		BusKt.set(exmem.test3,aluData);
		BusKt.set(exmem.test4,aluData);
		BusKt.set(exmem.tmpAluData, aluData);


		//dt register
		var rdRegister = BusKt.bus(5);
		Multiplexer.dtRegister(idex.regDst, idex.rtRegister, idex.rdRegister, rdRegister);
		//set
		BusKt.set(exmem.rtRegister, rdRegister);

		//set flags that passed
		BusKt.set(exmem.WB, idex.WB);
		BusKt.set(exmem.MEM, idex.MEM);

		//set write memory
		BusKt.set(exmem.writeMem, idex.WB);
	}


	public static void main(String[] args) {
		//..
//		TestKt.test("and", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x24);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 14);
//			BusKt.set(idex.rtData, 14);
//			idex.eval(time);
//			exmem.eval(time);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//..
//		TestKt.test("add", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x20);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 12);
//			BusKt.set(memwb.rdRegister, 13);
//			BusKt.set(idex.rsRegister, 15);
//			BusKt.set(idex.rdRegister, 16);
//			BusKt.set(idex.shiftMa, 0);
//			BusKt.set(idex.rsData, 5);
//			BusKt.set(idex.rtData, 8);
//
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//..
//		TestKt.test("sub", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x22);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 20);
//			BusKt.set(idex.rtData, 14);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//..
//		TestKt.test("or", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x25);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 16);
//			BusKt.set(idex.rtData, 14);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//ohh we have some bug
//		TestKt.test("set on less", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x2A);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 16);
//			BusKt.set(idex.rtData, 14);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//..
//		TestKt.test("set on less", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x2A);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 16);
//			BusKt.set(idex.rtData, 16);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//		//..
//		TestKt.test("set on less", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 2);
//			BusKt.set(idex.function, 0x2A);
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 18);
//			BusKt.set(idex.rtData, 17);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});
//
//		TestKt.test("load word and store word addi", () -> {
//			final var time = System.currentTimeMillis();
//			final var processor = new Processor();
//			final var idex = processor.idex;
//			final var exmem = processor.exmem;
//			final var memwb = processor.memwb;
//			processor.init();
//
//
//			BusKt.set(idex.aluOp, 0);
//			BusKt.set(idex.function, 0);
//			idex.aluSrc.set(true);
//			BusKt.set(exmem.rtRegister, 7);
//			BusKt.set(memwb.rdRegister, 7);
//			BusKt.set(idex.rsRegister, 5);
//			BusKt.set(idex.rdRegister, 6);
//
//			BusKt.set(idex.rsData, 16);
//			BusKt.set(idex.rtData, 16);
//			BusKt.set(idex.immediate, 2);
//			idex.eval(time);
//			exmem.eval(time);
//			return exmem;
//		});

		//..

		TestKt.test("forwarding add", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			processor.init();



			BusKt.set(idex.aluOp, 2);
			BusKt.set(idex.function, 0x20);
			idex.aluSrc.set(false);
			idex.regDst.set(true);

			BusKt.set(memwb.rdRegister, 7);

			BusKt.set(idex.regWrite, true);

			BusKt.set(idex.rtRegister, 6);
			idex.rsRegister.get(0).set(true);
			BusKt.set(idex.rdRegister, 1);

			BusKt.set(idex.shiftMa, 0);
			BusKt.set(idex.rsData, 5);
			BusKt.set(idex.rtData, 8);

			idex.eval(time);

			exmem.eval(time);


			//idex.eval(time + 1);

			exmem.eval(time + 1);


			return exmem;
		});
	}
}
