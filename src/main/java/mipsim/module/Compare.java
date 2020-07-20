package mipsim.module;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

import static sim.gates.GatesKt.*;

public class Compare {
	public static void comp4Bit(Value A3, Value A2, Value A1, Value A0, Value B3, Value B2, Value B1, Value B0, MutableValue AeqB, MutableValue AltB, MutableValue AgtB) {
		AeqB.set(and(and(xnor(A3, B3), xnor(A2, B2)), and(xnor(A1, B1), xnor(A0, B0))));
		AgtB.set(or(or(and(A3, not(B3)), and(and(A2, not(B2)), xnor(A3, B3))), or(and(and(A1, not(B1)), and(xnor(A3, B3), xnor(A2, B2))), and(and(and(A0, not(B0)), xnor(A1, B1)), and(xnor(A3, B3), xnor(A2, B2))))));
		AltB.set(nor(AeqB, AgtB));
	}

	public static void com32Bit(List<? extends Value> input1, List<? extends Value> input2, MutableValue AEB, MutableValue ALB, MutableValue AGB) {
		var AeqB = BusKt.bus(8);
		var AgtB = BusKt.bus(8);
		var AltB = BusKt.bus(8);

		comp4Bit(input1.get(31), input1.get(30), input1.get(29), input1.get(28), input2.get(31), input2.get(30), input2.get(29), input2.get(28), AeqB.get(0), AltB.get(0), AgtB.get(0));
		int j, i;
		var e = ValueKt.mut(false);
		var l = ValueKt.mut(false);
		var g = ValueKt.mut(false);
		for (i = 1, j = 27; i < 8; i++) {
			comp4Bit(input1.get(j), input1.get(j - 1), input1.get(j - 2), input1.get(j - 3), input2.get(j), input2.get(j - 1), input2.get(j - 2), input2.get(j - 3), e, l, g);
			AeqB.get(i).set(and(AeqB.get(i - 1), e));
			AgtB.get(i).set(or(and(AeqB.get(i - 1), e), AgtB.get(i - 1)));
			AltB.get(i).set(or(and(l, AeqB.get(i - 1)), AltB.get(i - 1)));
			j -= 4;
		}
		AEB.set(AeqB.get(7));
		AGB.set(AgtB.get(7));
		ALB.set(AltB.get(7));
	}
}
