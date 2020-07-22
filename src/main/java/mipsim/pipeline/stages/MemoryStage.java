package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;

public class MemoryStage extends Stage {
	public MemoryStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var exmem = processor.exmem;
		final var memwb = processor.memwb;
		final var dataMem = processor.dataMemory;

		// write next pipeline register
		BusKt.set(memwb.WB, exmem.WB);
		BusKt.set(memwb.aluData, exmem.aluData);
		BusKt.set(memwb.rdRegister, exmem.rtRegister);


		//read and write data in to the memory
		BusKt.set(dataMem.address, exmem.aluData);
		BusKt.set(dataMem.writeData, exmem.writeMem);
		BusKt.set(dataMem.memWrite, exmem.memWrite);
		BusKt.set(dataMem.memRead, exmem.memRead);
		BusKt.set(memwb.memoryData, dataMem.readData);
	}

	@Override
	public void eval() {

	}
}
