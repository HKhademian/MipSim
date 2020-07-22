package mipsim.units;

import org.jetbrains.annotations.NotNull;
import sim.DebugWriter;
import sim.base.BusKt;
import sim.base.Eval;
import sim.base.MutableValue;
import sim.base.Value;
import sim.test.TestKt;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseBinToInstruction;

/**
 * TODO: create multiplexer flavor
 */
public final class InstructionMemory implements Eval, DebugWriter {
	public final Memory _memory;
	private final List<MutableValue> instructionBus = BusKt.bus(32);
	public final List<Value> instruction = (List) instructionBus;
	public final List<MutableValue> pc = BusKt.bus(32);

	public InstructionMemory(final Value clock, final int wordSize) {
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

	@Override
	public void writeDebug(@NotNull final StringBuffer buffer) {
		var pc = BusKt.toInt(this.pc);
		var instructionBin = BusKt.toInt(this.instruction);
		var instructionStr = parseBinToInstruction(instructionBin);
		buffer
			.append("InstructionMemory:\t")
			.append(String.format("PC=%08xH\t", pc))
			.append(String.format("INST=%08xH=' %s '\t", instructionBin, instructionStr));
	}

	public static void main(String[] args) {
		final var size = 64;
		final var instMem = new InstructionMemory(null, size);
		TestKt.testOn(instMem, "beforeInit");

		TestKt.testOn(instMem, "init", () -> {
			for (int i = 0; i < size; i++) {
				var word = MemoryKt.getWord(instMem._memory, i);
				BusKt.set(word, 2 + i * 3);
			}
		});

		TestKt.testOn(instMem, "changePCNoEval", () -> {
			BusKt.set(instMem.pc, 5);
		});

		TestKt.testOn(instMem, "eval+pcChange", () -> {
			BusKt.set(instMem.pc, 5);
			instMem.eval();
		});
	}
}
