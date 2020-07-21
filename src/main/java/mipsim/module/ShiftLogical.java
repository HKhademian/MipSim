package mipsim.module;


import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

final class ShiftHelper {
//	public static void oneBitShifterLeft(List<Value> input, List<Value> shiftma, int position, List<MutableValue> output) {
//		// Todo: use HelpersKt.shift
//		//HelpersKt.shift(input, BusKt.toInt(shiftma), ...);
//
////		try {
////			output.get(position+ BusKt.toInt(shiftma)).set(new Variable(input.get(position).get())); ;
////		}catch (Exception e){
////			// the output is only contain 32 bit and shiftma an position show location that is out of 32 (above of 31)so we ignore the result
////		}
//	}
//
//	public static void oneBitShifterRight(List<Value> shift, List<Value> shiftma, int position, List<MutableValue> output) {
//		// Todo: use HelpersKt.shift
//		//HelpersKt.shift(input, -BusKt.toInt(shiftma), ...);
//
////		try {
////			output.get(position- BusKt.toInt(shiftma)).set(new Variable(shift.get(position).get())); ;
////		}catch (Exception e){
////			// the output is only contain 32 bit and shiftma an position show location that is out of 32(below of 0) so we ignore the result
////		}
//	}

	/**
	 * @param shiftma -> a number show number of bit will shift
	 */
	public static void thirtyTwoBitShifterLeft(
		List<? extends Value> input,
		List<? extends Value> shiftma,
		List<? extends MutableValue> output
	) {
		BusKt.set(output, HelpersKt.shift(input, BusKt.toInt(shiftma)));
	}

	/**
	 * @param shiftma -> a number show number of bit will shift
	 */
	public static void thirtyTwoBitShifterRight(
		List<? extends Value> input,
		List<? extends Value> shiftma,
		List<? extends MutableValue> output
	) {
		BusKt.set(output, HelpersKt.shift(input, -BusKt.toInt(shiftma)));
	}
}
