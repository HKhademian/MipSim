package mipsim.pipeline.registers;

import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

public final class IFID_PipelineRegister extends PipelineRegister {
	public final List<MutableValue> pc = BusKt.slice(memory, 0, 32);//this will be 32 bit for the branch and jump

	public final List<MutableValue> instruction = BusKt.slice(memory, 32, 64);

	public final MutableValue flush = memory.get(64);

	public IFID_PipelineRegister() {
		super(65);
	}
}
