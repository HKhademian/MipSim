package mipsim;

import sim.base.Variable;

import static sim.gates.GatesKt.xor;

public class Main {
	public static void main(String[] args) {
		var m = new Variable(true);
		var i = new Variable(true);
		var x = xor(i, m);
		System.out.println("Hello World, " + x);
	}
}
