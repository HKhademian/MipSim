package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.LogicALU;
import mipsim.units.AluControlUnit;
import mipsim.units.ControlUnit;
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




		//alu control unit
		var aluOp = BusKt.bus(4);
		AluControlUnit.aluControlUnit(idex.aluOp,idex.function,aluOp);




		//first alu src
		var resultOneOfAlu = BusKt.bus(32);
		var forwardingEx1 = BusKt.bus(2);
		var forwardingMem1 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite,exmem.rtRegister, idex.rsRegister, forwardingEx1);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem1);
		Multiplexer.aluInput(or(forwardingEx1,forwardingMem1), idex.rsData, exmem.aluData, memwb.memoryData, resultOneOfAlu);

		//second alu  src
		var forwardingResult2 = BusKt.bus(32);
		var forwardingExe2 = BusKt.bus(2);
		var forwardingMem2 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(exmem.regWrite, exmem.rtRegister, idex.rtRegister, forwardingExe2);
		ForwardingUnit.forwardingUnitMEMHazard(memwb.regWrite,memwb.rdRegister,idex.rsRegister,exmem.regWrite,exmem.rtRegister,forwardingMem2);
		Multiplexer.aluInput(or(forwardingEx1,forwardingMem1), idex.rsData, exmem.aluData, memwb.memoryData, forwardingResult2);

		var resultTwoOfAlu = BusKt.bus(32);
		Multiplexer.aluSrc(idex.aluSrc,forwardingResult2,idex.immediate,resultTwoOfAlu);

		//alu result
		var aluData = BusKt.bus(32);
		LogicALU.AluInStage(resultOneOfAlu, resultTwoOfAlu, aluOp, idex.shiftMa, aluData);
		BusKt.set(exmem.aluData,aluData);



		//dt register
		var rdRegister = BusKt.bus(5);
		Multiplexer.dtRegister(idex.regDst,idex.rtRegister,idex.rdRegister,rdRegister);
		//set
		BusKt.set(exmem.rtRegister,rdRegister);

		//set flags that passed
		BusKt.set(exmem.WB, idex.WB);
		BusKt.set(exmem.MEM, idex.MEM);

		//set write memory
		BusKt.set(exmem.writeMem, idex.WB);

	}

	@Override
	public void eval() {

	}

	public static void main(String[] args) {
		Processor processor = new Processor();
		processor.exStage.init();


	}
}
