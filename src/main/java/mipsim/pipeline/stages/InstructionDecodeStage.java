package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.Multiplexer;
import mipsim.module.TinyModules;
import mipsim.units.ControlUnit;
import mipsim.units.HazardDetectionUnit;
import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.ValueKt;
import static mipsim.sim.InstructionParserKt.parseInstructionToBin;
import sim.test.TestKt;

public class InstructionDecodeStage extends Stage {
	public InstructionDecodeStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		//help for coding
		final var ID_EX = processor.idex;
		final var IF_ID = processor.ifid;
		final var REG_FILE = processor.registerFile;
		final var IF_STAGE = processor.ifStage;
		// 32 bit instruction,pc 32 bit
		final var instruction = IF_ID.instruction;
		final var PC = IF_ID.pc;

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
		final var ID_EX_memRead = ID_EX.memRead;
		final var ID_EX_registerRt = BusKt.slice(ID_EX.rtRegister, 0, 5);
		//rt ==  IF_ID_registerRt;
		//rs ==  IF_ID_registerRs;
		final var stallFlag = ValueKt.mut();
		HazardDetectionUnit.hazardDetectionUnit(ID_EX_memRead, ID_EX_registerRt, rt, rs, stallFlag);

		final var regWriteFinal = ValueKt.mut();
		final var memWriteFinal = ValueKt.mut();


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


		final var jumpAddressExtended = BusKt.bus(32);
		BusKt.set(jumpAddressExtended.subList(0, 26), jumpAddress);//extend jump
		BusKt.set(jumpAddressExtended, HelpersKt.shift(jumpAddressExtended, 2));//shifted
		BusKt.set(jumpAddressExtended.subList(28, 32), PC.subList(28, 32));//set the 4 most significant bit


		//we will read data from register
		BusKt.set(REG_FILE.readReg1, rs);
		BusKt.set(rsData, REG_FILE.readData1);

		BusKt.set(REG_FILE.readReg2, rt);
		BusKt.set(rtData, REG_FILE.readData2);


		//here we set the pipeline


		//set register value
		BusKt.set(ID_EX.rsData, rsData);
		BusKt.set(ID_EX.rtData, rtData);
		BusKt.set(ID_EX.immediate, immediate);

		//set register number
		BusKt.set(ID_EX.rtRegister, rt);
		BusKt.set(ID_EX.rdRegister, rd);
		BusKt.set(ID_EX.rsRegister, rs);
		//setFlag
		ID_EX.regDst.set(regDst);
		ID_EX.memToReg.set(memToReg);
		ID_EX.regWrite.set(regWriteFinal);
		ID_EX.memRead.set(memRead);
		ID_EX.memWrite.set(memWriteFinal);
		ID_EX.aluSrc.set(ALUsrc);
		BusKt.set(ID_EX.aluOp, aluOp);


		//set func
		BusKt.set(ID_EX.function, func);

		//set shift
		BusKt.set(ID_EX.shiftMa, shiftMa);

		//set branch and jump
		BusKt.set(IF_STAGE.branchTarget.subList(0, 26), finalBranch);
		//todo check it friends
		BusKt.set(IF_STAGE.jumpTarget, jumpAddressExtended);


		IF_STAGE.jump.set(jump);
		IF_STAGE.branch.set(branch);


