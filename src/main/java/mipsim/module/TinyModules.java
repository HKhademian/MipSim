package mipsim.module;

import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.ValueKt;
import sim.real.AdderKt;

import java.util.List;

import static sim.gates.GatesKt.*;

public final class TinyModules {

	/**
	 * todo: merge with previous adder/sub unit
	 */
	public static void easyAdder(
		@NotNull List<? extends Value> input1,
		@NotNull List<? extends Value> input2,
		@NotNull List<? extends MutableValue> result
	) {
		Value currentCarry = ValueKt.constant(false);
		MutableValue nextCarry = ValueKt.mut(false);
		for (int i = 0; i < input1.size(); i++) {
			AdderKt.fullAdder(input1.get(i), input2.get(i), currentCarry, result.get(i), nextCarry);
			currentCarry = nextCarry;
			nextCarry = ValueKt.mut(false);
		}
	}

	/**
	 * this module, creates an adder with a constant value.
	 * it's completely gate-level adder,
	 * inputVal2 converts to a equivalent value bus in compile/bake/first time
	 */
	public static void easyAdder(
		@NotNull List<? extends Value> input1,
		int inputVal2,
		@NotNull List<? extends MutableValue> result
	) {
		var input2 = BusKt.toBus(inputVal2, input1.size());
		easyAdder(input1, input2, result);
	}

	/**
	 * like quick-adder but returns result
	 */
	@NotNull
	public static List<Value> easyAdder(
		@NotNull List<? extends Value> input1,
		int inputVal2
	) {
		var result = BusKt.bus(input1.size());
		easyAdder(input1, inputVal2, result);
		return (List) result;
	}

	@NotNull
	public static Value isNotEqual(
		@NotNull List<? extends Value> lhs,
		@NotNull List<? extends Value> rhs
	) {
		return or(xor(lhs, rhs));
	}


	@NotNull
	public static Value isEqual(
		@NotNull List<? extends Value> lhs,
		@NotNull List<? extends Value> rhs
	) {
		return not(isNotEqual(lhs, rhs));
	}

}
