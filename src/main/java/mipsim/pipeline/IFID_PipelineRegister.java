package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.MemoryKt;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

public final class IFID_PipelineRegister extends PipelineRegister {
	public final List<MutableValue> instruction = BusKt.slice(memory, 0, 32);

	public final List<MutableValue> pc = BusKt.slice(memory, 32, 64);//this will be 32 bit for the branch and jump


	public IFID_PipelineRegister() {
		super(
			MemoryKt.WORD_SIZE // instruction
				+ MemoryKt.WORD_SIZE //pc
		);
	}
}
