package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.TinyModules;
import mipsim.units.ControlUnit;
import mipsim.units.HazardDetectionUnit;
import mipsim.module.Multiplexer;
import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.ValueKt;

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
		final var rs = BusKt.slice(instruction, 21, 26);
		final var rd = BusKt.slice(instruction, 16, 21);
		final var rt = BusKt.slice(instruction, 11, 16);
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


		//register result
		final var rsData = BusKt.bus(32);
		final var rtData = BusKt.bus(32);
		final var immediateValue = HelpersKt.signEx(immediate);

		//this will calculator address of jump and branch

		final var branchAddress = HelpersKt.shift(immediate, 2);
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
		ID_EX.memToReg.set(regDst);
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
	public static void main(final String... args) {
		final var processor = new Processor();

		BusKt.set(processor.ifid.instruction, BusKt.toBus(4294967295l, 32));
		BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
		System.out.println("instruction =" + BusKt.toInt(processor.ifid.instruction));
		System.out.println("pc =" + BusKt.toInt(processor.ifid.pc));
		processor.ifid.eval();
		processor.idex.eval();
		System.out.println("instruction =" + BusKt.toInt(processor.ifid.instruction));
		System.out.println("new pc= " + BusKt.toInt(processor.ifid.pc));

		//processor.ifStage.eval();
		//processor.idStage.eval();
		System.out.println("pc shift ="+processor.ifStage.jump);


		System.out.println("func =" + BusKt.toInt(processor.idex.function));
		System.out.println("rt =" + BusKt.toInt(processor.idex.rsRegister));


	}
}
