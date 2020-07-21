package mipsim;

import mipsim.pipeline.registers.EXMEM_PipelineRegister;
import mipsim.pipeline.registers.IDEX_PipelineRegister;
import mipsim.pipeline.registers.IFID_PipelineRegister;
import mipsim.pipeline.registers.MEMWB_PipelineRegister;
import mipsim.pipeline.stages.*;
import mipsim.units.*;
import sim.base.*;

public class Simulator implements Eval {
	public final Value clock;
	public final Memory pc;
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

	public Simulator() {
		clock = ValueKt.mut(false);
		pc = MemoryKt.createWords(1);
		instructionMemory = new InstructionMemory(this, 100);
		dataMemory = new DataMemory(this, 100);
		registerFile = new RegisterFile(this);

		ifStage = new InstructionFetchStage(this);
		idStage = new InstructionDecodeStage(this);
		exStage = new ExecutionStage(this);
		memStage = new MemoryStage(this);
		wbStage = new WriteBackStage(this);

		ifid = new IFID_PipelineRegister();
		idex = new IDEX_PipelineRegister();
		exmem = new EXMEM_PipelineRegister();
		memwb = new MEMWB_PipelineRegister();
	}

	public void run() {
		//test
		var PC = BusKt.bus(32);
		var ber = BusKt.bus(32);
		var jp = BusKt.bus(32);
		var select = BusKt.bus(2);


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
	public void eval() {
		// we have two `eval`s per clock cycle
		((MutableValue) clock).toggle();

		// i have some eval follow:
		// 1. all components start to end
		// 2. all components end to start
		// 3,4,5,6. first pipeline-regs then stages (each like 1 or 2)
		// 7,8,9,10. first stages then pipeline-regs (each like 1 or 2)
		// TODO: we must investigate to realize  which one is correct
		// plz do not remove this comment, to keep eye one the results

		pc.eval();
		ifStage.eval();
		ifid.eval();
		idStage.eval();
		idex.eval();
		exStage.eval();
		exmem.eval();
		memStage.eval();
		memwb.eval();
		wbStage.eval();
	}
}
