package mipsim;


import sim.HelpersKt;
import sim.base.Variable;

import java.util.List;

//@param shiftma -> a number show number of bit will shift
final class ShiftHelper{
	public static void oneBitShifterLeft(Variable[] shift, List<Variable> shiftma, int position , Variable[] output){
		try {
			output[position+ HelpersKt.toInt(shiftma)] = new Variable(shift[position].get());
		}catch (Exception e){
			// the output is only contain 32 bit and shiftma an position show location that is out of 32 (above of 31)so we ignore the result
		}
	}

	public static void oneBitShifterRight(Variable[] shift, List<Variable> shiftma, int position , Variable[] output){
		try {
			output[position- HelpersKt.toInt(shiftma)] = new Variable(shift[position].get());
		}catch (Exception e){
			// the output is only contain 32 bit and shiftma an position show location that is out of 32(below of 0) so we ignore the result
		}
	}
}


public  class ShiftLogical {

	public static void thirtyTwoBitShifterLeft(Variable[] shift, List<Variable> shiftma , Variable[] output)
	{
		for(int position = 31; position >=0 ; position--)
			ShiftHelper.oneBitShifterLeft(shift,shiftma,position,output);
	}

	public static void thirtyTwoBitShifterRight(Variable[] shift, List<Variable> shiftma , Variable[] output)
	{
		for(int position = 31; position >=0 ; position--)
			ShiftHelper.oneBitShifterRight(shift,shiftma,position,output);
	}


}
