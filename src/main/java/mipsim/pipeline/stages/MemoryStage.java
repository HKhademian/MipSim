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
		final var ExMEM = processor.exmem.next;
		final var memwb = processor.memwb;
		final var MEMWB = processor.memwb.next;
		final var dataMem = processor.dataMemory;
		final var DATAMEM = processor.dataMemory;
		assert ExMEM != null;
		assert MEMWB != null;


		// write next pipeline register
		BusKt.set(MEMWB.WB, exmem.WB);
		BusKt.set(MEMWB.aluData, exmem.aluData);
		BusKt.set(MEMWB.rdRegister, exmem.rtRegister);


		//read and write data in to the memory
		BusKt.set(DATAMEM.address, exmem.aluData);
		BusKt.set(DATAMEM.writeData, exmem.writeMem);
		BusKt.set(DATAMEM.memWrite, exmem.memWrite);
		BusKt.set(DATAMEM.memRead, exmem.memRead);
		BusKt.set(MEMWB.memoryData, dataMem.readData);
	}
}
