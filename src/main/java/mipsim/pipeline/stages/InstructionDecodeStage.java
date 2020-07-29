package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.Multiplexer;
import mipsim.units.ControlUnit;
import mipsim.units.HazardDetectionUnit;
import sim.base.BusKt;
import sim.base.MathKt;
import sim.base.MutableValue;
import sim.base.ValueKt;
import sim.tool.TestKt;

import java.util.List;

import static mipsim.sim.ParserKt.parseInstructionToBin;

public class InstructionDecodeStage extends Stage {
	public InstructionDecodeStage(final Processor processor) {
		super(processor);
	}

	public final MutableValue jumpFlag = ValueKt.mut(false);
	public final MutableValue stallFlag = ValueKt.mut(false);
	public final List<? extends MutableValue> jumpAddress = BusKt.bus(32);

	@Override
	public void init() {
		//final var ifStage = processor.ifStage; //todo check we need to change ifStage
		//help for coding
		final var idex = processor.idex;
		final var ifid = processor.ifid;
		final var regFile = processor.registerFile;

		final var IDEX = idex.next;
		final var REGFILE = regFile;

		assert IDEX != null;
		assert REGFILE != null;

		// 32 bit instruction,pc 32 bit
		final var instruction = ifid.instruction;
		final var pc = ifid.pc;

		//split the instruction
		final var opcode = BusKt.slice(instruction, 26, 32);
		//System.out.println(opcode);
		final var rs = BusKt.slice(instruction, 21, 26);
		final var rt = BusKt.slice(instruction, 16, 21);
		final var rd = BusKt.slice(instruction, 11, 16);
		final var shiftMa = BusKt.slice(instruction, 6, 11);
		final var func = BusKt.slice(instruction, 0, 6);

		final var jumpAddress26 = BusKt.slice(instruction, 0, 26);
		final var immediate = BusKt.slice(instruction, 0, 16);

		// all control unit flag would create
		final var regDst = ValueKt.mut(false);
		final var ALUsrc = ValueKt.mut(false);
		final var memToReg = ValueKt.mut(false);
		final var regWrite = ValueKt.mut(false);
		final var memRead = ValueKt.mut(false);
		final var memWrite = ValueKt.mut(false);
		final var branch = ValueKt.mut(false);
		final var bne = ValueKt.mut(false);
		final var shift16 = ValueKt.mut(false);

		final var aluOp = BusKt.bus(2);

		ControlUnit.controlPlus(opcode, regDst, ALUsrc, memToReg, regWrite
			, memRead, memWrite, branch, jumpFlag,bne,shift16, aluOp);

		//this will show if hazard would happen and we need stall
		final var ID_EX_memRead = idex.memRead;
		final var ID_EX_registerRt = BusKt.slice(idex.rtRegister, 0, 5);
		//rt ==  IF_ID_registerRt;
		//rs ==  IF_ID_registerRs;
		HazardDetectionUnit.hazardDetectionUnit(ID_EX_memRead, ID_EX_registerRt, rt, rs, stallFlag);

		final var regWriteFinal = ValueKt.mut(false);
		final var memWriteFinal = ValueKt.mut(false);

		Multiplexer.hazardDetection(stallFlag, regWrite, memWrite, regWriteFinal, memWriteFinal);
		//todo debug

		//register result
		final var rsData = BusKt.bus(32);
		final var rtData = BusKt.bus(32);

		//todo : some one check this  that would not happen a bug
		BusKt.set(BusKt.slice(jumpAddress, 0, 2), 0);
		BusKt.set(BusKt.slice(jumpAddress, 2, 28), jumpAddress26);//extend jump
		BusKt.set(BusKt.slice(jumpAddress, 28, 32), BusKt.slice(pc, 28, 32));//set the 4 most significant bit

		//we will read data from register
		BusKt.set(REGFILE.readReg1, rs);
		BusKt.set(rsData, regFile.readData1);

		BusKt.set(REGFILE.readReg2, rt);
		BusKt.set(rtData, regFile.readData2);

		//here we set the pipeline

		//set register value
		BusKt.set((List) IDEX.rsData, rsData);
		BusKt.set((List) IDEX.rtData, rtData);
		BusKt.set((List) IDEX.immediate, MathKt.signEx(immediate));

		//set register number
		BusKt.set((List) IDEX.rtRegister, rt);
		BusKt.set((List) IDEX.rdRegister, rd);
		BusKt.set((List) IDEX.rsRegister, rs);
		//setFlag
		BusKt.set((MutableValue) IDEX.regDst, regDst);
		BusKt.set((MutableValue) IDEX.memToReg, memToReg);
		BusKt.set((MutableValue) IDEX.regWrite, regWriteFinal);
		BusKt.set((MutableValue) IDEX.memRead, memRead);
		BusKt.set((MutableValue) IDEX.memWrite, memWriteFinal);
		BusKt.set((MutableValue) IDEX.aluSrc, ALUsrc);
		BusKt.set((MutableValue) IDEX.branch, branch);
		BusKt.set((MutableValue) IDEX.bne, bne);
		BusKt.set((MutableValue) IDEX.shift16, shift16);
		BusKt.set((List) IDEX.aluOp, aluOp);

		//set pc for branch
		BusKt.set((List) IDEX.PC, pc);
		//set func
		BusKt.set((List) IDEX.function, func);

		//set shift
		BusKt.set((List) IDEX.shiftMa, shiftMa);

		//DEBUG
		BusKt.set((List) IDEX.pc, ifid.pc);
		BusKt.set((List) IDEX.instruction, ifid.instruction);
	}

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String[] args) {

		TestKt.test("test beq", () -> {
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();

			var instBin = parseInstructionToBin("beq $s1,$t1,1");
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});


		TestKt.test("test set less than", () -> {
			var instBin = parseInstructionToBin("slt $s2,$t7,$5");
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});

//
		TestKt.test("test shiftR", () -> {

			var instBin = parseInstructionToBin("srl $s1,$s3,4");
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;

		});

//
		TestKt.test("test shiftL", () -> {

			var instBin = parseInstructionToBin("sll $s1,$t1,6");
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;

		});


		TestKt.test("test and", () -> {
			var instBin = parseInstructionToBin("and $s1,$t1,$t2");
			final var time = System.currentTimeMillis();
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;

		});
//
//
		TestKt.test("test or", () -> {
			final var time = System.currentTimeMillis();
			var instBin = parseInstructionToBin("or $s1,$t1,$t2");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
//
//
		TestKt.test("test sub", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("sub $s1,$t1,$t2");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
//
//
		TestKt.test("test addi", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("addi $s1,$zero,5");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
//
//
		TestKt.test("test add", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("add $s1,$t1,$t2");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
//
//
		TestKt.test("test SW", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("sw $t1,6($t2)");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
//
		TestKt.test("test LW", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("lw $t1,5($t2)");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});

		TestKt.test("Jump ", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("j 50");
			final var processor = new Processor();
			final var IFID = processor.ifid.next;
			final var ifid = processor.ifid;
			final var IDEX = processor.idex.next;
			final var idex = processor.idex;

			assert IFID != null;
			assert IDEX != null;
			processor.init();
			var inst = BusKt.toBus(instBin);

			BusKt.set((List) IFID.instruction, inst);
			processor.eval(time);
			processor.eval(time);
			return IDEX;
		});
	}
}
