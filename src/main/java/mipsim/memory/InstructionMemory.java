package mipsim.memory;

import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;
//all value and mutable value are now bit but must change to byte <----------------------------------------------
public class InstructionMemory {
		List<MutableValue> memory;

		public InstructionMemory(int size)
		{
			memory = BusKt.bus(size);
		}

		public void readMemory(List<MutableValue> PC,List<MutableValue> instruction)
		{
			instruction.get(0).set(memory.get(BusKt.toInt(PC)));
			instruction.get(1).set(memory.get(BusKt.toInt(PC)+1));
			instruction.get(2).set(memory.get(BusKt.toInt(PC)+2));
			instruction.get(3).set(memory.get(BusKt.toInt(PC)+3));
		}


	public void writeInstruction(List<MutableValue> parserResult)
	{
		memory = parserResult;
	}


}
