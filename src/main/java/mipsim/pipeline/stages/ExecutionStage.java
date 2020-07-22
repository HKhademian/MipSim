package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;
import mipsim.units.ForwardingUnit;
import mipsim.module.Multiplexer;
import sim.base.BusKt;

import static sim.gates.GatesKt.or;

public class ExecutionStage extends Stage {
	public ExecutionStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var idex = processor.idex;
		final var exmem = processor.exmem;
		final var memwb = processor.memwb;

		BusKt.set(exmem.WB, idex.WB);
		BusKt.set(exmem.MEM, idex.MEM);

		/*
		 * in this 2 type code we make enter of alu with forwarding data  and rd and rs data to give them to alu
		 *
		 */

		// todo: wrong
		BusKt.set(exmem.writeMem, idex.rtRegister);//I don't know what is this I think it's a rt reg to read or save it's value of memory or save value of memory
		//todo Ask friends

		var resultOneOfAlu = BusKt.bus(32);
		var forwardingEx1 = BusKt.bus(2);
		var forwardingMem1 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite,exmem.rtRegister, idex.rsRegister, forwardingEx1);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem1);

		Multiplexer.aluInput(or(forwardingEx1,forwardingMem1), idex.rsData, exmem.aluData, memwb.aluData, resultOneOfAlu);

		var resultTowOfAlu = BusKt.bus(32);
		var forwardingExe2 = BusKt.bus(2);
		var forwardingMem2 = BusKt.bus(2);
		var resAluSrc = BusKt.bus(32);
		Multiplexer.aluSrc(idex.aluSrc, idex.rtData, idex.immediate, resAluSrc);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rtRegister, forwardingExe2);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem2);

		Multiplexer.aluInput(or(forwardingEx1,forwardingMem1), idex.rsData, exmem.aluData, memwb.aluData, resultTowOfAlu);
		LogicALU.AluInStage(resultOneOfAlu, resultTowOfAlu, idex.function, idex.aluOp, exmem.aluData);

		/* note:
		 * in this code we save or signal that don't need them in this state
		 */

		Multiplexer.dtRegister(idex.regDst, idex.rtRegister, idex.rdRegister, exmem.rtRegister);

	}

	@Override
	public void eval() {

	}
}
