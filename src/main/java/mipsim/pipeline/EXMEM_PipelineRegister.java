package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.MemoryKt;
import sim.base.BusKt;

import java.util.List;

public final class EXMEM_PipelineRegister extends PipelineRegister {
	public final List<MemBit> instruction = BusKt.slice(memory, 0, 32);

	//this would be alu result and value that would be write on memory,32bit
	public List<MemBit> aluData;
	public List<MemBit> writeMe;

	//this would be register code that will be write on it,5 bit
	public List<MemBit> rdRegister;


	//all control flag will be passed to pipeline except aluSrc and aluOp
	public MemBit memToReg;
	public MemBit regWrite;
	public MemBit memRead;
	public MemBit memWrite;


	// note --> we used aluOp  in stage execution

	public EXMEM_PipelineRegister() {
		super(
			MemoryKt.WORD_SIZE // instruction
		);
	}
}
