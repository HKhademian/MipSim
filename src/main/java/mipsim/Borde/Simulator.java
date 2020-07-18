package mipsim.Borde;

import mipsim.stage.InstructionDecode;
import sim.base.BusKt;

public class Simulator {
	InstructionDecode instructionDecode;


	public Simulator(int sizeOfMemory ,String fileName)
	{
		instructionDecode = new InstructionDecode(sizeOfMemory);

	}

	public void run()
	{
		//test
		var PC = BusKt.bus(32);
		var ber = BusKt.bus(32);
		var jp = BusKt.bus(32);
		var select = BusKt.bus(2);


	}

}
