package mipsim.pipeline;

import mipsim.Simulator;
import sim.base.Eval;

public abstract class Stage implements Eval {
	protected final Simulator simulator;

	protected Stage(final Simulator simulator) {
		this.simulator = simulator;
	}

	/**
	 * wiring here
	 */
	public abstract void init();
}
