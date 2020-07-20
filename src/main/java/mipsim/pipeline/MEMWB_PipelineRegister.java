package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.MemoryKt;
import sim.base.BusKt;

import java.util.List;

public final class MEMWB_PipelineRegister extends PipelineRegister {
	public final List<MemBit> instruction = BusKt.slice(memory, 0, 32);

	//this would be alu and memory read result,32bit
	public List<MemBit> aluData;
	public List<MemBit> memoryValue;


	//this would be register code that will be write on it,5 bit
	public List<MemBit> rdRegister;


	//all control flag will be passed to pipeline except memRead and memWrite
	public MemBit memToReg;
	public MemBit regWrite;


	public MEMWB_PipelineRegister() {
		super(
			MemoryKt.WORD_SIZE // instruction
		);
	}
}
