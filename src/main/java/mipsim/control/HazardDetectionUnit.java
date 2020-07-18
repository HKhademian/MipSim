package mipsim.control;

import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;
import sim.gates.AndGate;
import sim.gates._dslKt;

import java.util.List;

public class HazardDetectionUnit {
	public static void hazardDetectionUnit(Value ID_EX_memRead, List<Value> ID_EX_registerRt ,
																				 List<Value> IF_ID_registerRt, List<Value> IF_ID_registerRs, MutableValue stallFlag)
	{
		/*
		if(ID_EX_memRead && ( (IF_ID_registerRt == ID_EX_memRead) || (IF_ID_registerRs == ID_EX_memRead) )
		{
			we must stall the pipeline
		}



		 */
		var cmpRTRS = new AndGate(_dslKt.xor(ID_EX_registerRt,IF_ID_registerRs));

		var cmpRTRT = new AndGate(_dslKt.xor(ID_EX_registerRt,IF_ID_registerRt));

		var isSourceNeedMem = new AndGate(cmpRTRS,cmpRTRT);

		stallFlag.set(new Variable((new AndGate(ID_EX_memRead,isSourceNeedMem)).get(),""));

	}
}
