package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;

import mipsim.units.AluControlUnit;
import mipsim.units.ForwardingUnit;
import mipsim.module.Multiplexer;
import sim.base.BusKt;

import sim.base.ValueKt;

import sim.test.TestKt;

import static sim.gates.GatesKt.or;

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
		AluControlUnit.aluControlUnit(idex.aluOp,idex.function,aluOp);


		var exmem_aluData = BusKt.constant(exmem.aluData);


		//first alu src
		var resultOneOfAlu = BusKt.bus(32);
		var forwardingEx1 = BusKt.bus(2);
		var forwardingMem1 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite,exmem.rtRegister, idex.rsRegister, forwardingEx1);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem1);
		Multiplexer.aluInput(or(forwardingEx1,forwardingMem1), idex.rsData,exmem_aluData, memwb.memoryData, resultOneOfAlu);

		//second alu  src
		var forwardingResult2 = BusKt.bus(32);
		var forwardingExe2 = BusKt.bus(2);
		var forwardingMem2 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rtRegister, forwardingExe2);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem2);
		Multiplexer.aluInput(or(forwardingExe2,forwardingMem2), idex.rtData, exmem_aluData, memwb.memoryData, forwardingResult2);

		var resultTwoOfAlu = BusKt.bus(32);
		Multiplexer.aluSrc(idex.aluSrc,forwardingResult2,idex.immediate,resultTwoOfAlu);

		//alu result
		var aluData = BusKt.bus(32);
		LogicALU.AluInStage(resultOneOfAlu, resultTwoOfAlu, aluOp, idex.shiftMa, aluData);
		BusKt.set(exmem.aluData,aluData);



		//dt register
		var rdRegister = BusKt.bus(5);
		Multiplexer.dtRegister(idex.regDst,idex.rtRegister,idex.rdRegister,rdRegister);
		//set
		BusKt.set(exmem.rtRegister,rdRegister);

		//set flags that passed
		BusKt.set(exmem.WB, idex.WB);
		BusKt.set(exmem.MEM, idex.MEM);

		//set write memory
		BusKt.set(exmem.writeMem, idex.WB);

	}

	@Override
	public void eval() {

	}

	public static void main(String[] args) {

		Processor processor = new Processor();
		var idex = processor.idex;
		var exmem = processor.exmem;
		var memwb = processor.memwb;
		processor.idStage.init();
		processor.exStage.init();





//		TestKt.test("and", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x24,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(14,32));
//			BusKt.set(idex.rtData,BusKt.toBus(14,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});
//
//		TestKt.test("add", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x20,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(12,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(13,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(15,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(16,5));
//			BusKt.set(idex.shiftMa,BusKt.toBus(0,5));
//			BusKt.set(idex.rsData,BusKt.toBus(5,32));
//			BusKt.set(idex.rtData,BusKt.toBus(8,32));
//
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});
//
//		TestKt.test("sub", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x22,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(20,32));
//			BusKt.set(idex.rtData,BusKt.toBus(14,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});
//
//		TestKt.test("or", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x25,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(16,32));
//			BusKt.set(idex.rtData,BusKt.toBus(14,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});

//		TestKt.test("set on less", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x2A,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(16,32));
//			BusKt.set(idex.rtData,BusKt.toBus(14,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});
//
//		TestKt.test("set on less", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x2A,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(16,32));
//			BusKt.set(idex.rtData,BusKt.toBus(16,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});
//
//		TestKt.test("set on less", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
//			BusKt.set(idex.function , BusKt.toBus(0x2A,6));
//			idex.aluSrc.set(false);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(18,32));
//			BusKt.set(idex.rtData,BusKt.toBus(17,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});

//		TestKt.test("load word and store word addi", () -> {
//			BusKt.set(idex.aluOp , BusKt.toBus(0,2));
//			BusKt.set(idex.function , BusKt.toBus(0,6));
//			idex.aluSrc.set(true);
//			BusKt.set(exmem.rtRegister,BusKt.toBus(7,5));
//			BusKt.set(memwb.rdRegister,BusKt.toBus(7,5));
//			BusKt.set(idex.rsRegister,BusKt.toBus(5,5));
//			BusKt.set(idex.rdRegister,BusKt.toBus(6,5));
//
//			BusKt.set(idex.rsData,BusKt.toBus(16,32));
//			BusKt.set(idex.rtData,BusKt.toBus(16,32));
//			BusKt.set(idex.immediate,BusKt.toBus(2,32));
//			idex.eval();
//			exmem.eval();
//			return exmem;
//		});



		TestKt.test("forwarding add", () -> {

			BusKt.set(idex.aluOp , BusKt.toBus(2,2));
			BusKt.set(idex.function , BusKt.toBus(0x20,6));
			idex.aluSrc.set(false);
			idex.regDst.set(true);



			BusKt.set(memwb.rdRegister, BusKt.toBus(7, 5));

			BusKt.set(idex.rtRegister, BusKt.toBus(6, 5));
			idex.rsRegister.get(0).set(true);
			idex.rdRegister.get(0).set(true);

			BusKt.set(idex.shiftMa, BusKt.toBus(0, 5));
			BusKt.set(idex.rsData, BusKt.toBus(5, 32));
			BusKt.set(idex.rtData, BusKt.toBus(8, 32));
			idex.eval();
			exmem.eval();
			idex.eval();
			exmem.eval();

			return exmem;
		});


	}
}
