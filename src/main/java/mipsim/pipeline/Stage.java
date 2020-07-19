package mipsim.pipeline;

import mipsim.Simulator;
import sim.base.Eval;

public abstract class Stage implements Eval {
	private final Simulator simulator;

	protected Stage(final Simulator simulator) {
		this.simulator = simulator;
	}
}
