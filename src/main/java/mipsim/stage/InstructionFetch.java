package mipsim.stage;

import mipsim.memory.InstructionMemory;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;
import sim.complex.MuxKt;

import java.util.List;

public class InstructionFetch {

	InstructionMemory instructionMemory;

	public InstructionFetch(int sizeOfMemory)
	{
		instructionMemory = new InstructionMemory(sizeOfMemory);
	}

	public void readInstruction(List<MutableValue> PC, List<Value> jump, List<Value> branch, List<Value> muxSelector, List<MutableValue> result)
	{
		//here we need use multi plixer for all bits
		instructionMemory.readMemory(PC,result);
		addFourPC(PC);
	}
	public void readFile(List<MutableValue> parser )
	{
		instructionMemory.writeInstruction(parser);
	}
	//todo
	public void addFourPC(List<MutableValue> PC)
	{

	}

}
