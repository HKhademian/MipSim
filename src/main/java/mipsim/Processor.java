package mipsim;

import mipsim.pipeline.registers.*;
import mipsim.pipeline.stages.*;
import mipsim.sim.ParserKt;
import mipsim.units.DataMemory;
import mipsim.units.InstructionMemory;
import mipsim.units.RegisterFile;
import org.jetbrains.annotations.NotNull;
import sim.base.*;
import sim.tool.DebugKt;
import sim.tool.DebugWriter;

import java.util.List;

public class Processor implements Eval, DebugWriter {
	private List<? extends MutableValue> currentState = BusKt.bus(0);
	private List<? extends MutableValue> nextState = BusKt.bus(0);


	public final Value clock;
	public final InstructionMemory instructionMemory;
	public final DataMemory dataMemory;
	public final RegisterFile registerFile;

	// stages
	public final InstructionFetchStage ifStage;
	public final InstructionDecodeStage idStage;
	public final ExecutionStage exStage;
	public final MemoryStage memStage;
	public final WriteBackStage wbStage;

	// pipeline state
	public final WBIF_PipelineRegister wbif;
	public final IFID_PipelineRegister ifid;
	public final IDEX_PipelineRegister idex;
	public final EXMEM_PipelineRegister exmem;
	public final MEMWB_PipelineRegister memwb;

	public Processor() {
		clock = ValueKt.mut(false);

		instructionMemory = new InstructionMemory(clock, 32);
		dataMemory = new DataMemory(clock, 16);
		registerFile = new RegisterFile(clock);

		ifStage = new InstructionFetchStage(this);
		idStage = new InstructionDecodeStage(this);
		exStage = new ExecutionStage(this);
		memStage = new MemoryStage(this);
		wbStage = new WriteBackStage(this);

		wbif = new WBIF_PipelineRegister(this);
		ifid = new IFID_PipelineRegister(this);
		idex = new IDEX_PipelineRegister(this);
		exmem = new EXMEM_PipelineRegister(this);
		memwb = new MEMWB_PipelineRegister(this);
	}

	@SuppressWarnings("unchecked")
	public List<? extends MutableValue> alloc(final int bitSize, final boolean isTemp) {
		final var memory = BusKt.bus(bitSize);
		if (isTemp) nextState = BusKt.merge(nextState, memory);
		else currentState = BusKt.merge(currentState, memory);
		return memory;
	}

	public void init() {
		// pc.init()
		ifStage.init();
		idStage.init();
		exStage.init();
		memStage.init();
		wbStage.init();

		// wiring here ...
	}

	@Override
	public void eval(final long time) {
		registerFile.eval(time);
		instructionMemory.eval(time);
		dataMemory.eval(time);

		final var snapshotState = BusKt.constant(nextState);
		if (idStage.stallFlag.get()) {
			final var offset = wbif.SIZE + ifid.SIZE;
			BusKt.set(
				BusKt.slice(currentState, offset),
				BusKt.slice(snapshotState, offset)
			);
		} else {
			BusKt.set(currentState, snapshotState);
		}

//		// we have two `eval`s per clock cycle
//		((MutableValue) clock).toggle();
//
//		// i have some eval follow:
//		// 1. all components start to end
//		// 2. all components end to start
//		// 3,4,5,6. first pipeline-regs then stages (each like 1 or 2)
//		// 7,8,9,10. first stages then pipeline-regs (each like 1 or 2)
//		// TODO: we must investigate to realize  which one is correct
//		// plz do not remove this comment, to keep eye one the results
	}

	@Override
	public void writeDebug(@NotNull StringBuffer buffer) {
		final var pc = BusKt.toInt(this.wbif.pc);
		final var instBin = BusKt.toInt(instructionMemory.instruction);
		final var instStr = ParserKt.parseBinToInstruction(instBin);

		buffer.append(String.format("pc pipe: %04xH\t", pc));
		buffer.append(String.format("pc inst: %04xH\t", BusKt.toInt(instructionMemory.pc)));
		buffer.append(String.format("pc ifid: %04xH\n", BusKt.toInt(ifid.pc)));
		buffer.append(String.format("inst read: %08xH = ' %s '\t", instBin, instStr));
		buffer.append(String.format("inst ifid: %08xH\n", BusKt.toInt(ifid.instruction)));
		DebugKt.writeTo(registerFile, buffer);
	}

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();
		System.out.println(processor.ifid);
	}
}
