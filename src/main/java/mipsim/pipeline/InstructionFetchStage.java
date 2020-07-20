package mipsim.pipeline;

import mipsim.Simulator;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public class InstructionFetchStage extends Stage {
	public InstructionFetchStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
		// wiring here ...
	}

	@Override
	public void eval() {

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
