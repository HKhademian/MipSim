package mipsim.pipeline.stages;

import mipsim.Simulator;
import mipsim.module.TinyModules;
import mipsim.units.Multiplexer;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.ValueKt;

import java.util.List;

public class InstructionFetchStage extends Stage {
	public final List<MutableValue> branchTarget = BusKt.bus(32);
	public final List<MutableValue> jumpTarget = BusKt.bus(32);
	public final MutableValue stall = ValueKt.mut();
	public final MutableValue branch = ValueKt.mut();
	public final MutableValue jump = ValueKt.mut();

	public InstructionFetchStage(final Simulator simulator) {
		super(simulator);
	}

	@Override
	public void init() {
		final var pc = simulator.pc;
		final var ifid = simulator.ifid;


		// new pc that will show next instruction
		var pc4 = TinyModules.easyAdder(pc, 4);

		// set next pc
		Multiplexer.pcChoice(jump, branch, pc4, branchTarget, jumpTarget, pc);

		// todo: watch stall

		//set pc to read data
		BusKt.set(simulator.instructionMemory.pc, pc);

		//set pc and instruction
		BusKt.set(ifid.pc, pc4);
		BusKt.set(ifid.instruction, simulator.instructionMemory.instruction);
	}

	@Override
	public void eval() {

	}
}
