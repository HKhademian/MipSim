package mipsim;


import sim.HelpersKt;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.base.Variable;

import java.util.List;

//@param shiftma -> a number show number of bit will shift
final class ShiftHelper{
	public static void oneBitShifterLeft(List<Value> shift, List<Value> shiftma, int position , List<MutableValue> output){
		try {
			output.get(position+ BusKt.toInt(shiftma)).set(shift.get(position).get());
		}catch (Exception e){
			// the output is only contain 32 bit and shiftma an position show location that is out of 32 (above of 31)so we ignore the result
		}
	}

	public static void oneBitShifterRight(List<Value> shift, List<Value> shiftma, int position , List<MutableValue> output){
		try {
			output.get(position- BusKt.toInt(shiftma)).set(shift.get(position).get());
		}catch (Exception e){
			// the output is only contain 32 bit and shiftma an position show location that is out of 32(below of 0) so we ignore the result
		}
	}
}


public  class ShiftLogical {

	public static void thirtyTwoBitShifterLeft(List<Value> shift, List<Value> shiftma , List<MutableValue> output)
	{
		for(int position = 31; position >=0 ; position--)
			ShiftHelper.oneBitShifterLeft(shift,shiftma,position,output);
	}

	public static void thirtyTwoBitShifterRight(List<Value> shift, List<Value> shiftma , List<MutableValue> output)
	{
		for(int position = 31; position >=0 ; position--)
			ShiftHelper.oneBitShifterRight(shift,shiftma,position,output);
	}


}
