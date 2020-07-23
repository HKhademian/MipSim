package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.Eval;

public abstract class Stage implements Eval {
	protected final Processor processor;

	protected Stage(final Processor processor) {
		this.processor = processor;
	}

	/**
	 * wiring here
	 */
	public abstract void init();

	@Override
	public void eval(final long time) {
	}
}
