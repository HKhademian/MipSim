package mipsim;

import mipsim.pipeline.*;
import mipsim.units.DataMemory;
import mipsim.units.InstructionMemory;
import mipsim.units.RegisterFile;
import sim.base.BusKt;
import sim.base.Eval;

public class Simulator implements Eval {
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

	}
}
