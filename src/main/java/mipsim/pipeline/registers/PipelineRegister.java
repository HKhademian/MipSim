package mipsim.pipeline.registers;

import mipsim.units.MemoryKt;
import org.jetbrains.annotations.NotNull;
import sim.DebugWriter;
import sim.base.Eval;
import sim.base.EvalKt;
import sim.base.MutableValue;

import java.util.List;

/**
 * all pipeline registers use this
 */
public abstract class PipelineRegister implements Eval, DebugWriter {
	protected final List<? extends MutableValue> memory;

	protected PipelineRegister(int bitSize) {
		memory = MemoryKt.createMemory(bitSize);
	}

	@Override
	public void eval(final long time) {
		EvalKt.eval(memory, time);
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		((DebugWriter) memory).writeDebug(buffer);
	}

	@Override
	public String toString() {
		var buffer = new StringBuffer();
		writeDebug(buffer);
		return buffer.toString();
	}
}
