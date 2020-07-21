package mipsim.pipeline;

import mipsim.Simulator;
import mipsim.module.TinyModules;
import mipsim.units.ControlUnit;
import mipsim.units.HazardDetectionUnit;
import mipsim.units.Multiplexer;
import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.ValueKt;

import java.util.List;

public class InstructionDecodeStage extends Stage {
	public InstructionDecodeStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
	//help for coding
		var ID_EX = simulator.idex;
		var IF_ID = simulator.ifid;
		var REG_FILE =simulator.registerFile;
		var IF_STAGE = simulator.ifStage;
// 32 bit instruction,pc 32 bit
		var instruction = IF_ID.instruction;
		var PC = IF_ID.pc;

		//split the instruction
		var opcode = BusKt.slice(instruction, 26, 32);
		var rs = BusKt.slice(instruction, 21, 26);
		var rd = BusKt.slice(instruction, 16, 21);
		var rt = BusKt.slice(instruction, 11, 16);
		var shiftMa = BusKt.slice(instruction, 6, 11);
		var func = BusKt.slice(instruction, 0, 6);
		var jumpAddress = BusKt.slice(instruction, 0, 26);
		var immediate = BusKt.slice(instruction, 0, 16);

		// all control unit flag would create
		var regDst = ValueKt.mut(false);
		var ALUsrc = ValueKt.mut(false);
		var memToReg = ValueKt.mut(false);
		var regWrite = ValueKt.mut(false);
		var memRead = ValueKt.mut(false);
		var memWrite = ValueKt.mut(false);
		var branch = ValueKt.mut(false);
		var jump = ValueKt.mut(false);

		var aluOp = BusKt.bus(2);

		ControlUnit.control(opcode, regDst, ALUsrc, memToReg, regWrite
			, memRead, memWrite, branch, jump, aluOp);

		//this will show if hazard would happen and we need stall
		var ID_EX_memRead = ID_EX.memRead;
		var ID_EX_registerRt = BusKt.slice(ID_EX.rtRegister, 0, 5);
		//rt ==  IF_ID_registerRt;
		//rs ==  IF_ID_registerRs;
		var stallFlag = ValueKt.mut();
		HazardDetectionUnit.hazardDetectionUnit(ID_EX_memRead, ID_EX_registerRt, rt, rs, stallFlag);

		var regWriteFinal = ValueKt.mut();
		var memWriteFinal = ValueKt.mut();


		Multiplexer.hazardDetection(stallFlag, regWrite, memWrite, regWriteFinal, memWriteFinal);


		//register result
		var rsData = BusKt.bus(32);
		var rtData = BusKt.bus(32);
		var immediateValue = HelpersKt.signEx(immediate);

		//this will calculator address of jump and branch

		var branchAddress = HelpersKt.shift(immediate, 2);
		var finalBranch = BusKt.bus(32);
		TinyModules.easyAdder(PC, branchAddress, finalBranch);


		var jumpAddressExtended = BusKt.bus(32);
		BusKt.set(jumpAddressExtended.subList(0, 26), jumpAddress);//extend jump
		BusKt.set(jumpAddressExtended, HelpersKt.shift(jumpAddressExtended, 2));//shifted
		BusKt.set(jumpAddressExtended.subList(28, 32), PC.subList(28, 32));//set the 4 most significant bit


		//we will read data from register
		BusKt.set(REG_FILE.readReg1, rs);
		BusKt.set(rsData, REG_FILE.readData1);

		BusKt.set(REG_FILE.readReg1, rt);
		BusKt.set(rtData, REG_FILE.readData1);


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
}
