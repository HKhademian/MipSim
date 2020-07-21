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


// 32 bit instruction,pc 32 bit
		var instruction = simulator.ifid.instruction;
		var PC = simulator.ifid.pc;

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
		var regWrite = ValueKt.mut(false), ;
		var memRead = ValueKt.mut(false);
		var memWrite = ValueKt.mut(false);
		var branch = ValueKt.mut(false);
		var jump = ValueKt.mut(false);

		var aluOp = BusKt.bus(2);

		ControlUnit.control(opcode, regDst, ALUsrc, memToReg, regWrite
			, memRead, memWrite, branch, jump, aluOp);

		//this will show if hazard would happen and we need stall
		var hazardDetection = ValueKt.mut();
		var ID_EX_memRead = simulator.idex.memRead;
		var ID_EX_registerRt = BusKt.slice(simulator.idex.rtRegister, 0, 5);
		//rt ==  IF_ID_registerRt;
		//rs ==  IF_ID_registerRs;
		var stallFlag = ValueKt.mut();
		HazardDetectionUnit.hazardDetectionUnit(memRead, ID_EX_registerRt, rt, rs, stallFlag);

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
		BusKt.set(simulator.registerFile.readReg1, rs);
		BusKt.set(rsData, simulator.registerFile.readData1);

		BusKt.set(simulator.registerFile.readReg1, rt);
		BusKt.set(rtData, simulator.registerFile.readData1);


		//here we set the pipeline


		//set register value
		BusKt.set(simulator.idex.rsData, rsData);
		BusKt.set(simulator.idex.rtData, rtData);
		BusKt.set(simulator.idex.immediate, immediate);

		//set register number
		BusKt.set(simulator.idex.rtRegister, rt);
		BusKt.set(simulator.idex.rdRegister, rd);
		BusKt.set(simulator.idex.rsRegister, rs);
		//setFlag
		simulator.idex.memToReg.set(regDst);
		simulator.idex.memToReg.set(memToReg);
		simulator.idex.regWrite.set(regWriteFinal);
		simulator.idex.memRead.set(memRead);
		simulator.idex.memWrite.set(memWriteFinal);
		simulator.idex.aluSrc.set(ALUsrc);
		BusKt.set(simulator.idex.aluOp, aluOp);


		//set func
		BusKt.set(simulator.idex.function, func);

		//set shift
		BusKt.set(simulator.idex.shiftMa, shiftMa);

		//set branch and jump
		BusKt.set(simulator.ifStage.branchTarget.subList(0, 26), finalBranch);
		BusKt.set(simulator.ifStage.jumpTarget, jumpAddressExtended);


		simulator.ifStage.jump.set(jump);
		simulator.ifStage.branch.set(branch);


		//set stall
		simulator.ifStage.stall.set(stallFlag);

	}

	@Override
	public void eval() {

	}
}
