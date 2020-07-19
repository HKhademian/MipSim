package mipsim;

import mipsim.stage.InstructionDecodeStage;
import sim.base.BusKt;

public class Simulator {
	InstructionDecodeStage instructionDecodeStage;


	public Simulator(int sizeOfMemory, String fileName) {
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
