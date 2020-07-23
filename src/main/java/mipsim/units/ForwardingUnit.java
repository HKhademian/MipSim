package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

import static mipsim.module.TinyModules.isEqual;
import static mipsim.module.TinyModules.isNotEqual;
import static sim.base.GateKt.*;


//test:done;
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

		var isNotSourceInEx = not(and(EX_MEM_RegWrite, isNotEqual(EX_MEM_RegisterRd, ZERO_REG),isEqual(EX_MEM_RegisterRd,ID_EX_RegisterSource)));
		//Not (EX/MEM.write and ((EX/MEM.rd != Register.zero) and ((EX/MEM.rd == ID/EX.source)))
		forwardingFlag.get(0).set(and(MEM_WB_RegWrite, isNotZero, isSourceNeedMEM, isNotSourceInEx));
		// if all condition happen flag wil be --> 01
	}

	public static void main(String[] args) {

//		// exe test
//		var EX_MEM_RegisterRd = BusKt.bus(5);
//		EX_MEM_RegisterRd.set(0, ValueKt.mut(false));
//		EX_MEM_RegisterRd.set(1,ValueKt.mut(true));
//		EX_MEM_RegisterRd.set(2,ValueKt.mut(false));
//		EX_MEM_RegisterRd.set(3,ValueKt.mut(true));
//		EX_MEM_RegisterRd.set(4,ValueKt.mut(true));
//
//		var ID_EX_RegisterSource = BusKt.bus(5);
//		ID_EX_RegisterSource.set(0,ValueKt.mut(true));
//		ID_EX_RegisterSource.set(1,ValueKt.mut(true));
//		ID_EX_RegisterSource.set(2,ValueKt.mut(false));
//		ID_EX_RegisterSource.set(3,ValueKt.mut(true));
//		ID_EX_RegisterSource.set(4,ValueKt.mut(true));
//
//
//		var exmem_write = ValueKt.mut(true);
//
//		var result = BusKt.bus(2);
//		ForwardingUnit.forwardingUnitEXHazard(exmem_write,EX_MEM_RegisterRd,ID_EX_RegisterSource,result);
//		System.out.println(result.get(1)+""+result.get(0));




		// mem test
		var MEM_WB_RegisterRd = BusKt.bus(5);
		MEM_WB_RegisterRd.set(0, ValueKt.mut(true));
		MEM_WB_RegisterRd.set(1,ValueKt.mut(true));
		MEM_WB_RegisterRd.set(2,ValueKt.mut(false));
		MEM_WB_RegisterRd.set(3,ValueKt.mut(false));
		MEM_WB_RegisterRd.set(4,ValueKt.mut(false));

		var ID_EX_RegisterSource = BusKt.bus(5);
		ID_EX_RegisterSource.set(0,ValueKt.mut(true));
		ID_EX_RegisterSource.set(1,ValueKt.mut(true));
		ID_EX_RegisterSource.set(2,ValueKt.mut(false));
		ID_EX_RegisterSource.set(3,ValueKt.mut(false));
		ID_EX_RegisterSource.set(4,ValueKt.mut(false));

		var EX_MEM_RegisterRd = BusKt.bus(5);
		EX_MEM_RegisterRd.set(0,ValueKt.mut(true));
		EX_MEM_RegisterRd.set(1,ValueKt.mut(true));
		EX_MEM_RegisterRd.set(2,ValueKt.mut(false));
		EX_MEM_RegisterRd.set(3,ValueKt.mut(false));
		EX_MEM_RegisterRd.set(4,ValueKt.mut(false));

		var MEM_WB_RegWrite = ValueKt.mut(false);
		var EX_MEM_RegWrite = ValueKt.mut(false);


		var result = BusKt.bus(2);
		ForwardingUnit.forwardingUnitMEMHazard(MEM_WB_RegWrite,MEM_WB_RegisterRd,ID_EX_RegisterSource,EX_MEM_RegWrite,EX_MEM_RegisterRd,result);

		System.out.println(result.get(1)+""+result.get(0));

	}

}
