package mipsim.pipeline;

import mipsim.units.MemBit;
import mipsim.units.Memory;
import mipsim.units.MemoryKt;
import sim.base.BusKt;

import java.util.List;

public final class IDEX_PipelineRegister extends PipelineRegister {
	public final List<MemBit> instruction = BusKt.slice(memory, 0, 32);

	//this will be out put of register --> we use mux before of rt with aluSrc to select right value;32 bit
	public List<MemBit> rsData;
	public List<MemBit> rtData;

	//this is shift and sign extend of 16 bit of immediate value,32 bit
	public List<MemBit> immediate;

	//there would be another mux after to chose right destination with rgDst,5 bit
	public List<MemBit> rtRegister;
	public List<MemBit> rdRegister;

	//this will be shiftMa for alu the number of bit that would be shifted ,5 bit
	public List<MemBit> shiftMa;

	//all control flag will be passed to pipeline
	public MemBit memToReg;
	public MemBit regWrite;
	public MemBit memRead;
	public MemBit memWrite;
	public MemBit aluSrc;
	public List<MemBit> aluOp;//this would be 2 bit

	// note --> we used branch jump  regDst in stage decode




	public IDEX_PipelineRegister() {
		super(
			MemoryKt.WORD_SIZE // instruction
		);
	}
}
