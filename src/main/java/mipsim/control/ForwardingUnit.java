package mipsim.control;

import sim.base.Constant;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;
import sim.gates.AndGate;
import sim.gates.NotGate;
import sim.gates.XorGate;
import sim.gates._dslKt;

import java.util.ArrayList;
import java.util.List;

public class ForwardingUnit {

	static List<Value> zeroRegister;

	static {
		zeroRegister = new ArrayList<>();
		zeroRegister.add(new Constant(false,""));
		zeroRegister.add(new Constant(false,""));
		zeroRegister.add(new Constant(false,""));
		zeroRegister.add(new Constant(false,""));
		zeroRegister.add(new Constant(false,""));
		//zero register code is zero so we put 5 false line inside of it;
	}

	public static void forwardingUnitEXHazard(Value EX_MEM_RegWrite, List<Value> EX_MEM_RegisterRd,
																						List<Value> ID_EX_RegisterSource, List<MutableValue> forwardingFlag)
	{

		var isSourceNeedEx = new AndGate(_dslKt.xor(EX_MEM_RegisterRd,ID_EX_RegisterSource));
		var isNotZero = new NotGate( new AndGate(_dslKt.xor(EX_MEM_RegisterRd,zeroRegister)));
		//check that is source Value could be in register that in execution part and it's not to be zero register


		forwardingFlag.get(1).set(new Variable(
			new AndGate(isNotZero,isSourceNeedEx,EX_MEM_RegWrite),""));

		// if all condition happen flag wil be --> 10

	}

	public static void forwardingUnitMEMHazard(Value MEM_WB_RegWrite,List<Value> MEM_WB_RegisterRd,
																						 List<Value> ID_EX_RegisterSource,Value EX_MEM_RegWrite,List<Value> EX_MEM_RegisterRd
		,List<MutableValue> forwardingFlag)
	{

		var isSourceNeedMEM = new AndGate(_dslKt.xor(MEM_WB_RegisterRd,ID_EX_RegisterSource));
		var isNotZero = new NotGate( new AndGate(_dslKt.xor(MEM_WB_RegisterRd,zeroRegister)));
		//check that is source Value could be in register that in memory part and it's not to be zero register

		var isNotSourceInEx = new NotGate(new AndGate(EX_MEM_RegWrite,
			new NotGate( new AndGate(_dslKt.xor(EX_MEM_RegisterRd,zeroRegister)))));
		//Not (EX/MEM.write and (EX/MEM.rd != Register.zero))


		forwardingFlag.get(0).set(new Variable(
			new AndGate(isNotZero,isSourceNeedMEM,MEM_WB_RegWrite,isNotSourceInEx),""));

		// if all condition happen flag wil be --> 01


	}



}
