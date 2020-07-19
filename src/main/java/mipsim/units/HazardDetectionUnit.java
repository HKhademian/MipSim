package mipsim.units;

import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

import static sim.gates.GatesKt.and;
import static sim.gates.GatesKt.xor;

public final class HazardDetectionUnit {
	public static void hazardDetectionUnit(
		Value ID_EX_memRead,
		List<Value> ID_EX_registerRt,
		List<Value> IF_ID_registerRt,
		List<Value> IF_ID_registerRs,
		MutableValue stallFlag) {
		/*
		if(ID_EX_memRead && ( (IF_ID_registerRt == ID_EX_memRead) || (IF_ID_registerRs == ID_EX_memRead) )
		{
			we must stall the pipeline
		}



		 */
		var cmpRTRS = and(xor(ID_EX_registerRt, IF_ID_registerRs));

		var cmpRTRT = and(xor(ID_EX_registerRt, IF_ID_registerRt));

		var isSourceNeedMem = and(cmpRTRS, cmpRTRT);

		stallFlag.set(and(ID_EX_memRead, isSourceNeedMem));

	}
}
