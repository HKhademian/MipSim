package mipsim.pipeline;

import mipsim.Simulator;
import sim.base.BusKt;

public class MemoryStage extends Stage {
	public MemoryStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
		final var exmem = simulator.exmem;
		final var memwb = simulator.memwb;
		final var dataMem = simulator.dataMemory;

		// write next pipeline register
		BusKt.set(memwb.WB, exmem.WB);
		BusKt.set(memwb.aluData, exmem.aluData);
		BusKt.set(memwb.rdRegister, exmem.rdRegister);
		BusKt.set(memwb.memoryData, dataMem.readData);

		BusKt.set(dataMem.address, exmem.aluData);
		BusKt.set(dataMem.writeData, exmem.writeMem);
		BusKt.set(dataMem.memWrite, exmem.memWrite);
		BusKt.set(dataMem.memRead, exmem.memWrite);
	}

	@Override
	public void eval() {

	}
}
