package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseBinToInstruction;

public final class IFID_PipelineRegister extends PipelineRegister<IFID_PipelineRegister> {
	public final List<? extends MutableValue> pc = BusKt.slice(memory, 0, 32);//this will be 32 bit for the branch and jump

	public final List<? extends MutableValue> instruction = BusKt.slice(memory, 32, 64);

	private IFID_PipelineRegister(final Processor processor, final IFID_PipelineRegister next) {
		super(processor, 65, next);
	}

	public IFID_PipelineRegister(final Processor processor) {
		this(processor, new IFID_PipelineRegister(processor, null));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		var pc = BusKt.toInt(this.pc);
		var instructionBin = BusKt.toInt(this.instruction);
		var instructionStr = parseBinToInstruction(instructionBin);
		buffer
			.append(String.format("PC: %08xH\t", pc))
			.append(String.format("INST: %08xH = ' %s '\t", instructionBin, instructionStr));
	}
}
