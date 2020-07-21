package mipsim.pipeline.registers;

import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

public final class IDEX_PipelineRegister extends PipelineRegister {

	//all control flag will be passed to pipeline

	//WB
	public final List<MutableValue> WB = BusKt.slice(memory, 0, 2);
	public final MutableValue memToReg = WB.get(0);
	public final MutableValue regWrite = WB.get(1);

	//MEM
	public final List<MutableValue> MEM = BusKt.slice(memory, 2, 4);
	public final MutableValue memRead = MEM.get(0);
	public final MutableValue memWrite = MEM.get(1);

	//EX
	public final List<MutableValue> EX = BusKt.slice(memory, 4, 8);
	public final MutableValue aluSrc = EX.get(0);
	public final MutableValue regDst = EX.get(1);
	public final List<MutableValue> aluOp = BusKt.slice(EX, 2, 4);//this would be 2 bit
	// note --> we used branch jump  in stage decode



	//function 6 bit
	public final List<MutableValue> function  = BusKt.slice(memory, 8, 14);



	//this will be out put of register --> we use mux before of rt with aluSrc to select right value;32 bit
	public final List<MutableValue> rsData = BusKt.slice(memory, 14, 46);
	public final List<MutableValue> rtData = BusKt.slice(memory, 46, 78);

	//this is shift and sign extend of 16 bit of immediate value,32 bit
	public final List<MutableValue> immediate = BusKt.slice(memory, 78, 110);

	//there would be another mux after to chose right destination with rgDst,5 bit
	public final List<MutableValue> rsRegister = BusKt.slice(memory, 110, 115);
	public final List<MutableValue> rdRegister = BusKt.slice(memory, 115, 120);
	public final List<MutableValue> rtRegister = BusKt.slice(memory, 120, 125);



	//this will be shiftMa for alu the number of bit that would be shifted ,5 bit
	public final List<MutableValue> shiftMa = BusKt.slice(memory, 125, 130);







	public IDEX_PipelineRegister() {
		super(130);
	}
}
