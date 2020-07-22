package mipsim.pipeline.registers;

import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseBinToInstruction;

public final class MEMWB_PipelineRegister extends PipelineRegister {

	//all control flag will be passed to pipeline

	//WB
	public final List<MutableValue> WB = BusKt.slice(memory, 0, 2);
	public MutableValue memToReg = WB.get(0);
	public MutableValue regWrite = WB.get(1);
	// we use the memRead memWrite


	//this would be alu and memory read result,32bit
	public final List<MutableValue> aluData = BusKt.slice(memory, 2, 34);
	public final List<MutableValue> memoryData = BusKt.slice(memory, 34, 66);

	//this would be register code that will be write on it,5 bit
	public final List<MutableValue> rdRegister = BusKt.slice(memory, 66, 71);


	public MEMWB_PipelineRegister() {
		super(71);
	}

	public void writeDebug(@NotNull StringBuffer buffer) {
		var memToReg = this.memToReg;
		var regWrite = this.regWrite;

		var aluDataBin = BusKt.toInt(this.aluData);
		var aluDataBinStr = parseBinToInstruction(aluDataBin);

		var memoryDataBin = BusKt.toInt(this.memoryData);
		var memoryDataStr = parseBinToInstruction(memoryDataBin);

		var rdRegisterBin = BusKt.toInt(this.rdRegister);
		var rdRegisterStr = parseBinToInstruction(rdRegisterBin);

		buffer
			.append(String.format("memToReg: ", memToReg))
			.append(String.format("regWrite: ", regWrite))
			.append(String.format("aluData: %08xH = ' %s '\t", aluDataBin, aluDataBinStr))
			.append(String.format("memoryData: %08xH = ' %s '\t", memoryDataBin, memoryDataStr))
			.append(String.format("rdRegister: %08xH = ' %s '\t", rdRegisterBin, rdRegisterStr));
	}

}
