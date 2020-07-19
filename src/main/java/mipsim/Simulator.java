package mipsim;

import mipsim.stage.InstructionDecodeStage;
import mipsim.units.DataMemory;
import mipsim.units.InstructionMemory;
import mipsim.units.RegisterFile;
import sim.base.BusKt;

public class Simulator {
	final InstructionMemory instructionMemory;
	final DataMemory dataMemory;
	final RegisterFile registerFile;

	// stages
	final InstructionDecodeStage instructionDecodeStage;

	public Simulator() {
		instructionMemory = new InstructionMemory(100);
		dataMemory = new DataMemory(100);
		registerFile = new RegisterFile();

		instructionDecodeStage = new InstructionDecodeStage();
	}

	public void run() {
		//test
		var PC = BusKt.bus(32);
		var ber = BusKt.bus(32);
		var jp = BusKt.bus(32);
		var select = BusKt.bus(2);


	}

}
