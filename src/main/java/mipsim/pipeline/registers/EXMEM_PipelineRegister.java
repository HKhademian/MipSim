package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.Value;

import java.util.List;

public final class EXMEM_PipelineRegister extends PipelineRegister<EXMEM_PipelineRegister> {
	public final List<? extends Value> WB = BusKt.slice(memory, 0, 2);
	public final Value memToReg = WB.get(0);
	public final Value regWrite = WB.get(1);

	public final List<? extends Value> MEM = BusKt.slice(memory, 2, 4);
	public final Value memRead = MEM.get(0);
	public final Value memWrite = MEM.get(1);

	public final List<? extends Value> aluData = BusKt.slice(memory, 4, 36);

	public final List<? extends Value> writeMem = BusKt.slice(memory, 36, 68);

	public final List<? extends Value> rtRegister = BusKt.slice(memory, 68, 73);

	private EXMEM_PipelineRegister(final Processor processor, final EXMEM_PipelineRegister next) {
		super(processor, 201, next);
	}

	public EXMEM_PipelineRegister(final Processor processor) {
		this(processor, new EXMEM_PipelineRegister(processor, null));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		final var memToReg = this.memToReg.get();
		final var regWrite = this.regWrite.get();
		final var memRead = this.memRead.get();
		final var memWrite = this.memWrite.get();

		final var aluData = BusKt.toInt(this.aluData);
		final var writeMem = BusKt.toInt(this.writeMem);
		final var rtRegister = BusKt.toInt(this.rtRegister);

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
