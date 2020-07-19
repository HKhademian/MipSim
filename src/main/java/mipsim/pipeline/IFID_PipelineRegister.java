package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.MemoryKt;
import sim.base.BusKt;

import java.util.List;

public final class IFID_PipelineRegister extends PipelineRegister {
	public final List<MemBit> instruction = BusKt.slice(memory, 0, 32);

	public IFID_PipelineRegister() {
		super(
			MemoryKt.WORD_SIZE // instruction
		);
	}
}
