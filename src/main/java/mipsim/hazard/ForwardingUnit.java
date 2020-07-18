package mipsim.hazard;

import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public class ForwardingUnit {
	public static void forwardingUnitEXhazard(Value EX_MEM_RegWrite, List<Value> EX_MEM_RegisterRd,
																						List<Value> EX_MEM_RegisterSource, List<MutableValue> forwardingFlag)
	{

	}

	public static void forwardingUnitMEMhazard(Value MEM_WB_RegWrite,List<Value> MEM_WB_RegisterRd,
																						 List<Value> MEM_WB_RegisterSource,List<Value> EX_MEM_RegisterRd
																							,List<MutableValue> forwardingFlag)
	{

	}



}
