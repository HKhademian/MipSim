package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;
import mipsim.module.Multiplexer;
import mipsim.module.TinyModules;
import mipsim.units.AluControlUnit;
import mipsim.units.ForwardingUnit;
import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.ValueKt;
import sim.complex.MuxKt;
import sim.test.TestKt;

import static sim.base.GateKt.and;
import static sim.base.GateKt.or;


public class ExecutionStage extends Stage {
	public ExecutionStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var idex = processor.idex;
		final var exmem = processor.exmem;
		final var EXMEM = processor.exmem.next;
		final var memwb = processor.memwb;


		final var ifStage = processor.ifStage;

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
		BusKt.set(EXMEM.test2, or(forwardingEx1, forwardingMem1));

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
		var zero = ValueKt.mut(false);
		var aluData = BusKt.bus(32);
		LogicALU.AluInStage(resultOneOfAlu, resultTwoOfAlu, aluOp, idex.shiftMa, aluData,zero);
		BusKt.set(EXMEM.test1, resultOneOfAlu);
		BusKt.set(EXMEM.test2, resultTwoOfAlu);
		BusKt.set(EXMEM.test3, forwardingEx1);
		BusKt.set(EXMEM.test4, forwardingExe2);
		BusKt.set(EXMEM.aluData, aluData);


		//set branch
		var branchAddress = HelpersKt.shift(idex.immediate, 2);
		var finalBranch = BusKt.bus(32);
		TinyModules.easyAdder(idex.PC, branchAddress, finalBranch);

		var branch = ValueKt.mut(false);
		branch.set(and(idex.branch,zero));

		ifStage.branch.set(branch);
		BusKt.set(ifStage.branchTarget, finalBranch);


		//dt register
		var rdRegister = BusKt.bus(5);
		Multiplexer.dtRegister(idex.regDst, idex.rtRegister, idex.rdRegister, rdRegister);
		//set
		BusKt.set(EXMEM.rtRegister, rdRegister);

		//set flags that passed
		BusKt.set(EXMEM.WB, idex.WB);
		BusKt.set(EXMEM.MEM, idex.MEM);

		//set write memory
		BusKt.set(EXMEM.writeMem, idex.WB);
	}


	public static void main(String[] args) {

		TestKt.test("and", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();

			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x24);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 14);
			BusKt.set(idex.next.rtData, 14);
			processor.eval(time);
			return exmem;
		});
		//..
		TestKt.test("add", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();

			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x20);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 12);
			BusKt.set(memwb.next.rdRegister, 13);
			BusKt.set(idex.next.rsRegister, 15);
			BusKt.set(idex.next.rdRegister, 16);
			BusKt.set(idex.next.shiftMa, 0);
			BusKt.set(idex.next.rsData, 5);
			BusKt.set(idex.next.rtData, 8);

			processor.eval(time);
			return exmem;
		});
		//..
		TestKt.test("sub", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x22);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 20);
			BusKt.set(idex.next.rtData, 14);
			processor.eval(time);
			return exmem;
		});
		//..
		TestKt.test("or", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x25);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 16);
			BusKt.set(idex.next.rtData, 14);
			processor.eval(time);
			return exmem;
		});
		//ohh we have some bug
		TestKt.test("set on less", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;

			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.aluOp, 2);
			BusKt.set(idex.function, 0x2A);
			idex.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 16);
			BusKt.set(idex.next.rtData, 14);
			processor.eval(time);
			return exmem;
		});
		//..
		TestKt.test("set on less", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;

			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x2A);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 16);
			BusKt.set(idex.next.rtData, 16);
			processor.eval(time);
			return exmem;
		});
		//..
		TestKt.test("set on less", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.next.aluOp, 2);
			BusKt.set(idex.next.function, 0x2A);
			idex.next.aluSrc.set(false);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 18);
			BusKt.set(idex.next.rtData, 17);
			processor.eval(time);
			return exmem;
		});

		TestKt.test("load word and store word addi", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;
			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;
			processor.init();


			BusKt.set(idex.next.aluOp, 0);
			BusKt.set(idex.next.function, 0);
			idex.next.aluSrc.set(true);
			BusKt.set(exmem.next.rtRegister, 7);
			BusKt.set(memwb.next.rdRegister, 7);
			BusKt.set(idex.next.rsRegister, 5);
			BusKt.set(idex.next.rdRegister, 6);

			BusKt.set(idex.next.rsData, 16);
			BusKt.set(idex.next.rtData, 16);
			BusKt.set(idex.next.immediate, 2);
			processor.eval(time);
			return exmem;
		});



		TestKt.test("forwarding add", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var idex = processor.idex;
			final var exmem = processor.exmem;
			final var memwb = processor.memwb;

			final var IDEX = processor.idex.next;
			final var EXMEM = processor.exmem.next;
			final var MEMWB = processor.memwb.next;
			assert IDEX != null;
			assert EXMEM != null;
			assert MEMWB != null;

			processor.init();

			BusKt.set(IDEX.aluOp, 2);
			BusKt.set(IDEX.function, 0x20);
			IDEX.aluSrc.set(false);
			IDEX.regDst.set(true);

			BusKt.set(MEMWB.rdRegister, 7);

			BusKt.set(IDEX.regWrite, true);

			BusKt.set(IDEX.rtRegister, 6);
			IDEX.rsRegister.get(0).set(true);
			BusKt.set(IDEX.rdRegister, 1);

			BusKt.set(IDEX.shiftMa, 0);
			BusKt.set(IDEX.rsData, 5);
			BusKt.set(IDEX.rtData, 8);

			processor.eval(time);

			processor.eval(time + 1);

			processor.eval(time + 2);

			return exmem;
		});
	}
}
