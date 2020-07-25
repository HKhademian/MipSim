package mipsim.units;

import sim.base.*;

import java.util.List;

import static sim.base.BusKt.ZERO_BUS;

public final class DataMemory implements Eval {
	public final List<? extends MutableValue> _memory;
	private final List<? extends MutableValue> readDataBus = BusKt.bus(32);

	public final List<? extends MutableValue> address = BusKt.bus(32);
	public final List<? extends MutableValue> writeData = BusKt.bus(32);
	public final MutableValue memWrite = ValueKt.mut(false);
	public final MutableValue memRead = ValueKt.mut(false);
	public final List<? extends Value> readData = readDataBus;

	public final int wordSize;

	public DataMemory(final Value clock, final int wordSize) {
		this.wordSize = wordSize;
		_memory = MemoryKt.createWords(wordSize);
		BusKt.set(address, ZERO_BUS);
		BusKt.set(writeData, ZERO_BUS);
		BusKt.set(readDataBus, ZERO_BUS);
	}

	@Override
	public void eval(final long time) {
		// cache values, can ignore (comment) this section
		final var address = BusKt.constant(this.address);
		final var writeData = BusKt.constant(this.writeData);
		final var memWrite = ValueKt.constant(this.memWrite);
		final var memRead = ValueKt.constant(this.memRead);

		final var wordAddress = BusKt.toInt(address) >> 2; // it's address/4 to get word number

		if (memWrite.get()) { // write data to memory
			assert wordAddress < wordSize;
			final var word = MemoryKt.getWord(_memory, wordAddress);
			BusKt.set(word, writeData);
			EvalKt.eval(word, time);
		}

		if (memRead.get()) {  // read value or zero out
			assert wordAddress < wordSize;
			final var word = MemoryKt.getWord(_memory, wordAddress);
			BusKt.set(readDataBus, BusKt.constant(word));
		}
	}
}