		//set stall
		IF_STAGE.stall.set(stallFlag);


	}

	@Override
	public void eval() {

	}


	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String[]args) {
		final var processor = new Processor();
		final var idStage = processor.idStage;
		idStage.init();

		System.out.println("instruction before =" + BusKt.toInt(processor.ifid.instruction));
		System.out.println("pc before =" + BusKt.toInt(processor.ifid.pc));





		TestKt.testOn(processor.idex, "test beq", () -> {
			var instBin = parseInstructionToBin("beq $s1,$t1,6");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

		TestKt.testOn(processor.idex, "test branch", () -> {
			var instBin = parseInstructionToBin("beq $zero,$t1,2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

		TestKt.testOn(processor.idex, "test set less than", () -> {
			var instBin = parseInstructionToBin("slt $s2,$t7,$5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

//		TestKt.testOn(processor.idex, "test shiftR", () -> {
//			var instBin = parseInstructionToBin("slr $s1,$s3,4");
//			var inst = BusKt.toBus(instBin);
//			BusKt.set(processor.ifid.instruction, inst);
//			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
//			processor.ifid.eval();
//		});
		//todo hossain check it's shift right

		TestKt.testOn(processor.idex, "test shiftL", () -> {
			var instBin = parseInstructionToBin("sll $s1,$t1,6");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

		TestKt.testOn(processor.idex, "test and", () -> {
			var instBin = parseInstructionToBin("and $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});


		TestKt.testOn(processor.idex, "test or", () -> {
			var instBin = parseInstructionToBin("or $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});


		TestKt.testOn(processor.idex, "test sub", () -> {
			var instBin = parseInstructionToBin("sub $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});


		TestKt.testOn(processor.idex, "test addi", () -> {
			var instBin = parseInstructionToBin("addi $s1,$zero,5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.ifid.eval();
		});


		TestKt.testOn(processor.idex, "test add", () -> {
			var instBin = parseInstructionToBin("add $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});



		TestKt.testOn(processor.idex, "test SW", () -> {
			var instBin = parseInstructionToBin("sw $t1,6($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

		TestKt.testOn(processor.idex, "test LW", () -> {
			var instBin = parseInstructionToBin("lw $t1,5($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

		TestKt.testOn(processor.idex, "Jump ", () -> {

			var instBin = parseInstructionToBin("j 50");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.idex.eval();
		});

//		System.out.println("instruction next=" + BusKt.toInt(processor.ifid.instruction));
//		System.out.println("new pc next = " + BusKt.toInt(processor.ifid.pc));
//		System.out.println("\n####################################################################\n");
//		System.out.println("rs before = " + BusKt.toInt(processor.idex.rsRegister));
//		System.out.println("rs before = " + BusKt.toInt(processor.idex.rsData));
//		System.out.println("rt before = " + BusKt.toInt(processor.idex.rtRegister));
//		System.out.println("rt data before = " + BusKt.toInt(processor.idex.rtData));
//		System.out.println("rd before = " + BusKt.toInt(processor.idex.rdRegister));
//
//
//		System.out.println("immadiate = " + BusKt.toInt(processor.idex.immediate));
//		System.out.println("aluOp = " + BusKt.toInt(processor.idex.aluOp));
//		System.out.println("mem to reg before = " + processor.idex.memToReg);
//		System.out.println("Write register before= " + (processor.idex.regWrite));
//		System.out.println("aluSrc before =" + (processor.idex.aluSrc));
//		System.out.println("meme to Read befor = " + processor.idex.memRead);
//		System.out.println("meme to write before = " + processor.idex.memWrite);
//		System.out.println("Mem=" + BusKt.toInt(processor.idex.MEM));
//		System.out.println("Exe next=" + BusKt.toInt(processor.idex.EX));
//		System.out.println("func next=" + BusKt.toInt(processor.idex.function));
//		System.out.println("Wb before = " + processor.idex.WB);
//		System.out.println("shiftMa before = " + processor.idex.shiftMa);
//
//		System.out.println("\n----------------------------------------------------------------\n");
//
//		processor.idex.eval();
//
//
//		System.out.println("rs next = " + BusKt.toInt(processor.idex.rsRegister));
//		System.out.println("rs data next = " + BusKt.toInt(processor.idex.rsData));
//		System.out.println("rt next=" + BusKt.toInt(processor.idex.rtRegister));
//		System.out.println("rt data next = " + BusKt.toInt(processor.idex.rtData));
//		System.out.println("rd =" + BusKt.toInt(processor.idex.rdRegister));
//		System.out.println("immadiate =" + BusKt.toInt(processor.idex.immediate));
//		System.out.println("aluOp =" + BusKt.toInt(processor.idex.aluOp));
//		System.out.println("mem to reg next =" + processor.idex.memToReg);
//		System.out.println("Write register next = " + (processor.idex.regWrite));
//		System.out.println("aluSrc next = " + (processor.idex.aluSrc));
//		System.out.println("meme to Read next = " + processor.idex.memRead);
//		System.out.println("meme to write next = " + processor.idex.memWrite);
//		System.out.println("MeM next = " + BusKt.toInt(processor.idex.MEM));
//		System.out.println("Ex next=" + BusKt.toInt(processor.idex.EX));
//		System.out.println("func next=" + BusKt.toInt(processor.idex.function));
//		System.out.println("Wb next = " + processor.idex.WB);
//		System.out.println("shiftMa next = " + processor.idex.shiftMa);
//		System.out.println("Dg reg next = " + processor.idex.regDst);
//
//		System.out.println("\n############################################################################\n");
//
//		System.out.println("before branch = " + processor.ifStage.branch);
//		;
//		System.out.println("before branch traget = " + processor.ifStage.branchTarget);
//		System.out.println("before jump " + processor.ifStage.jump);
//		System.out.println("before stall" + processor.ifStage.stall);
//
//		processor.ifStage.eval();
//		System.out.println("\n--------------------------------------------------------------------\n");
//
//
//		System.out.println("next branch = " + processor.ifStage.branch);
//		;
//		System.out.println("next branch target = " + processor.ifStage.branchTarget);
//		System.out.println("next jump = " + processor.ifStage.jump);
//		System.out.println("next  stall = " + processor.ifStage.stall);
//
//
//		System.out.println("\n###########################################################################\n");


	}
}
