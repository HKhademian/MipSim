package mipsim.module;


import sim.base.BusKt;
import sim.base.MathKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.ArrayList;
import java.util.List;

import static sim.complex.MuxKt.mux;

final class ShiftHelper {
	/**
	 * @param shiftma -> a number show number of bit will shift
	 */
	public static void thirtyTwoBitShifterLeft(
		List<? extends Value> input,
		List<? extends Value> shiftma,
		List<? extends MutableValue> output
	) {
		BusKt.set(output, MathKt.shift(input, BusKt.toInt(shiftma)));
	}

	/**
	 * @param shiftma -> a number show number of bit will shift
	 */
	public static void thirtyTwoBitShifterRight(
		List<? extends Value> input,
		List<? extends Value> shiftma,
		List<? extends MutableValue> output
	) {
		BusKt.set(output, MathKt.shift(input, -BusKt.toInt(shiftma)));
	}

	public static void shifter(boolean isRight,List<? extends Value> shiftMA,List<? extends Value> input, List<? extends MutableValue> result ) {
		ArrayList<List<Value>> shifts = new ArrayList<>(32);
		for (int i = 0; i < 32; i++) {
			shifts.add(MathKt.shift(input, i * (isRight ? -1 : 1),false,32));
		}
		BusKt.set(result,mux(shiftMA, shifts));
	}
}
