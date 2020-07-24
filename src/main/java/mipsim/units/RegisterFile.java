package mipsim.units;

import org.jetbrains.annotations.NotNull;
import sim.DebugWriter;
import sim.base.*;

import java.util.List;

import static sim.base.BusKt.ZERO_BUS;

public final class RegisterFile implements Eval, DebugWriter {
	public final List<? extends MutableValue> _memory;
	private final List<? extends MutableValue> readData1Bus = BusKt.bus(32);
	private final List<? extends MutableValue> readData2Bus = BusKt.bus(32);

	public final List<? extends MutableValue> readReg1 = BusKt.bus(5);
	public final List<? extends MutableValue> readReg2 = BusKt.bus(5);
	public final List<? extends MutableValue> writeReg = BusKt.bus(5);
	public final List<? extends MutableValue> writeData = BusKt.bus(32);
	public final MutableValue regWrite = ValueKt.mut(false);
	public final List<? extends Value> readData1 = readData1Bus;
	public final List<? extends Value> readData2 = readData2Bus;

	public RegisterFile(final Value clock) {
		_memory = MemoryKt.createWords(32);
		BusKt.set(readReg1, ZERO_BUS);
		BusKt.set(readReg2, ZERO_BUS);
		BusKt.set(writeReg, ZERO_BUS);
		BusKt.set(readData1Bus, ZERO_BUS);
		BusKt.set(readData2Bus, ZERO_BUS);
		BusKt.set(writeData, ZERO_BUS);

		// set $0 non-writable
		var regZero = MemoryKt.getWord(_memory, 0);
		MemoryKt.setMemWrite(regZero, false);
	}

	@Override
	public void eval(final long time) {
		EvalKt.eval(readReg1, time);
		EvalKt.eval(readReg2, time);
		EvalKt.eval(writeReg, time);
		EvalKt.eval(writeData, time);
		EvalKt.eval(regWrite, time);

		// cache values, can ignore (comment) this section
		var readReg1 = BusKt.constant(this.readReg1);
		var readReg2 = BusKt.constant(this.readReg2);
		var writeReg = BusKt.constant(this.writeReg);
		var writeData = BusKt.constant(this.writeData);
		var regWrite = ValueKt.constant(this.regWrite);

		if (regWrite.get()) {
			var reg = BusKt.toInt(writeReg);
			if (reg != 0) {
				var word = MemoryKt.getWord(_memory, reg);
				BusKt.set(word, writeData);
				EvalKt.eval(word, time);
			}
		}

		{  // write reg1 to output line
			var reg = BusKt.toInt(readReg1);
			var word = MemoryKt.getWord(_memory, reg);
			BusKt.set(readData1Bus, word);
		}

		{  // write reg2 to output line
			var reg = BusKt.toInt(readReg2);
			var word = MemoryKt.getWord(_memory, reg);
			BusKt.set(readData2Bus, word);
		}
	}

	@Override
	public void writeDebug(@NotNull final StringBuffer buffer) {
		for (int i = 0; i < 32; i++) {
			var value = BusKt.toInt(MemoryKt.getWord(_memory, i));
			buffer.append(String.format("$%d=%08xH ", i, value));
		}
		buffer.append("\n");
		buffer.append("ReadReg1: ").append(BusKt.toInt(readReg1)).append("\t");
		buffer.append("ReadReg2: ").append(BusKt.toInt(readReg2)).append("\t");
		buffer.append("WriteReg: ").append(BusKt.toInt(writeReg)).append("\n");
		buffer.append("RegWrite: ").append(regWrite).append("\t");
		buffer.append("ReadData1: ").append(BusKt.toInt(readData1)).append("\t");
		buffer.append("ReadData2: ").append(BusKt.toInt(readData2)).append("\t");
		buffer.append("WriteData: ").append(BusKt.toInt(writeData));
	}

	public static void main(final String... args) {

	}
}
