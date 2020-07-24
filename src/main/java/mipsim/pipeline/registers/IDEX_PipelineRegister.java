package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.ValueKt;

import java.util.List;

public final class IDEX_PipelineRegister extends PipelineRegister<IDEX_PipelineRegister> {

	//all control flag will be passed to pipeline

	// WB
	public final List<? extends MutableValue> WB = BusKt.slice(memory, 0, 2);
	public final MutableValue memToReg = WB.get(0);
	public final MutableValue regWrite = WB.get(1);

	//MEM
	public final List<? extends MutableValue> MEM = BusKt.slice(memory, 2, 4);
	public final MutableValue memRead = MEM.get(0);
	public final MutableValue memWrite = MEM.get(1);

	//EX
	public final List<? extends MutableValue> EX = BusKt.slice(memory, 4, 9);
	public final MutableValue aluSrc = EX.get(0);
	public final MutableValue regDst = EX.get(1);
	public final MutableValue branch = EX.get(2);
	public final List<? extends MutableValue> aluOp = BusKt.slice(EX, 3, 5);//this would be 2 bit
	// note --> we used branch jump  in stage decode


	//function 6 bit
	public final List<? extends MutableValue> function = BusKt.slice(memory, 9, 15);


	//this will be out put of register --> we use mux before of rt with aluSrc to select right value;32 bit
	public final List<? extends MutableValue> rsData = BusKt.slice(memory, 15, 47);
	public final List<? extends MutableValue> rtData = BusKt.slice(memory, 47, 79);

	//this is shift and sign extend of 16 bit of immediate value,32 bit
	public final List<? extends MutableValue> immediate = BusKt.slice(memory, 79, 111);

	//there would be another mux after to chose right destination with rgDst,5 bit
	public final List<? extends MutableValue> rsRegister = BusKt.slice(memory, 111, 116);
	public final List<? extends MutableValue> rdRegister = BusKt.slice(memory, 116, 121);
	public final List<? extends MutableValue> rtRegister = BusKt.slice(memory, 121, 126);


	//this will be shiftMa for alu the number of bit that would be shifted ,5 bit
	public final List<? extends MutableValue> shiftMa = BusKt.slice(memory, 126, 131);

	public final List<? extends MutableValue> PC = BusKt.slice(memory, 131, 163);

	private IDEX_PipelineRegister(final Processor processor,final IDEX_PipelineRegister next) {
		super(processor,130, next);
	}

	public IDEX_PipelineRegister(final Processor processor) {
		this(processor, new IDEX_PipelineRegister(processor, null));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		var rdDataBin = BusKt.toInt(this.rtData);
		var rsDataBin = BusKt.toInt(this.rsData);
		var rdRegisterBin = BusKt.toInt(this.rdRegister);
		var ExBin = BusKt.toInt(this.EX);
		var MEMBin = BusKt.toInt(this.MEM);
		var aluOpBin = BusKt.toInt(this.aluOp);
		var immediateBin = BusKt.toInt(this.immediate);
		var rsRegisterBin = BusKt.toInt(this.rsRegister);
		var functionBin = BusKt.toInt(this.function);
		var rtRegisterBin = BusKt.toInt(this.rtRegister);
		var rtDataBin = BusKt.toInt(this.rtData);
		var shiftMaBin = BusKt.toInt(this.shiftMa);
		var WbBin = BusKt.toInt(this.WB);
		var memToReg = ValueKt.toInt(this.memToReg);
		var regDst = ValueKt.toInt(this.regDst);
		var branch = ValueKt.toInt(this.branch);
		var regWrite = ValueKt.toInt(this.regWrite);
		var aluSrc = ValueKt.toInt(this.aluSrc);
		var memWrite = ValueKt.toInt(this.memWrite);
		var memRead = ValueKt.toInt(this.memRead);

		buffer
			.append(String.format("rdData: %08xH\t", rdDataBin))
			.append(String.format("rdRegister: %08xH\t", rdRegisterBin))

			.append(String.format("rtData: %08xH\t", rtDataBin))
			.append(String.format("rtRegister: %08xH\t", rtRegisterBin))

			.append(String.format("rsRegister: %08xH\t", rsRegisterBin))
			.append(String.format("rdData: %08xH\t", rsDataBin))

			.append(String.format("Ex: %08xH\t", ExBin))
			.append(String.format("MEM: %08xH\t", MEMBin))
			.append(String.format("aluOp: %8xH\t", aluOpBin))
			.append(String.format("immediate: %08xH\t", immediateBin))
			.append(String.format("function: %08xH\t", functionBin))
			.append(String.format("shiftMa: %08xH\t", shiftMaBin))
			.append(String.format("WbBin: %08xH\t", WbBin))

			.append(String.format("memToReg: %d\t", memToReg))
			.append(String.format("regDst: %d\t", regDst))
			.append(String.format("branch: %d\t", branch))
			.append(String.format("regWrite: %d\t", regWrite))
			.append(String.format("memRead: %d\t", memRead))
			.append(String.format("aluSrc: %d\t", aluSrc))
			.append(String.format("memWrite: %d\t", memWrite));
	}

}
