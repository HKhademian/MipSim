package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.MemoryKt;
import sim.base.BusKt;

import java.util.List;

public final class EXMEM_PipelineRegister extends PipelineRegister {
	//this would be alu result and value that would be write on memory,32bit
	public final List<MemBit> aluData = BusKt.slice(memory, 0, 32);
	public final List<MemBit> writeMe = BusKt.slice(memory, 32, 64);

	//this would be register code that will be write on it,5 bit
	public final List<MemBit> rdRegister = BusKt.slice(memory, 64, 69);


	//all control flag will be passed to pipeline except aluSrc and aluOp
	public final MemBit memToReg = memory.get(70);
	public final MemBit regWrite = memory.get(71);
	public final MemBit memRead = memory.get(72);
	public final MemBit memWrite = memory.get(73);


	// note --> we used aluOp  in stage execution

	public EXMEM_PipelineRegister() {
		super(73);
	}
}
