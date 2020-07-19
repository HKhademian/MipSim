package mipsim;

import mipsim.pipeline.*;
import mipsim.units.*;
import sim.base.BusKt;
import sim.base.Eval;

public class Simulator implements Eval {
	final Memory pc;
	final InstructionMemory instructionMemory;
	final DataMemory dataMemory;
	final RegisterFile registerFile;

	// stages
	final InstructionFetchStage ifStage;
	final InstructionDecodeStage idStage;
	final ExecutionStage exStage;
	final MemoryStage memStage;
	final WriteBackStage wbStage;

	//register pipelines
	final IFID_PipelineRegister ifid;
	final IDEX_PipelineRegister idex;
	final EXMEM_PipelineRegister exmem;
	final MEMWB_PipelineRegister memwb;

	public Simulator() {
		pc = MemoryKt.createWords(1);
		instructionMemory = new InstructionMemory(100);
		dataMemory = new DataMemory(100);
		registerFile = new RegisterFile();

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

	@Override
	public void eval() {
		// i have some eval follow:
		// 1. all components start to end
		// 2. all components end to start
		// 3,4,5,6. first pipeline-regs then stages (each like 1 or 2)
		// 7,8,9,10. first stages then pipeline-regs (each like 1 or 2)
		// TODO: we must investigate to relize  which one is correct
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
