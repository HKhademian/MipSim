package mipsim;


import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;
import sim.gates.XorGate;

public class Main {
	public static void main(String[] args) {
		var m = new Variable(true,"");
		var i = new Variable(true,"");
		XorGate x = new XorGate(i,m);
		System.out.println("Hello World, " +x);
	}
}
