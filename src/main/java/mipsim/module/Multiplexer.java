package mipsim.module;

import sim.base.*;
import sim.complex.MuxKt;

import java.util.List;

import static sim.base.BusKt.ZERO_BUS;
import static sim.base.GateKt.*;
import static sim.complex.MuxKt.*;

public final class Multiplexer {
	private Multiplexer() {
	}


	/**
	 * we use aluInput and dtValue two time but we use all other of them only one time
	 * <p>
	 * we need use to of this before of alu to avoid from data hazard
	 * note -->reg source select between immediate and reg Value-->this mux should happen after "readDataFor2Selector"
	 * forward has 2 bit and other have 32 bit
	 * forwarding == 00 -> result = regSource
	 * forwarding == 10 -> result = EXE_MEM
	 * forwarding == 01 -> result = MEM_WB
	 */
	public static void aluInput(
		List<? extends Value> forwarding,
		List<? extends Value> regSource,
		List<? extends Value> EXE_MEM,
		List<? extends Value> MEM_WB,
		List<? extends MutableValue> result
	) {
		BusKt.set(result, mux(forwarding, regSource, MEM_WB, EXE_MEM, ZERO_BUS));
	}


	/**
	 * we use it two time
	 * this mux will implement before regFile and one after ID/EX to chose the register that will be writeBackRegister
	 * alu src has one bit and other have 5 bit
	 * regDst == 0 --> result = rd
	 * regDst == 1 --> result = rt
	 */
	public static void dtRegister(
		Value regDst,
		List<? extends Value> rt,
		List<? extends Value> rd,
		List<? extends MutableValue> result
	) {
		BusKt.set(result, mux2(regDst, rt, rd));
	}


	/**
	 * we use this mux for select jump or branch in ex
	 * if its Jr so we must accept rs so jump flag must be one
	 * and we set flag of jump either it is branch or jump
	 * remember that you must add pc +4 + immediate to create branch never forgot that
	 *
	 */
	public static void selectBranchPlus(
		Value branch,
		Value zero,
		Value bne,
		Value jump,
		List<? extends Value> branchAddress,
		List<? extends Value> rs,
		MutableValue flagJump,
		List<? extends MutableValue> result
	) {

		flagJump.set(or(jump,and(branch,xor(bne,zero))));
		BusKt.set(result,MuxKt.mux2(jump,branchAddress,MathKt.shift(rs,2)));

	}

	/**
	 * this function added for sui lui
	 * for 16 bit shift if flag is on
	 *
	 *
	 *
	 */

	public static void shiftImi16(
		Value shift16,
		List<? extends Value> immediate,
		List<? extends MutableValue> result
	) {

		BusKt.set(result,MuxKt.mux2(shift16,immediate, MathKt.shift(immediate, 16)));

	}

	public static void changeR_type(
		Value jump,
		Value write,
		MutableValue result
	) {

		result.set(MuxKt.mux2(jump,write,Value.ZERO));

	}


	/**
	 * hazardDetection implement before pipeline ID/EX if one of source need use memory but it is in stage EX we stall one operation to reach that ppoint
	 * naturally we convert all control flag to 0 to stop it but now we can only use the register that change our states and with knowledge that branch happen in stage ID
	 * we use to mux here
	 * hazardDetection = 0 --> regWriteResult = regWrite,regWriteResult = regWrite
	 * hazardDetection = 1 --> regWriteResult = 0 ,regWriteResult = 0
	 */
	public static void hazardDetection(
		Value hazardDetection,
		Value regWrite,
		Value memWrite,
		MutableValue regWriteResult,
		MutableValue memWriteResult
	) {
		regWriteResult.set(mux2(hazardDetection, regWrite, Value.ZERO));
		memWriteResult.set(mux2(hazardDetection, memWrite, Value.ZERO));
	}


