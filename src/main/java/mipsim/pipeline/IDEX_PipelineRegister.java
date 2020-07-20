package mipsim.pipeline;

import mipsim.units.MemBit;
import sim.base.BusKt;

import java.util.List;

public final class IDEX_PipelineRegister extends PipelineRegister {
	//this will be out put of register --> we use mux before of rt with aluSrc to select right value;32 bit
	public final List<MemBit> rsData = BusKt.slice(memory, 0, 32);
	public final List<MemBit> rtData = BusKt.slice(memory, 32, 64);

	//this is shift and sign extend of 16 bit of immediate value,32 bit
	public final List<MemBit> immediate = BusKt.slice(memory, 64, 96);

	//there would be another mux after to chose right destination with rgDst,5 bit
	public final List<MemBit> rtRegister = BusKt.slice(memory, 96, 101);
	public final List<MemBit> rdRegister = BusKt.slice(memory, 101, 106);

	//all control flag will be passed to pipeline
	public final MemBit memToReg = memory.get(106);
	public final MemBit regWrite = memory.get(107);
	public final MemBit memRead = memory.get(108);
	public final MemBit memWrite = memory.get(109);
	public final MemBit aluSrc = memory.get(110);
	public final List<MemBit> aluOp = BusKt.slice(memory, 111, 113);//this would be 2 bit

	//this will be shiftMa for alu the number of bit that would be shifted ,5 bit
	public final List<MemBit> shiftMa = BusKt.slice(memory, 113, 118);

	// note --> we used branch jump  regDst in stage decode
	public final List<MemBit> rsRegister = BusKt.slice(memory, 118, 123);
	public final List<MemBit> function  = BusKt.slice(memory, 123, 128);

	public IDEX_PipelineRegister() {
		super(123);
	}
}
