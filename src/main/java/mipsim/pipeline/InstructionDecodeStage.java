package mipsim.pipeline;

import mipsim.Simulator;
import sim.base.BusKt;

public class InstructionDecodeStage extends Stage {
	public InstructionDecodeStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
		// wiring here ...

		var instruction = simulator.ifid.instruction;
		var opcode = BusKt.slice(instruction, 26,32);
		var readReg1 = BusKt.slice(instruction, 21,26);
		var readReg2 = BusKt.slice(instruction, 16,21);
		var writeReg = simulator.memwb.rdRegister;
		var writeData = BusKt.bus(32);

		BusKt.set(simulator.registerFile.readReg1, readReg1);
		BusKt.set(simulator.registerFile.writeReg, writeReg);
		BusKt.set(simulator.registerFile.writeData, writeData);

		BusKt.set(simulator.idex.rsData, simulator.registerFile.readData1);
	}

	@Override
	public void eval() {

	}
}
