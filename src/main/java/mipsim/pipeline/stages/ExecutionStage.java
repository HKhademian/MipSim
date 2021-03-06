package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;
import mipsim.module.Multiplexer;
import mipsim.module.TinyModules;
import mipsim.units.AluControlUnit;
import mipsim.units.ForwardingUnit;
import sim.base.*;
import sim.tool.TestKt;

import java.util.List;

import static sim.base.GateKt.and;
import static sim.base.GateKt.or;


@SuppressWarnings({"unchecked", "rawtypes"})
public class ExecutionStage extends Stage {

	public final MutableValue branchFlag = ValueKt.mut(false);
	public final List<? extends MutableValue> branchAddress = BusKt.bus(32);

	public ExecutionStage(final Processor processor) {
		super(processor);
	}

	public List<? extends MutableValue> forwardingEx1 = BusKt.bus(2);
	public List<? extends MutableValue> forwardingEx2 = BusKt.bus(2);
	public List<? extends MutableValue> forwardingMem1 = BusKt.bus(2);
	public List<? extends MutableValue> forwardingMem2 = BusKt.bus(2);

	@Override
	public void init() {
		final var ifStage = processor.ifStage;
		final var wbStage = processor.wbStage;
		final var idex = processor.idex;
		final var exmem = processor.exmem;
		final var memwb = processor.memwb;

		final var EXMEM = processor.exmem.next;
		assert EXMEM != null;

		//alu control unit
		final var aluOp = BusKt.bus(4);
		final var jumpReg = ValueKt.mut(false);
		AluControlUnit.aluControlUnitPlus(idex.aluOp, idex.function,jumpReg, aluOp);

		//first alu src
		final var resultOneOfAlu = BusKt.bus(32);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rsRegister, forwardingEx1);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite, memwb.rdRegister, idex.rsRegister, exmem.regWrite, exmem.rtRegister, forwardingMem1);
		Multiplexer.aluInput(or(forwardingEx1, forwardingMem1), idex.rsData, exmem.aluData, wbStage.writeData, resultOneOfAlu);

		//second alu  src
		final var forwardingResult2 = BusKt.bus(32);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rtRegister, forwardingEx2);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite, memwb.rdRegister, idex.rtRegister, exmem.regWrite, exmem.rtRegister, forwardingMem2);
		Multiplexer.aluInput(or(forwardingEx2, forwardingMem2), idex.rtData, exmem.aluData, wbStage.writeData, forwardingResult2);

		//set write memory
		assert false; //value to write
		BusKt.set((List) EXMEM.writeMem, forwardingResult2);

		final var immediateFinal = BusKt.bus(32);
		Multiplexer.shiftImi16(idex.shift16,idex.immediate,immediateFinal);

		final var resultTwoOfAlu = BusKt.bus(32);
		Multiplexer.aluSrc(idex.aluSrc, forwardingResult2, immediateFinal, resultTwoOfAlu);

		//alu result
		final var zero = ValueKt.mut(false);
		final var aluData = BusKt.bus(32);
		LogicALU.AluInStagePlus(resultOneOfAlu, resultTwoOfAlu, aluOp, idex.shiftMa, aluData, zero);
		BusKt.set((List) EXMEM.aluData, aluData);


		//set branch
		final var shiftAddress = MathKt.shift(idex.immediate, 2);
		final var branchLocation =BusKt.bus(32);
		TinyModules.easyAdder(idex.PC, shiftAddress, branchLocation);

		// here we select location to jump (note rs will shift 2 bit to left)
		Multiplexer.selectBranchPlus(idex.branch,zero,idex.bne,jumpReg,branchLocation,idex.rsData,branchFlag,branchAddress);


		// if it's R type the reg write is true but if it is jr it must be false
		final var regWriteFinal = ValueKt.mut(false);
		Multiplexer.changeR_type(jumpReg,idex.regWrite,regWriteFinal);






		//dt register
		final var rdRegister = BusKt.bus(5);
		Multiplexer.dtRegister(idex.regDst, idex.rtRegister, idex.rdRegister, rdRegister);
		//set
		BusKt.set((List) EXMEM.rtRegister, rdRegister);

		//set flags that passed
		BusKt.set((MutableValue) EXMEM.regWrite, regWriteFinal);
		BusKt.set((MutableValue) EXMEM.memToReg, idex.memToReg);
		BusKt.set((List) EXMEM.MEM, idex.MEM);

		//DEBUG
		BusKt.set((List) EXMEM.pc, idex.pc);
		BusKt.set((List) EXMEM.instruction, idex.instruction);

	}


	public static void main(String[] args) {
		TestKt.test("and", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x24);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 14);
			BusKt.set((List) IDEX.rtData, 14);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("add", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x20);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 12);
			BusKt.set((List) MEMWB.rdRegister, 13);
			BusKt.set((List) IDEX.rsRegister, 15);
			BusKt.set((List) IDEX.rdRegister, 16);
			BusKt.set((List) IDEX.shiftMa, 0);
			BusKt.set((List) IDEX.rsData, 5);
			BusKt.set((List) IDEX.rtData, 8);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("sub", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x22);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 20);
			BusKt.set((List) IDEX.rtData, 14);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("or", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x25);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 16);
			BusKt.set((List) IDEX.rtData, 14);

			processor.eval(System.nanoTime());
			return exmem;
		});

		// todo: explain why
		//ohh we have some bug
		TestKt.test("set on less", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x2A);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 16);
			BusKt.set((List) IDEX.rtData, 14);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("set on less", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x2A);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 16);
			BusKt.set((List) IDEX.rtData, 16);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("set on less", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x2A);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 18);
			BusKt.set((List) IDEX.rtData, 17);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("load word and store word addi", () -> {
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

			BusKt.set((List) IDEX.aluOp, 0);
			BusKt.set((List) IDEX.function, 0);
			BusKt.set((MutableValue) IDEX.aluSrc, true);
			BusKt.set((List) EXMEM.rtRegister, 7);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((List) IDEX.rsRegister, 5);
			BusKt.set((List) IDEX.rdRegister, 6);
			BusKt.set((List) IDEX.rsData, 16);
			BusKt.set((List) IDEX.rtData, 16);
			BusKt.set((List) IDEX.immediate, 2);

			processor.eval(System.nanoTime());
			return exmem;
		});

		TestKt.test("forwarding add", () -> {
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

			BusKt.set((List) IDEX.aluOp, 2);
			BusKt.set((List) IDEX.function, 0x20);
			BusKt.set((MutableValue) IDEX.aluSrc, false);
			BusKt.set((MutableValue) IDEX.regDst, true);
			BusKt.set((List) MEMWB.rdRegister, 7);
			BusKt.set((MutableValue) IDEX.regWrite, true);
			BusKt.set((List) IDEX.rtRegister, 6);
			BusKt.set((List) IDEX.rsRegister, 1); // todo: is it true?
			BusKt.set((List) IDEX.rdRegister, 1);
			BusKt.set((List) IDEX.shiftMa, 0);
			BusKt.set((List) IDEX.rsData, 5);
			BusKt.set((List) IDEX.rtData, 8);

			processor.eval(System.nanoTime());
			processor.eval(System.nanoTime());
			processor.eval(System.nanoTime());

			return exmem;
		});
	}
}
