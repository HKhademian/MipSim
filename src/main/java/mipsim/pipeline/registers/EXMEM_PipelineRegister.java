package mipsim.pipeline.registers;

import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseBinToInstruction;

public final class EXMEM_PipelineRegister extends PipelineRegister {

	//all control flag will be passed to pipeline

	//WB
	public final List<MutableValue> WB = BusKt.slice(memory, 0, 2);
	public final MutableValue memToReg = WB.get(0);
	public final MutableValue regWrite = WB.get(1);

	//MEM
	public final List<MutableValue> MEM = BusKt.slice(memory, 2, 4);
	public final MutableValue memRead = MEM.get(0);
	public final MutableValue memWrite = MEM.get(1);

	//we used regDst aluSrc alu control unit in EX state


	//this would be alu result and value that would be write on memory,32bit
	public final List<MutableValue> aluData = BusKt.slice(memory, 4, 36);
	public final List<MutableValue> writeMem = BusKt.slice(memory, 36, 68);

	//this would be register code that will be write on it,5 bit
	public final List<MutableValue> rtRegister = BusKt.slice(memory, 68, 73);


	// note --> we used aluOp  in stage execution

	public EXMEM_PipelineRegister() {
		super(73);
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
