package mipsim.pipeline;

import mipsim.units.MemBit;
import sim.base.BusKt;

import java.util.List;

public final class MEMWB_PipelineRegister extends PipelineRegister {
	//this would be alu and memory read result,32bit
	public final List<MemBit> aluData = BusKt.slice(memory, 0, 32);
	public final List<MemBit> memoryValue = BusKt.slice(memory, 32, 64);

	//this would be register code that will be write on it,5 bit
	public final List<MemBit> rdRegister = BusKt.slice(memory, 64, 69);

	//all control flag will be passed to pipeline except memRead and memWrite
	public final MemBit memToReg = memory.get(70);
	public final MemBit regWrite = memory.get(71);

	public MEMWB_PipelineRegister() {
		super(71);
	}
}
