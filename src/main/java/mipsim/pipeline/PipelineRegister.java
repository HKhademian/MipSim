package mipsim.pipeline;

import mipsim.units.Memory;
import mipsim.units.MemoryKt;
import sim.base.Eval;

/**
 * all pipeline registers use this
 */
public abstract class PipelineRegister implements Eval {
	protected final Memory memory;

	protected PipelineRegister(int bitSize) {
		memory = MemoryKt.createMemory(bitSize);
	}

	@Override
	public void eval() {
		memory.eval();
	}
}
