package mipsim.module;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.complex.MuxKt;

import java.util.List;

import static sim.base.BusKt.ZERO_BUS;
import static sim.complex.MuxKt.mux;
import static sim.complex.MuxKt.mux4;

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
		BusKt.set(result, mux4(forwarding.get(0),forwarding.get(1), regSource, MEM_WB, EXE_MEM, ZERO_BUS));
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
		BusKt.set(result, MuxKt.mux2(regDst, rd, rt));
	}


	/**
	 * we use this mux after pipeline ID/EX
	 * important --> before adding data hazard it happen in next stage but now it happen before of it
	 * aluSrc is only one bit but other have 32 bit
	 * aluSrc 0 --> result = register
	 * aluSrc 1 --> result = immediate
	 */
	public static void readDataFor2Selector(
		Value aluSrc,
		List<? extends Value> register,
		List<? extends Value> immediate,
		List<? extends MutableValue> result
	) {
		BusKt.set(result, MuxKt.mux2(aluSrc, register, immediate));
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
		regWriteResult.set(MuxKt.mux2(hazardDetection, regWrite, Value.ZERO));
		regWriteResult.set(MuxKt.mux2(memWriteResult, memWrite, Value.ZERO));
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
		Value jumpFlag,
		Value branchFlag,
		List<? extends Value> PC,
		List<? extends Value> jump,
		List<? extends Value> branch,
		List<? extends MutableValue> PCSelect
	) {
		var muxRes = mux4(jumpFlag, branchFlag, PC, jump, branch, PC);
		BusKt.set(PCSelect, muxRes);//I put pc for 11 of select
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
		List<? extends Value> memoryResult,
		List<? extends Value> aluResult,
		List<? extends MutableValue> register
	) {
		BusKt.set(register, MuxKt.mux2(memToReg, aluResult, memoryResult));
	}


	/**
	 * this is mux inside of alu for chose the final result
	 * aluControlInput is 4 bit but other are 32 bit
	 * use it inside of alu
	 * aluControlInput = 0000 --> result = and
	 * aluControlInput = 0001 --> result = or
	 * aluControlInput = 0010 --> result = add
	 * aluControlInput = 0011 --> there is not such thing in aluControlInput
	 * aluControlInput = 0100 --> result = shift left
	 * aluControlInput = 0101 --> result = shift right
	 * aluControlInput = 0110 --> result = sub
	 * aluControlInput = 0111 --> result = set on less than
	 * there  is not any aluControlInput which it's most significant is 1
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

	public static void aluSrc(
		Value aluSrc,
		List<? extends Value> rtReg,
		List<? extends Value> imm,
		List<? extends MutableValue> res
	) {
		BusKt.set(res, MuxKt.mux2(aluSrc, rtReg, imm));
	}
}
