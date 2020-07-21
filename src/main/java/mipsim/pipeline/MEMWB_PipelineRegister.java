package mipsim.pipeline;

import mipsim.units.MemBit;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

public final class MEMWB_PipelineRegister extends PipelineRegister {

	//all control flag will be passed to pipeline

	//WB
	public final List<MutableValue> WB = BusKt.slice(memory, 0, 2);
	public final MutableValue memToReg = WB.get(0);
	public final MutableValue regWrite = WB.get(1);
	// we use the memRead memWrite


	//this would be alu and memory read result,32bit
	public final List<MutableValue> aluData = BusKt.slice(memory, 2, 34);
	public final List<MutableValue> memoryValue = BusKt.slice(memory, 34, 66);

	//this would be register code that will be write on it,5 bit
	public final List<MutableValue> rdRegister = BusKt.slice(memory, 66, 71);


	public MEMWB_PipelineRegister() {
		super(71);
	}
}
