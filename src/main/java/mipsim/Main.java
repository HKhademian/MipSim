package mipsim;

import sim.base.ValueKt;

import static sim.gates.GatesKt.xor;

public class Main {
	public static void main(String[] args) {
		var m = ValueKt.mut(true);

		var i = ValueKt.mut(true);


		var x = xor(i, m);
		System.out.println("Hello World, " + x);
	}
}
