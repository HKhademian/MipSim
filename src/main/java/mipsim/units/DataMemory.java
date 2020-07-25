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

	public DataMemory(final Value clock, int wordSize) {
		_memory = MemoryKt.createWords(wordSize);
		BusKt.set(address, ZERO_BUS);
		BusKt.set(writeData, ZERO_BUS);
		BusKt.set(readDataBus, ZERO_BUS);
	}

	@Override
	public void eval(final long time) {
		// cache values, can ignore (comment) this section
		var address = BusKt.constant(this.address);
		var writeData = BusKt.constant(this.writeData);
		var memWrite = ValueKt.constant(this.memWrite);
		var memRead = ValueKt.constant(this.memRead);

		var wordAddress = BusKt.toInt(address) >> 2; // it's address/4 to get word number
		var word = MemoryKt.getWord(_memory, wordAddress);

		if (memWrite.get()) { // write data to memory
			assert BusKt.toInt(writeData) != 0; // creates debug error to handle situation
			BusKt.set(word, writeData);
			EvalKt.eval(word, time);
		}

		if (memRead.get()) {  // read value or zero out
			BusKt.set(readDataBus, BusKt.constant(word));
		}
	}
}
