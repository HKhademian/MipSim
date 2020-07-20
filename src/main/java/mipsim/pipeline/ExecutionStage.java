package mipsim.pipeline;

import mipsim.Simulator;
import mipsim.module.LogicALU;
import mipsim.units.ForwardingUnit;
import mipsim.units.Multiplexer;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.ValueKt;
import sim.real.MuxKt;

import java.util.Collections;

public class ExecutionStage extends Stage {
	public ExecutionStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {

		/**
		 * in this 2 type code we make enter of alu with forwarding data  and rd and rs data to give them to alu
		 *
		 */

		BusKt.set(simulator.exmem.writeMe,simulator.idex.rtRegister);//I don't know what is this I think it's a rt reg to read or save it's value of memory or save value of memory
		//todo Ask friends

		var resultOneOfAlu = BusKt.bus(32);
		var forwarding1 = BusKt.bus(2);
		ForwardingUnit.forwardingUnitEXHazard(simulator.exmem.regWrite,simulator.exmem.rdRegister,simulator.idex.rsRegister,forwarding1);
		Multiplexer.aluInput(forwarding1,simulator.idex.rsData,simulator.exmem.aluData,simulator.memwb.aluData,resultOneOfAlu);
		// code above have can not detective load hazard detection
		var resultTowOfAlu = BusKt.bus(32);
		var forwarding2 = BusKt.toBus(2);

		var resAluSrc = BusKt.bus(32);
		Multiplexer.aluSrc(simulator.idex.aluSrc,simulator.idex.rtData,simulator.idex.immediate,resAluSrc);
		ForwardingUnit.forwardingUnitEXHazard(simulator.exmem.regWrite,simulator.exmem.rdRegister,simulator.idex.rtRegister,forwarding2);
		Multiplexer.aluInput(forwarding2,resAluSrc,simulator.exmem.aluData,simulator.memwb.aluData,resultOneOfAlu);
		LogicALU.AluInStage(resultOneOfAlu,resultTowOfAlu,simulator.idex.function,simulator.idex.aluOp,simulator.exmem.aluData);

		/**
		 * in this code we save or signal that don't need them in this state
		 */

		Multiplexer.dtRegister(simulator.idex.regDst,simulator.idex.rtRegister,simulator.idex.rdRegister,simulator.exmem.rdRegister);
		simulator.exmem.memToReg.set(simulator.idex.memToReg);
		simulator.exmem.memRead.set(simulator.idex.memRead);
		simulator.exmem.memWrite.set(simulator.idex.memWrite);
		simulator.exmem.regWrite.set(simulator.idex.regWrite);

		// wiring here ...
	}

	@Override
	public void eval() {

	}
}
