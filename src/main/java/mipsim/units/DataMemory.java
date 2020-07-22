package mipsim.units;

import mipsim.Processor;
import mipsim.test.TestKey;
import sim.base.*;

import java.util.List;

import static sim.base.BusKt.EMPTY_BUS;
import static sim.base.BusKt.ZERO_BUS;
import static sim.real.MuxKt.mux2;

public final class DataMemory implements Eval {
	public final Memory _memory;
	private final List<MutableValue> readDataBus = BusKt.bus(32);

	public final List<MutableValue> address = BusKt.bus(5);
	public final List<MutableValue> writeData = BusKt.bus(32);
	public final MutableValue memWrite = ValueKt.mut(false);
	public final MutableValue memRead = ValueKt.mut(false);
	public final List<Value> readData = (List) readDataBus;

	public DataMemory(Processor processor, int wordSize) {
		_memory = MemoryKt.createWords(wordSize);
		BusKt.set(address, ZERO_BUS);
		BusKt.set(writeData, ZERO_BUS);
		BusKt.set(readDataBus, ZERO_BUS);
	}

	@Override
	public void eval() {
		EvalKt.eval(address);
		EvalKt.eval(writeData);
		EvalKt.eval(memWrite);
		EvalKt.eval(memRead);
		EvalKt.eval(readData);

		// cache values, can ignore (comment) this section
		var address = BusKt.constant(this.address);
		var writeData = BusKt.constant(this.writeData);
		var memWrite = ValueKt.constant(this.memWrite);
		var memRead = ValueKt.constant(this.memRead);

		var wordAddress = BusKt.toInt(address) >> 2; // it's address/4 to get word number
		var word = MemoryKt.getWord(_memory, wordAddress);

		{ // write data to memory
			var writeFinalData = mux2(memWrite, EMPTY_BUS, writeData);
			BusKt.set(word, writeFinalData);
			EvalKt.eval(word);
		}

		{  // read value or zero out
			var readFinalData = mux2(memRead, EMPTY_BUS, word);
			BusKt.set(readDataBus, readFinalData);
		}
	}
}