	/**
	 * this is concat of two multiplexer of jump and branch
	 * <p>
	 * this will be before PC
	 * all except branchFlag and jumpFlag are 32 bit
	 * we assume our mux bit selector be like (branch-->second [1], jumpFlag-->first [0])
	 * (branchFlag jumpFlag) 00 --> PCSelect = PC
	 * (branchFlag jumpFlag) 01 --> PCSelect = jump
	 * (branchFlag jumpFlag) 10 --> PCSelect = branch
	 * (branchFlag jumpFlag) 11 --> because branch and jump will both happen in ID stage this is an absolute bug
	 */
	public static void pcChoice(
		Value stallFlag,
		Value jumpFlag,
		Value branchFlag,
		List<? extends Value> pc4,
		List<? extends Value> jump,
		List<? extends Value> branch,
		List<? extends Value> pc,
		List<? extends MutableValue> PCSelect
	) {
		var muxRes = mux4(jumpFlag, branchFlag, pc4, jump, branch, branch);
		var muxFinal = mux2(stallFlag, muxRes, pc);
		BusKt.set(PCSelect, muxFinal);//I put pc for 11 of select
	}


	/**
	 * this mux will be WB exactly after EX/WB
	 * this will select between result that get from ALU or result of memory
	 * all @param of them except memToReg are 32 bit
	 * memToReg = 0 --> register = aluResult
	 * memToReg = 1 --> register = memoryResult
	 */
	public static void writeBackValue(
		Value memToReg,
		List<? extends Value> aluResult,
		List<? extends Value> memoryResult,
		List<? extends MutableValue> register
	) {
		BusKt.set(register, mux2(memToReg, aluResult, memoryResult));
	}


	/**
	 * this is mux inside of alu for chose the final result
	 * aluControlInput is 4 bit but other are 32 bit
	 * use it inside of alu
	 * aluControlInput = 0000 --> result = and
	 * aluControlInput = 0001 --> result = or
	 * aluControlInput = 0010 --> result = add
	 * aluControlInput = 0011 --> xor
	 * aluControlInput = 0100 --> result = shift left
	 * aluControlInput = 0101 --> result = shift right
	 * aluControlInput = 0110 --> result = sub
	 * aluControlInput = 0111 --> result = set on less than
	 * aluControlInput = 1000 --> result = nor
	 */
	public static void aluResult(
		List<? extends Value> aluControlInput,
		List<? extends Value> add,
		List<? extends Value> sub,
		List<? extends Value> and,
		List<? extends Value> or,
		List<? extends Value> setOnLessThan,
		List<? extends Value> shiftLogicalLeft,
		List<? extends Value> shiftLogicalRight,
		List<? extends MutableValue> result
	) {
		var muxResult = mux(aluControlInput, and, or, add, ZERO_BUS, shiftLogicalLeft, shiftLogicalRight, sub, setOnLessThan,
			ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS);

		BusKt.set(result, muxResult);
	}

	public static void aluResultPlus(
		List<? extends Value> aluControlInput,
		List<? extends Value> add,
		List<? extends Value> sub,
		List<? extends Value> and,
		List<? extends Value> or,
		List<? extends Value> nor,
		List<? extends Value> xor,
		List<? extends Value> setOnLessThan,
		List<? extends Value> shiftLogicalLeft,
		List<? extends Value> shiftLogicalRight,
		List<? extends MutableValue> result
	) {
		var muxResult = mux(aluControlInput, and, or, add, xor, shiftLogicalLeft, shiftLogicalRight, sub, setOnLessThan,
			nor, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS, ZERO_BUS);

		BusKt.set(result, muxResult);
	}

	public static void aluSrc(
		Value aluSrc,
		List<? extends Value> rtReg,
		List<? extends Value> imm,
		List<? extends MutableValue> res
	) {
		BusKt.set(res, MuxKt.mux2(aluSrc, rtReg, imm));
	}

	public static void main(String[] args) {

//		var branch = ValueKt.mut(true);
//		var zero = ValueKt.mut(false);
//		var bne = ValueKt.mut(true);
//		var jump = ValueKt.mut(false);
//		var branchAddress = BusKt.toBus(100,32);
//		var jumpAddress = BusKt.toBus(200,32);
//		var flagJump = ValueKt.mut(false);;
//		var result = BusKt.bus(32);
//
//
//		selectBranchPlus(branch,zero,bne,jump,branchAddress,jumpAddress,flagJump,result);
//		System.out.println(BusKt.toInt(result)+"          "+flagJump);

//		var immediate = BusKt.toBus(1,32);
//		var shift16 = ValueKt.mut(false);
//		var save = BusKt.bus(32);
//		Multiplexer.shiftImi16(shift16,immediate,save);
//		System.out.println(BusKt.toInt(save));

	}


}
