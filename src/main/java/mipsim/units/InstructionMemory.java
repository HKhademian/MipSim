package mipsim.units;

import mipsim.Simulator;
import sim.base.BusKt;
import sim.base.Eval;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public final class InstructionMemory implements Eval {
	public final Memory memory;
	private final List<MutableValue> instructionBus = BusKt.bus(32);
	public final List<Value> instruction = (List) instructionBus;
	public final List<MutableValue> pc = BusKt.bus(32);

	public InstructionMemory(Simulator simulator,  int wordSize) {
		memory = MemoryKt.createWords(wordSize);
		BusKt.set(pc, BusKt.EMPTY_BUS);
	}

	/**
	 * here we must read pc value and choose words in memory,
	 * by multiplexer, but it's too complicated (but possible with mux)
	 */
	public void eval() {
		var pc = BusKt.constant(this.pc); // cache PC value, can ignore (comment) this section
		var wordIndex = BusKt.toInt(pc);
		var newInst = MemoryKt.getWord(memory, wordIndex);
		BusKt.set(instructionBus, newInst);
	}
}
