package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.Multiplexer;
import mipsim.module.TinyModules;
import mipsim.units.ControlUnit;
import mipsim.units.HazardDetectionUnit;
import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.ValueKt;
import sim.test.TestKt;

import static mipsim.sim.InstructionParserKt.parseInstructionToBin;

public class InstructionDecodeStage extends Stage {
	public InstructionDecodeStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		//help for coding
		final var idex = processor.idex;
		final var IDEX = idex.next;
		final var ifid = processor.ifid;
		final var regFile = processor.registerFile;
		final var REGFILE = regFile;
		final var ifStage = processor.ifStage;

		// 32 bit instruction,pc 32 bit
		final var instruction = ifid.instruction;
		final var PC = ifid.pc;

		//split the instruction
		final var opcode = BusKt.slice(instruction, 26, 32);
		//System.out.println(opcode);
		final var rs = BusKt.slice(instruction, 21, 26);
		final var rt = BusKt.slice(instruction, 16, 21);
		final var rd = BusKt.slice(instruction, 11, 16);
		final var shiftMa = BusKt.slice(instruction, 6, 11);
		final var func = BusKt.slice(instruction, 0, 6);

		final var jumpAddress = BusKt.slice(instruction, 0, 26);
		final var immediate = BusKt.slice(instruction, 0, 16);

		// all control unit flag would create
		final var regDst = ValueKt.mut(false);
		final var ALUsrc = ValueKt.mut(false);
		final var memToReg = ValueKt.mut(false);
		final var regWrite = ValueKt.mut(false);
		final var memRead = ValueKt.mut(false);
		final var memWrite = ValueKt.mut(false);
		final var branch = ValueKt.mut(false);
		final var jump = ValueKt.mut(false);

		final var aluOp = BusKt.bus(2);

		ControlUnit.control(opcode, regDst, ALUsrc, memToReg, regWrite
			, memRead, memWrite, branch, jump, aluOp);

		//this will show if hazard would happen and we need stall
		final var ID_EX_memRead = idex.memRead;
		final var ID_EX_registerRt = BusKt.slice(idex.rtRegister, 0, 5);
		//rt ==  IF_ID_registerRt;
		//rs ==  IF_ID_registerRs;
		final var stallFlag = ValueKt.mut(false);
		HazardDetectionUnit.hazardDetectionUnit(ID_EX_memRead, ID_EX_registerRt, rt, rs, stallFlag);

		final var regWriteFinal = ValueKt.mut(false);
		final var memWriteFinal = ValueKt.mut(false);

		Multiplexer.hazardDetection(stallFlag, regWrite, memWrite, regWriteFinal, memWriteFinal);
		//todo debuge


		//register result
		final var rsData = BusKt.bus(32);
		final var rtData = BusKt.bus(32);
		final var immediateValue = HelpersKt.signEx(immediate);

		//this will calculator address of jump and branch

		final var branchAddress = HelpersKt.shift(immediateValue, 2);
		final var finalBranch = BusKt.bus(32);
		TinyModules.easyAdder(PC, branchAddress, finalBranch);

		//todo : some one check this  that would not happen a bug
		final var jumpAddressExtended = BusKt.bus(32);
		BusKt.set(BusKt.slice(jumpAddressExtended, 0, 26), jumpAddress);//extend jump
		BusKt.set(jumpAddressExtended, HelpersKt.shift(jumpAddressExtended, 2));//shifted
		BusKt.set(BusKt.slice(jumpAddressExtended, 28, 32), PC.subList(28, 32));//set the 4 most significant bit


		//we will read data from register
		BusKt.set(REGFILE.readReg1, rs);
		BusKt.set(rsData, regFile.readData1);

		BusKt.set(REGFILE.readReg2, rt);
		BusKt.set(rtData, regFile.readData2);


		//here we set the pipeline


		//set register value
		BusKt.set(IDEX.rsData, rsData);
		BusKt.set(IDEX.rtData, rtData);
		BusKt.set(IDEX.immediate, immediate);

		//set register number
		BusKt.set(IDEX.rtRegister, rt);
		BusKt.set(IDEX.rdRegister, rd);
		BusKt.set(IDEX.rsRegister, rs);
		//setFlag
		IDEX.regDst.set(regDst);
		IDEX.memToReg.set(memToReg);
		IDEX.regWrite.set(regWriteFinal);
		IDEX.memRead.set(memRead);
		IDEX.memWrite.set(memWriteFinal);
		IDEX.aluSrc.set(ALUsrc);
		BusKt.set(IDEX.aluOp, aluOp);


		//set func
		BusKt.set(IDEX.function, func);

		//set shift
		BusKt.set(IDEX.shiftMa, shiftMa);

		//set branch and jump
		BusKt.set(ifStage.branchTarget, finalBranch);
		//todo check it friends
		BusKt.set(ifStage.jumpTarget, jumpAddressExtended);


		ifStage.jump.set(jump);
		ifStage.branch.set(branch);


		//set stall
		ifStage.stall.set(stallFlag);


	}

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String[] args) {
		final var processor = new Processor();
		final var idStage = processor.idStage;
		idStage.init();

		System.out.println("instruction before =" + BusKt.toInt(processor.ifid.instruction));
		System.out.println("pc before =" + BusKt.toInt(processor.ifid.pc));


		TestKt.testOn(processor.idex, "test beq", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("beq $s1,$t1,1");
			var inst = BusKt.toBus(instBin);
			System.out.println(BusKt.toInt(inst));
			//todo it must be check again
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.ifid.eval(time);

			processor.idex.eval(time);
			processor.ifStage.eval(time);
			System.out.println("\n branchTarget: " + processor.ifStage.branchTarget);

		});


		//todo we have some bug in beq or branch
		TestKt.testOn(processor.idex, "test set less than", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("slt $s2,$t7,$5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test shiftR", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("srl $s1,$s3,4");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.ifid.eval(time);
		});


		TestKt.testOn(processor.idex, "test shiftL", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("sll $s1,$t1,6");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test and", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("and $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test or", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("or $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test sub", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("sub $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test addi", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("addi $s1,$zero,5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.ifid.eval(time);
		});


		TestKt.testOn(processor.idex, "test add", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("add $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});


		TestKt.testOn(processor.idex, "test SW", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("sw $t1,6($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});

		TestKt.testOn(processor.idex, "test LW", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("lw $t1,5($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval(time);
		});

		TestKt.testOn(processor.idex, "Jump ", () -> {
			final var time = System.currentTimeMillis();

			var instBin = parseInstructionToBin("j 50");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.ifid.eval(time);
			processor.ifStage.eval(time);
			processor.idex.eval(time);
			System.out.println("jump: " + processor.ifStage.jumpTarget);
			//todo why it's jump don't be update
		});
	}
}
