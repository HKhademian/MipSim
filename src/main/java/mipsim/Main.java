package mipsim;


import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;
import sim.complex.MuxKt;
import sim.gates.XorGate;

import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {


//		var m = new Variable(true,"");
//		var i = new Variable(true,"");
//		XorGate x = new XorGate(i,m);
//		System.out.println("Hello World, " +x);

		ArrayList <Value>xi = (ArrayList<Value>) BusKt.toBus(9,32);
		ArrayList <Value>  xj = (ArrayList<Value>) BusKt.toBus(1,32);
		Variable TRue = new Variable(true,"");
		Variable False = new Variable(false,"");
		List<MutableValue> output = BusKt.bus(32);
//		var selector = BusKt.bus(2);
		ArrayList<Variable> carry = new ArrayList<>();
		for (int i = 0 ; i < 33;i++){
			carry.add(new Variable(false,""));
		}
		//LogicALU.AddSub(xi,xj,new Variable(false,""),output,carry);
		//ShiftLogical.thirtyTwoBitShifterRight(xi,xj,output);
		int reasult = BusKt.toInt(output);
		System.out.println(reasult);
//		MuxKt.mux2(m,x,output.get(5));
//
//		var resAnd = BusKt.bus(32);
//		var resSub = BusKt.bus(32);
//		var resOr = BusKt.bus(32);
//		var resIgnore = BusKt.bus(32);

//		LogicALU.thirtyTwoBitAnd(xi,xj,resAnd);
		// toDo sub and or
//
//		output.get(0).set(MuxKt.mux4(m,x,resAnd.get(0),resSub.get(0),resOr.get(0),resIgnore.get(0)));
//		output.get(1).set(MuxKt.mux(selector,resAnd.get(1),resSub.get(1),resOr.get(1),resIgnore.get(1)));
	}
}
