package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

import static mipsim.module.TinyModules.isEqual;
import static mipsim.module.TinyModules.isNotEqual;
import static sim.gates.GatesKt.and;
import static sim.gates.GatesKt.not;

public final class ForwardingUnit {

	//zero register code is zero so we put 5 false line inside of it;
	public static final List<Value> ZERO_REG = BusKt.toBus(0, 5);

	public static void forwardingUnitEXHazard(
		Value EX_MEM_RegWrite,
		List<? extends Value> EX_MEM_RegisterRd,
		List<? extends Value> ID_EX_RegisterSource,
		List<? extends MutableValue> forwardingFlag
	) {
		var isSourceNeedEx = isEqual(EX_MEM_RegisterRd, ID_EX_RegisterSource);
		var isNotZero = isNotEqual(EX_MEM_RegisterRd, ZERO_REG);
		//check that is source Value could be in register that in execution part and it's not to be zero register

		forwardingFlag.get(1).set(and(EX_MEM_RegWrite, isNotZero, isSourceNeedEx));

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
		var isSourceNeedMEM = isEqual(MEM_WB_RegisterRd, ID_EX_RegisterSource);
		var isNotZero = isNotEqual(MEM_WB_RegisterRd, ZERO_REG);
		//check that is source Value could be in register that in memory part and it's not to be zero register

		var isNotSourceInEx = not(and(EX_MEM_RegWrite, isNotEqual(EX_MEM_RegisterRd, ZERO_REG)));
		//Not (EX/MEM.write and (EX/MEM.rd != Register.zero))

		forwardingFlag.get(0).set(and(MEM_WB_RegWrite, isNotZero, isSourceNeedMEM, isNotSourceInEx));
		// if all condition happen flag wil be --> 01
	}

}
