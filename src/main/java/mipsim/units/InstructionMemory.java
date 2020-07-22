package mipsim.units;

import mipsim.Processor;
import sim.base.BusKt;
import sim.base.Eval;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public final class InstructionMemory implements Eval {
	public final Memory _memory;
	private final List<MutableValue> instructionBus = BusKt.bus(32);
	public final List<Value> instruction = (List) instructionBus;
	public final List<MutableValue> pc = BusKt.bus(32);

	public InstructionMemory(Processor processor, int wordSize) {
		_memory = MemoryKt.createWords(wordSize);
		BusKt.set(pc, BusKt.EMPTY_BUS);
	}

	/**
	 * here we must read pc value and choose words in memory,
	 * by multiplexer, but it's too complicated (but possible with mux)
	 */
	public void eval() {
		var pc = BusKt.constant(this.pc); // cache PC value, can ignore (comment) this section
		var wordIndex = BusKt.toInt(pc);
		var newInst = MemoryKt.getWord(_memory, wordIndex);
		BusKt.set(instructionBus, newInst);
	}
}
