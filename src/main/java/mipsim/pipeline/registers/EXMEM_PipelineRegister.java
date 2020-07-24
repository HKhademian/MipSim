package mipsim.pipeline.registers;

import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.Value;

import java.util.List;

public final class EXMEM_PipelineRegister extends PipelineRegister {
	public final List<? extends MutableValue> WB = BusKt.slice(memory, 0, 2);
	public final MutableValue memToReg = WB.get(0);
	public final MutableValue regWrite = WB.get(1);

	public final List<? extends MutableValue> MEM = BusKt.slice(memory, 2, 4);
	public final MutableValue memRead = MEM.get(0);
	public final MutableValue memWrite = MEM.get(1);

	public final List<? extends Value> aluData = BusKt.bus(32);
	public final List<? extends MutableValue> tmpAluData = BusKt.bus(32);

	public final List<? extends MutableValue> writeMem = BusKt.slice(memory, 36, 68);

	public final List<? extends MutableValue> rtRegister = BusKt.slice(memory, 68, 73);
	public final List<? extends MutableValue> test1 = BusKt.slice(memory, 73, 105);
	public final List<? extends MutableValue> test2 = BusKt.slice(memory, 105, 137);
	public final List<? extends MutableValue> test3 = BusKt.slice(memory, 137, 169);
	public final List<? extends MutableValue> test4 = BusKt.slice(memory, 169, 201);

	public EXMEM_PipelineRegister() {
		super(201);
	}

	@Override
	public void eval(final long time) {
		super.eval(time);
		BusKt.set((List) aluData, BusKt.constant(tmpAluData));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		var memToReg = this.memToReg.get();
		var regWrite = this.regWrite.get();
		var memRead = this.memRead.get();
		var memWrite = this.memWrite.get();

		var aluData = BusKt.toInt(this.aluData);
		var writeMem = BusKt.toInt(this.writeMem);
		var rtRegister = BusKt.toInt(this.rtRegister);

		buffer
			.append("memory to register: ").append(memToReg)
			.append("\t,register write: ").append(regWrite)
			.append("\t,memory read: ").append(memRead)
			.append("\t,memory write: ").append(memWrite)
			.append(String.format("\t,aluData: %08xH '", aluData))
			.append(String.format("\t,writeMem: %08xH  '", writeMem))
			.append(String.format("\t,rdRegister: %08xH ", rtRegister));
	}
}
