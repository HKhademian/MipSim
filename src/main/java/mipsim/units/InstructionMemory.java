package mipsim.units;

import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

public class InstructionMemory {
	public final List<MutableValue> instruction = BusKt.bus(32);
	public final List<MutableValue> pc = BusKt.bus(32);
	private final Memory memory;

	public InstructionMemory() {
		memory = MemoryKt.createWords(100);
		BusKt.set(pc, BusKt.toBus(0, 32));
	}

	/**
	 * here we must read pc value and choose words in memory,
	 * by multiplexer, but it's too complicated (but possible with mux)
	 */
	public void eval() {
		var wordIndex = BusKt.toInt(pc);
		var newInst = MemoryKt.getWord(memory, wordIndex);
		BusKt.set(instruction, newInst);
	}
}
