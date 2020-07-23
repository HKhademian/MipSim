package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

import static mipsim.module.TinyModules.isEqual;
import static sim.base.GateKt.and;
import static sim.base.GateKt.or;

/**
 * test done; both be same , one be same , other one be same , no of them be same ->for ID_EX_memRead flag on and off
 */

public final class HazardDetectionUnit {
	public static void hazardDetectionUnit(
		Value ID_EX_memRead,
		List<? extends Value> ID_EX_registerRt,
		List<? extends Value> IF_ID_registerRt,
		List<? extends Value> IF_ID_registerRs,
		MutableValue stallFlag
	) {
		/*
		if(ID_EX_memRead && ( (IF_ID_registerRt == ID_EX_memRead) || (IF_ID_registerRs == ID_EX_memRead) )
		{
			we must stall the pipeline
		}



		 */
		var cmpRTRS = isEqual(ID_EX_registerRt, IF_ID_registerRs);
		//System.out.println(IF_ID_registerRs+" "+ID_EX_registerRt+" "+cmpRTRS);
		var cmpRTRT = isEqual(ID_EX_registerRt, IF_ID_registerRt);
		//System.out.println(cmpRTRT);
		var isSourceNeedMem = or(cmpRTRS, cmpRTRT);

		stallFlag.set(and(ID_EX_memRead, isSourceNeedMem));

	}

	// note --> test main
	public static void main(String[] args) {

		// for testing
		// todo: wrong
		var IDEX_regWrite = BusKt.bus(5);
		IDEX_regWrite.set(0, ValueKt.mut(false));
		IDEX_regWrite.set(1, ValueKt.mut(false));
		IDEX_regWrite.set(2, ValueKt.mut(false));
		IDEX_regWrite.set(3, ValueKt.mut(true));
		IDEX_regWrite.set(4, ValueKt.mut(true));

		var regRT = BusKt.bus(5);
		// todo: wrong
		regRT.set(0, ValueKt.mut(false));
		regRT.set(1, ValueKt.mut(false));
		regRT.set(2, ValueKt.mut(false));
		regRT.set(3, ValueKt.mut(false));
		regRT.set(4, ValueKt.mut(true));

		var regRS = BusKt.bus(5);

		// todo: wrong
		regRS.set(0, ValueKt.mut(false));
		regRS.set(1, ValueKt.mut(false));
		regRS.set(2, ValueKt.mut(false));
		regRS.set(3, ValueKt.mut(true));
		regRS.set(4, ValueKt.mut(true));

		var IDEX_read = ValueKt.mut(true);

		var result = ValueKt.mut(false);

		HazardDetectionUnit.hazardDetectionUnit(IDEX_read, IDEX_regWrite, regRT, regRS, result);
		System.out.println(result);

	}

}
