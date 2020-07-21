package mipsim.pipeline.stages;

import mipsim.Simulator;
import sim.base.BusKt;
import sim.real.MuxKt;

public class WriteBackStage extends Stage {
	public WriteBackStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
		final var memwb = simulator.memwb;
		final var regFile = simulator.registerFile;

		//choice data memory and alu --> to write data
		final var writeData = MuxKt.mux2(memwb.memToReg, memwb.aluData, memwb.memoryData);
		regFile.regWrite.set(memwb.regWrite);
		BusKt.set(regFile.writeReg, memwb.rdRegister);
		BusKt.set(regFile.writeData, writeData);
	}

	@Override
	public void eval() {

	}
}
