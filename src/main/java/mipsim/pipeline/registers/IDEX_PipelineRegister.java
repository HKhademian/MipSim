package mipsim.pipeline.registers;

import mipsim.Processor;
import org.jetbrains.annotations.NotNull;
import sim.base.BusKt;
import sim.base.Value;
import sim.base.ValueKt;

import java.util.List;

public final class IDEX_PipelineRegister extends PipelineRegister<IDEX_PipelineRegister> {
	public static final int SIZE = 165+64;

	//all control flag will be passed to pipeline

	// WB
	public final List<? extends Value> WB = BusKt.slice(memory, 0, 2);
	public final Value memToReg = WB.get(0);
	public final Value regWrite = WB.get(1);

	//MEM
	public final List<? extends Value> MEM = BusKt.slice(memory, 2, 4);
	public final Value memRead = MEM.get(0);
	public final Value memWrite = MEM.get(1);

	//EX
	public final List<? extends Value> EX = BusKt.slice(memory, 4, 9);
	public final Value aluSrc = EX.get(0);
	public final Value regDst = EX.get(1);
	public final Value branch = EX.get(2);
	public final List<? extends Value> aluOp = BusKt.slice(EX, 3, 5);//this would be 2 bit
	// note --> we used branch jump  in stage decode


	//function 6 bit
	public final List<? extends Value> function = BusKt.slice(memory, 9, 15);


	//this will be out put of register --> we use mux before of rt with aluSrc to select right value;32 bit
	public final List<? extends Value> rsData = BusKt.slice(memory, 15, 47);
	public final List<? extends Value> rtData = BusKt.slice(memory, 47, 79);

	//this is shift and sign extend of 16 bit of immediate value,32 bit
	public final List<? extends Value> immediate = BusKt.slice(memory, 79, 111);

	//there would be another mux after to chose right destination with rgDst,5 bit
	public final List<? extends Value> rsRegister = BusKt.slice(memory, 111, 116);
	public final List<? extends Value> rdRegister = BusKt.slice(memory, 116, 121);
	public final List<? extends Value> rtRegister = BusKt.slice(memory, 121, 126);


	//this will be shiftMa for alu the number of bit that would be shifted ,5 bit
	public final List<? extends Value> shiftMa = BusKt.slice(memory, 126, 131);

	public final List<? extends Value> PC = BusKt.slice(memory, 131, 163);

	public final Value bne = memory.get(163);
	public final Value shift16 = memory.get(164);

	// DEBUG purpos only
	public final List<? extends Value> pc = BusKt.slice(memory, 165, 165+32);//this will be 32 bit for the branch and jump
	public final List<? extends Value> instruction = BusKt.slice(memory, 165+32, 165+64);

	private IDEX_PipelineRegister(final Processor processor, final IDEX_PipelineRegister next) {
		super(processor, SIZE, next);
	}

	public IDEX_PipelineRegister(final Processor processor) {
		this(processor, new IDEX_PipelineRegister(processor, null));
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		final var rdDataBin = BusKt.toInt(this.rtData);
		final var rsDataBin = BusKt.toInt(this.rsData);
		final var rdRegisterBin = BusKt.toInt(this.rdRegister);
		final var ExBin = BusKt.toInt(this.EX);
		final var MEMBin = BusKt.toInt(this.MEM);
		final var aluOpBin = BusKt.toInt(this.aluOp);
		final var immediateBin = BusKt.toInt(this.immediate);
		final var rsRegisterBin = BusKt.toInt(this.rsRegister);
		final var functionBin = BusKt.toInt(this.function);
		final var rtRegisterBin = BusKt.toInt(this.rtRegister);
		final var rtDataBin = BusKt.toInt(this.rtData);
		final var shiftMaBin = BusKt.toInt(this.shiftMa);
		final var WbBin = BusKt.toInt(this.WB);
		final var memToReg = ValueKt.toInt(this.memToReg);
		final var regDst = ValueKt.toInt(this.regDst);
		final var branch = ValueKt.toInt(this.branch);
		final var regWrite = ValueKt.toInt(this.regWrite);
		final var aluSrc = ValueKt.toInt(this.aluSrc);
		final var memWrite = ValueKt.toInt(this.memWrite);
		final var memRead = ValueKt.toInt(this.memRead);

		buffer
			.append(String.format("rdRegister: %d\t", rdRegisterBin))
			.append(String.format("rtRegister: %d\t", rtRegisterBin))
			.append(String.format("rsRegister: %d\t", rsRegisterBin))

			.append(String.format("rdData: %08xH\t", rdDataBin))
			.append(String.format("rtData: %08xH\t", rtDataBin))
			.append(String.format("rdData: %08xH\t", rsDataBin))

			.append(String.format("Ex: %01xH\t", ExBin))
			.append(String.format("MEM: %01xH\t", MEMBin))
			.append(String.format("aluOp: %01xH\t", aluOpBin))
			.append(String.format("immediate: %04xH\t", immediateBin))
			.append(String.format("function: %02xH\t", functionBin))
			.append(String.format("shiftMa: %02xH\t", shiftMaBin))
			.append(String.format("Wb: %02xH\t", WbBin))

			.append(String.format("memToReg: %d\t", memToReg))
			.append(String.format("regDst: %d\t", regDst))
			.append(String.format("branch: %d\t", branch))
			.append(String.format("regWrite: %d\t", regWrite))
			.append(String.format("memRead: %d\t", memRead))
			.append(String.format("aluSrc: %d\t", aluSrc))
			.append(String.format("memWrite: %d\t", memWrite));
	}

}
