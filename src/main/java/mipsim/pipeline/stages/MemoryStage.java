package mipsim.pipeline.stages;

import mipsim.Processor;
import sim.base.BusKt;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MemoryStage extends Stage {
	public MemoryStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var exmem = processor.exmem;
		final var memwb = processor.memwb;
		final var dataMem = processor.dataMemory;
		final var EXMEM = processor.exmem.next;
		final var MEMWB = processor.memwb.next;
		final var DATAMEM = processor.dataMemory;
		assert EXMEM != null;
		assert MEMWB != null;
		assert DATAMEM != null;

		// write next pipeline register
		BusKt.set((List) MEMWB.WB, exmem.WB);
		BusKt.set((List) MEMWB.aluData, exmem.aluData);
		BusKt.set((List) MEMWB.rdRegister, exmem.rtRegister);

		//read and write data in to the memory
		// todo: check datamem
		BusKt.set(DATAMEM.address, exmem.aluData);
		BusKt.set(DATAMEM.writeData, exmem.writeMem);
		BusKt.set(DATAMEM.memWrite, exmem.memWrite);
		BusKt.set(DATAMEM.memRead, exmem.memRead);

		BusKt.set((List) MEMWB.memoryData, dataMem.readData);
	}
}
