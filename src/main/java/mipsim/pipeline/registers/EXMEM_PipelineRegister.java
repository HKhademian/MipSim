package mipsim.pipeline.registers;

import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

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
	public final List<MutableValue> rdRegister = BusKt.slice(memory, 68, 73);





	// note --> we used aluOp  in stage execution

	public EXMEM_PipelineRegister() {
		super(73);
	}
}
