package mipsim.units;

import mipsim.Simulator;
import sim.base.*;

import java.util.List;

import static sim.base.BusKt.ZERO_BUS;

public final class RegisterFile implements Eval {
	private final Memory memory;
	private final List<MutableValue> readData1Bus = BusKt.bus(32);
	private final List<MutableValue> readData2Bus = BusKt.bus(32);

	public final List<MutableValue> readReg1 = BusKt.bus(5);
	public final List<MutableValue> readReg2 = BusKt.bus(5);
	public final List<MutableValue> writeReg = BusKt.bus(5);
	public final List<MutableValue> writeData = BusKt.bus(32);
	public final MutableValue regWrite = ValueKt.mut(false);
	public final List<Value> readData1 = (List) readData1Bus;
	public final List<Value> readData2 = (List) readData2Bus;

	public RegisterFile(Simulator simulator) {
		memory = MemoryKt.createWords(32);
		BusKt.set(readReg1, ZERO_BUS);
		BusKt.set(readReg2, ZERO_BUS);
		BusKt.set(writeReg, ZERO_BUS);
		BusKt.set(readData1Bus, ZERO_BUS);
		BusKt.set(readData2Bus, ZERO_BUS);
		BusKt.set(writeData, ZERO_BUS);
	}

	@Override
	public void eval() {
		EvalKt.eval(readReg1);
		EvalKt.eval(readReg2);
		EvalKt.eval(writeReg);
		EvalKt.eval(writeData);
		EvalKt.eval(regWrite);

		// cache values, can ignore (comment) this section
		var readReg1 = BusKt.constant(this.readReg1);
		var readReg2 = BusKt.constant(this.readReg2);
		var writeReg = BusKt.constant(this.writeReg);
		var writeData = BusKt.constant(this.writeData);
		var regWrite = ValueKt.constant(this.regWrite);

		if (regWrite.get()) {
			var reg = BusKt.toInt(writeReg);
			var word = MemoryKt.getWord(memory, reg);
			BusKt.set(word, writeData);
			EvalKt.eval(word);
		}

		{  // write reg1 to output line
			var reg = BusKt.toInt(readReg1);
			var word = MemoryKt.getWord(memory, reg);
			BusKt.set(readData1Bus, word);
		}

		{  // write reg2 to output line
			var reg = BusKt.toInt(readReg2);
			var word = MemoryKt.getWord(memory, reg);
			BusKt.set(readData2Bus, word);
		}
	}
}
