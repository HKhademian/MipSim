package mipsim.stage;

import mipsim.Simulator;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public class InstructionFetchStage {
	private final Simulator sim;

	public InstructionFetchStage(Simulator sim) {
		this.sim = sim;
	}

	public void readInstruction(List<MutableValue> PC, List<Value> jump, List<Value> branch, List<Value> muxSelector, List<MutableValue> result) {
		//here we need use multiplexer for all bits
		// sim.instructionMemory read (PC, result);
		addFourPC(PC);
	}


	//todo
	public void addFourPC(List<MutableValue> PC) {

	}

}
