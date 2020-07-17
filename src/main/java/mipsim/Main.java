package mipsim;

import sim.base.ConstValue;
import sim.base.Value;
import sim.gates.AndGate;

public class Main {
	public static void main(String[] args) {
		Value val1 = new ConstValue(false);
		AndGate g1 = new AndGate(val1);
		System.out.println("Hello World, " + g1);
	}
}
