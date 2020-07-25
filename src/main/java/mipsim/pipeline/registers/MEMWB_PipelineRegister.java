package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.Value;

import java.util.List;

public final class MEMWB_PipelineRegister extends PipelineRegister<MEMWB_PipelineRegister> {
	public static final int SIZE = 71;


	//all control flag will be passed to pipeline

	//WB
	public final List<? extends Value> WB = BusKt.slice(memory, 0, 2);
	public final Value memToReg = WB.get(0);
	public final Value regWrite = WB.get(1);
	// we use the memRead memWrite


	//this would be alu and memory read result,32bit
	public final List<? extends Value> aluData = BusKt.slice(memory, 2, 34);
	public final List<? extends Value> memoryData = BusKt.slice(memory, 34, 66);

	//this would be register code that will be write on it,5 bit
	public final List<? extends Value> rdRegister = BusKt.slice(memory, 66, 71);


	private MEMWB_PipelineRegister(final Processor processor, final MEMWB_PipelineRegister next) {
		super(processor, SIZE, next);
	}

	public MEMWB_PipelineRegister(final Processor processor) {
		this(processor, new MEMWB_PipelineRegister(processor, null));
	}

	public void writeDebug(@NotNull StringBuffer buffer) {
		final var memToReg = this.memToReg;
		final var regWrite = this.regWrite;

		final var aluDataBin = BusKt.toInt(this.aluData);
		final var memoryDataBin = BusKt.toInt(this.memoryData);
		final var rdRegisterBin = BusKt.toInt(this.rdRegister);

		buffer
			.append("memToReg: ").append(memToReg)
			.append("regWrite: ").append(regWrite)
			.append(String.format("rdRegister: %d\t", rdRegisterBin))
			.append(String.format("aluData: %08xH\t", aluDataBin))
			.append(String.format("memoryData: %08xH\t", memoryDataBin))
		;
	}

}
