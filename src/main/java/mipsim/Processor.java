package mipsim;

import mipsim.pipeline.registers.EXMEM_PipelineRegister;
import mipsim.pipeline.registers.IDEX_PipelineRegister;
import mipsim.pipeline.registers.IFID_PipelineRegister;
import mipsim.pipeline.registers.MEMWB_PipelineRegister;
import mipsim.pipeline.stages.*;
import mipsim.units.DataMemory;
import mipsim.units.InstructionMemory;
import mipsim.units.MemoryKt;
import mipsim.units.RegisterFile;
import sim.base.*;

import java.util.List;

public class Processor implements Eval {
	private List<? extends MutableValue> currentState = BusKt.bus(0);
	private List<? extends MutableValue> nextState = BusKt.bus(0);

	public final Value clock;
	public final List<? extends MutableValue> pc;
	public final InstructionMemory instructionMemory;
	public final DataMemory dataMemory;
	public final RegisterFile registerFile;

	// stages
	public final InstructionFetchStage ifStage;
	public final InstructionDecodeStage idStage;
	public final ExecutionStage exStage;
	public final MemoryStage memStage;
	public final WriteBackStage wbStage;

	//register pipelines
	public final IFID_PipelineRegister ifid;
	public final IDEX_PipelineRegister idex;
	public final EXMEM_PipelineRegister exmem;
	public final MEMWB_PipelineRegister memwb;

	public Processor() {
		clock = ValueKt.mut(false);
		pc = MemoryKt.createWords(1);
		instructionMemory = new InstructionMemory(clock, 128);
		dataMemory = new DataMemory(clock, 128);
		registerFile = new RegisterFile(clock);

		ifStage = new InstructionFetchStage(this);
		idStage = new InstructionDecodeStage(this);
		exStage = new ExecutionStage(this);
		memStage = new MemoryStage(this);
		wbStage = new WriteBackStage(this);

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
		BusKt.set(currentState, BusKt.constant(nextState));

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
//
//		EvalKt.eval(pc, time);
//		EvalKt.eval(ifStage, time);
//		EvalKt.eval(ifid, time);
//		EvalKt.eval(idStage, time);
//		EvalKt.eval(idex, time);
//		EvalKt.eval(exStage, time);
//		EvalKt.eval(exmem, time);
//		EvalKt.eval(memStage, time);
//		EvalKt.eval(memwb, time);
//		EvalKt.eval(wbStage, time);
	}

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();
		System.out.println(processor.ifid);
	}
}
