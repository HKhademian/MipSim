package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.Value;

import java.util.List;

public final class WBIF_PipelineRegister extends PipelineRegister<WBIF_PipelineRegister> {
	public static final int SIZE = 32;

	public final List<? extends Value> pc = BusKt.slice(memory, 0, 32);//this will be 32 bit for the branch and jump

	private WBIF_PipelineRegister(final Processor processor, final WBIF_PipelineRegister next) {
		super(processor, SIZE, next);
	}

	public WBIF_PipelineRegister(final Processor processor) {
		this(processor, new WBIF_PipelineRegister(processor, null));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		var pc = BusKt.toInt(this.pc);
		buffer
			.append(String.format("PC: %08xH\t", pc))
		;
	}
}
