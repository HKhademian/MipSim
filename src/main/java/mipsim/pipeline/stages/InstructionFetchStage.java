package mipsim.pipeline.stages;

import mipsim.Processor;
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

	public InstructionFetchStage(final Processor processor) {
		super(processor);
	}

	@Override
	public void init() {
		final var pc = processor.pc;
		final var ifid = processor.ifid;


		// new pc that will show next instruction
		var pc4 = TinyModules.easyAdder(pc, 4);

		// set next pc
		Multiplexer.pcChoice(jump, branch, pc4, branchTarget, jumpTarget, pc);

		// todo: watch stall

		//set pc to read data
		BusKt.set(processor.instructionMemory.pc, pc);

		//set pc and instruction
		BusKt.set(ifid.pc, pc4);
		BusKt.set(ifid.instruction, processor.instructionMemory.instruction);
	}

	@Override
	public void eval() {

	}
	public static void main(String args[]){
		var simulator = new Simulator();
		var x = 20004;
		BusKt.set(simulator.ifid.instruction,BusKt.toBus(x,32));

		BusKt.set(simulator.pc,BusKt.toBus(8,32));

		simulator.pc.eval();
		System.out.println(BusKt.toInt(simulator.pc));
		simulator.ifid.eval();
		System.out.println(BusKt.toInt(simulator.ifid.pc));
		//Todo oh freinds the ifid.pc don't update
		System.out.println(BusKt.toInt(simulator.ifid.instruction));
		//Todo I find the bug it's because of class of public void init this class don't run I think see
	}

}
