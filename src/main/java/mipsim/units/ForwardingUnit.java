package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

import static sim.gates.GatesKt.*;

public final class ForwardingUnit {

	//zero register code is zero so we put 5 false line inside of it;
	final static List<Value> zeroRegister = BusKt.toBus(0, 5);

	public static void forwardingUnitEXHazard(
		Value EX_MEM_RegWrite,
		List<? extends Value> EX_MEM_RegisterRd,
		List<? extends Value> ID_EX_RegisterSource,
		List<? extends MutableValue> forwardingFlag
	) {
		var isSourceNeedEx = and(xor(EX_MEM_RegisterRd, ID_EX_RegisterSource));
		var isNotZero = not(and(xor(EX_MEM_RegisterRd, zeroRegister)));
		//check that is source Value could be in register that in execution part and it's not to be zero register

		forwardingFlag.get(1).set(and(isNotZero, isSourceNeedEx, EX_MEM_RegWrite));

		// if all condition happen flag wil be --> 10
	}

	public static void forwardingUnitMEMHazard(
		Value MEM_WB_RegWrite,
		List<? extends Value> MEM_WB_RegisterRd,
		List<? extends Value> ID_EX_RegisterSource,
		Value EX_MEM_RegWrite,
		List<? extends Value> EX_MEM_RegisterRd,
		List<? extends MutableValue> forwardingFlag
	) {
		var isSourceNeedMEM = and(xor(MEM_WB_RegisterRd, ID_EX_RegisterSource));
		var isNotZero = not(and(xor(MEM_WB_RegisterRd, zeroRegister)));
		//check that is source Value could be in register that in memory part and it's not to be zero register

		var isNotSourceInEx = not(and(EX_MEM_RegWrite, not(and(xor(EX_MEM_RegisterRd, zeroRegister)))));
		//Not (EX/MEM.write and (EX/MEM.rd != Register.zero))

		forwardingFlag.get(0).set(and(isNotZero, isSourceNeedMEM, MEM_WB_RegWrite, isNotSourceInEx));
		// if all condition happen flag wil be --> 01
	}

}
