package mipsim.pipeline.registers;

import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.MutableValue;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseBinToInstruction;

public final class IDEX_PipelineRegister extends PipelineRegister {

	//all control flag will be passed to pipeline

	// WB
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
	public final List<MutableValue> function = BusKt.slice(memory, 8, 14);


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



	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		var rdDataBin = BusKt.toInt(this.rtData);
		var rdDataStr = parseBinToInstruction(rdDataBin);

		var rsDataBin = BusKt.toInt(this.rsData);
		var rsDataStr = parseBinToInstruction(rsDataBin);


		var rdRegisterBin = BusKt.toInt(this.rdRegister);
		var rdRegisterStr = parseBinToInstruction(rdRegisterBin);

		var ExBin = BusKt.toInt(this.EX);
		var ExStr = parseBinToInstruction(ExBin);

		var MEMBin = BusKt.toInt(this.MEM);
		var MEMStr = parseBinToInstruction(MEMBin);

		var aluOpBin = BusKt.toInt(this.aluOp);
		var aluOpStr = parseBinToInstruction(aluOpBin);

		var immediateBin = BusKt.toInt(this.immediate);
		var immediateStr = parseBinToInstruction(immediateBin);

		var rsRegisterBin = BusKt.toInt(this.rsRegister);
		var rsRegisterStr = parseBinToInstruction(rsRegisterBin);

		var functionBin = BusKt.toInt(this.function);
		var functionStr = parseBinToInstruction(functionBin);

		var rtRegisterBin = BusKt.toInt(this.rtRegister);
		var rtRegisterStr = parseBinToInstruction(rtRegisterBin);
		var rtDataBin = BusKt.toInt(this.rtData);
		var rtDataStr = parseBinToInstruction(rtRegisterBin);


		var shiftMaBin = BusKt.toInt(this.shiftMa);
		var shiftMaStr = parseBinToInstruction(shiftMaBin);

		var WbBin = BusKt.toInt(this.WB);
		var WbStr = parseBinToInstruction(WbBin);

		var memToReg = this.memToReg;
		var regDst   = this.regDst ;
		var regWrite = this.regWrite;
		var aluSrc	 = this.aluSrc;
		var	memWrite = this.memWrite;
		var memRead	 =	this.memRead;



		buffer

			.append(String.format("rdData: %08xH = ' %s '\t", rdDataBin,rdDataStr))
			.append(String.format("rdRegister: %08xH = ' %s '\t",rdRegisterBin,rdRegisterStr))

			.append(String.format("rtData: %08xH = ' %s '\t",rtDataBin,rtDataStr))
			.append(String.format("rtRegister: %08xH = ' %s '\t",rtRegisterBin,rtRegisterStr))


			.append(String.format("rsRegister: %08xH = ' %s '\t",rsRegisterBin,rsRegisterStr))
			.append(String.format("rdData: %08xH = ' %s '\t",rsDataBin,rsDataStr))


			.append(String.format("Ex: %08xH = ' %s '\t",ExBin,ExStr))
			.append(String.format("MEM: %08xH = ' %s '\t",MEMBin,MEMStr))
			.append(String.format("aluOp: %8xH = ' %s '\t",aluOpBin,aluOpStr))
			.append(String.format("immediate: %08xH = ' %s '\t",immediateBin,immediateStr))
			.append(String.format("function: %08xH = ' %s '\t",functionBin,functionStr))
			.append(String.format("shiftMa: %08xH = ' %s '\t",shiftMaBin,shiftMaStr))
			.append(String.format("WbBin: %08xH = ' %s '\t",WbBin,WbStr))

			.append(String.format("memToReg: \t", memToReg))
			.append(String.format("regDst: \t",regDst))
			.append(String.format("regWrite: \t",regWrite))
			.append(String.format("memRead: \t",memRead))
			.append(String.format("aluSrc: \t",aluSrc))
			.append(String.format("memWrite: \t",memWrite));
	}


}
