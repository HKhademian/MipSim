package mipsim.pipeline.registers;

import mipsim.units.Memory;
import mipsim.units.MemoryKt;
import org.jetbrains.annotations.NotNull;
import sim.DebugWriter;
import sim.base.Eval;

/**
 * all pipeline registers use this
 */
public abstract class PipelineRegister implements Eval, DebugWriter {
	protected final Memory memory;

	protected PipelineRegister(int bitSize) {
		memory = MemoryKt.createMemory(bitSize);
	}

	@Override
	public void eval() {
		memory.eval();
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		memory.writeDebug(buffer);
	}

	@Override
	public String toString() {
		var buffer = new StringBuffer();
		writeDebug(buffer);
		return buffer.toString();
	}
}
