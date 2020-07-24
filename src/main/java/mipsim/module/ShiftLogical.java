package mipsim.module;


import sim.base.BusKt;
import sim.base.MathKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

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
}
