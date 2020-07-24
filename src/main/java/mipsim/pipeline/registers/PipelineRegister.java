package mipsim.pipeline.registers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sim.DebugWriter;
import sim.base.BusKt;
import sim.base.Eval;
import sim.base.MutableValue;

import java.util.List;

/**
 * all pipeline registers use this
 */
public abstract class PipelineRegister<T extends PipelineRegister<T>> implements Eval, DebugWriter {
	public final boolean isTemp;

	@Nullable
	protected final PipelineRegister<T> next;

	@NotNull
	protected final List<? extends MutableValue> memory;

	protected PipelineRegister(final int bitSize, @Nullable final PipelineRegister<T> next) {
		this.memory = BusKt.bus(bitSize);
		this.next = next;
		this.isTemp = next == null;
	}

	@Override
	public void eval(final long time) {
		if (next != null) {
			BusKt.set(this.memory, BusKt.constant(next.memory));
		}
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
