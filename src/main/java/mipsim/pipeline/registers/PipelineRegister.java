package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sim.DebugWriter;
import sim.base.Eval;
import sim.base.MutableValue;

import java.util.List;

/**
 * all pipeline registers use this
 */
public abstract class PipelineRegister<T extends PipelineRegister<T>> implements Eval, DebugWriter {
	public final boolean isTemp;
	public final T next;
	@NotNull
	protected final List<? extends MutableValue> memory;

	protected PipelineRegister(final Processor processor, final int bitSize, @Nullable final T next) {
		this.isTemp = next == null;
		this.memory = processor.alloc(bitSize, isTemp);
		this.next = next;
	}

	@Override
	public void eval(final long time) {
//		if (next != null) {
//			BusKt.set(this.memory, BusKt.constant(next.memory));
//		}
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
