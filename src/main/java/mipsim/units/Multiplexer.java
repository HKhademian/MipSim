package mipsim.units;

import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public class Multiplexer {


	/**
	 *  we use aluInput and dtValue two time but we use all other of them only one time
	 *
	 */




	public static void aluInput(List<Value> forwarding, List<Value> regSource,
															List<Value> EXE_MEM, List<Value> MEM_WB, List<MutableValue> result)
	{

		/**
		 * we need use to of this before of alu to avoid from data hazard
		 * forward has 2 bit and other have 32 bit
		 * forwarding == 00 -> result = regSource
		 * forwarding == 10 -> result = EXE_MEM
		 * forwarding == 01 -> result = MEM_WB
		 */

	}




	public static void dtRegister(Value regDst,List<Value> rt ,List<Value> rd,List<Value> result)
	{
		/**
		 * we use it two time
		 * this mux will implement before regFile and one after ID/EX to chose the register that will be writeBackRegister
		 * alu src has one bit and other have 5 bit
		 * regDst == 0 --> result = rd
		 * regDst == 1 --> result = rt
		 */
	}




	public static void readDataFor2Selector(Value aluSrc,List<Value> register ,List<Value> immediate,List<Value> result)
	{
		/**
		 * we use this mux after regFile and before the pipeline stage
		 * important --> before adding data hazard it happen in next stage but now it happen before of it
		 * aluSrc is only one bit but other have 32 bit
		 * aluSrc 0 --> result = register
		 * aluSrc 1 --> result = immediate
		 */
	}




	public static void hazardDetection(Value hazardDetection,Value regWrite,Value memWrite,MutableValue regWriteResult,MutableValue  memWriteResult)
	{
		/**
		 * hazardDetection implement before pipeline ID/EX if one of source need use memory but it is in stage EX we stall one operation to reach that ppoint
		 * naturally we convert all control flag to 0 to stop it but now we can only use the register that change our states and with knowledge that branch happen in stage ID
		 * we use to mux here
		 * hazardDetection = 0 --> regWriteResult = regWrite,regWriteResult = regWrite
		 * hazardDetection = 1 --> regWriteResult = 0 ,regWriteResult = 0
		 */
	}



	/**
	 *
	 *this is concat of two multiplexer of jump and branch
	 */
	public static void pcChoice(Value jumpFlag,Value branchFlag,List<Value> PC,
															List<Value> jump,List<Value> branch,List<MutableValue> PCSelect)
	{
		/**
		 * this will be before PC
		 * all except branchFlag and jumpFlag are 32 bit
		 * we assume our mux bit selector be like (branch-->second [1], jumpFlag-->first [0])
		 * (branchFlag jumpFlag) 00 --> PCSelect = PC
		 * (branchFlag jumpFlag) 01 --> PCSelect = jump
		 * (branchFlag jumpFlag) 10 --> PCSelect = branch
		 * (branchFlag jumpFlag) 11 --> because branch and jump will both happen in ID stage this is an absolute bug
		 */
	}




	public static void writeBackValue(Value memToReg,List<Value> memoryResult,List<Value> aluResult,List<Value> register)
	{
		/**
		 * this mux will be WB exactly after EX/WB
		 * this will select between result that get from ALU or result of memory
		 * all @param of them except memToReg are 32 bit
		 * memToReg = 0 --> register = aluResult
		 * memToReg = 1 --> register = memoryResult
		 */
	}




	public static void aluResult(List<Value> aluControlInput,List<Value> add
															,List<Value> sub,List<Value> and, List<Value> or
															,List<Value> setOnLessThan,List<Value> shiftLogicalLeft
															,List<Value> shiftLogicalRight,List<Value> result)
	{
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
	}



}
