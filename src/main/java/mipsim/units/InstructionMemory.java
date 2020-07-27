//package mipsim.units;
//
//import org.jetbrains.annotations.NotNull;
//import sim.tool.DebugWriter;
//import sim.base.*;
//import sim.tool.TestKt;
//
//import java.util.List;
//
//import static mipsim.sim.ParserKt.parseBinToInstruction;
//
///**
// * TODO: create multiplexer flavor
// */
//public final class InstructionMemory implements Eval, DebugWriter {
//	public final List<? extends MutableValue> _memory;
//	private final List<? extends MutableValue> instructionBus = BusKt.bus(32);
//	public final List<? extends Value> instruction = instructionBus;
//	public final List<? extends MutableValue> pc = BusKt.bus(32);
//
//	public InstructionMemory(final Value clock, final int wordSize) {
//		_memory = MemoryKt.createWords(wordSize);
//		BusKt.set(pc, BusKt.EMPTY_BUS);
//	}
//
//	/**
//	 * here we must read pc value and choose words in memory,
//	 * by multiplexer, but it's too complicated (but possible with mux)
//	 */
//	public void eval(final long time) {
//		final var pc = BusKt.constant(this.pc); // cache PC value, can ignore (comment) this section
//		final var wordIndex = BusKt.toInt(pc) >> 2;
//		final var newInst = MemoryKt.getWord(_memory, wordIndex);
//		final var constInst = BusKt.constant(newInst);
//		BusKt.set(instructionBus, constInst);
//	}
//
//	@Override
//	public void writeDebug(@NotNull final StringBuffer buffer) {
//		var pc = BusKt.toInt(this.pc);
//		var instructionBin = BusKt.toInt(this.instruction);
//		var instructionStr = parseBinToInstruction(instructionBin);
//		buffer
//			.append("InstructionMemory:\t")
//			.append(String.format("PC=%08xH\t", pc))
//			.append(String.format("INST=%08xH=' %s '\t", instructionBin, instructionStr));
//	}
//
//	/**
//	 * test in progress by: hossain
//	 */
//	public static void main(String[] args) {
//		final var size = 64;
//		final var instMem = new InstructionMemory(null, size);
//		TestKt.testOn(instMem, "beforeInit");
//
//		TestKt.testOn(instMem, "init", () -> {
//			final var time = System.currentTimeMillis();
//			for (int i = 0; i < size; i++) {
//				var word = MemoryKt.getWord(instMem._memory, i);
//				BusKt.set(word, 2 + i * 3);
//				EvalKt.eval(word, time);
//			}
//			instMem.eval(time);
//		});
//
//		TestKt.testOn(instMem, "changePCNoEval", () -> {
//			BusKt.set(instMem.pc, 5);
//		});
//
//		TestKt.testOn(instMem, "eval+pcChange", () -> {
//			final var time = System.currentTimeMillis();
//			BusKt.set(instMem.pc, 5);
//			instMem.eval(time);
//		});
//
//	}
//}
