package mipsim.pipeline.stages;

import mipsim.Processor;
import mipsim.module.TinyModules;
import mipsim.module.Multiplexer;
import sim.base.BusKt;
import sim.base.MutableValue;
import sim.base.ValueKt;
import sim.test.TestKt;

import java.util.List;

import static mipsim.sim.InstructionParserKt.parseInstructionToBin;

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

	/**
	 * test in progress by: mehdi
	 */
	public static void main(final String... args) {
		final var processor = new Processor();

//		BusKt.set(processor.ifid.instruction, BusKt.toBus(2004l, 32));
//
//		BusKt.set(processor.pc, BusKt.toBus(8, 32));
//
//		processor.pc.eval();
//		System.out.println(BusKt.toInt(processor.pc));
//		processor.ifid.eval();
//		System.out.println(BusKt.toInt(processor.ifid.pc));
//		System.out.println(BusKt.toInt(processor.ifid.instruction));






		TestKt.testOn(processor.ifid, "test beq", () -> {
			var instBin = parseInstructionToBin("beq $s1,$t1,1");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();

		});

		TestKt.testOn(processor.ifid, "test set less than", () -> {
			var instBin = parseInstructionToBin("slt $s2,$t7,$5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});

//		TestKt.testOn(processor.idex, "test shiftR", () -> {
//			var instBin = parseInstructionToBin("slr $s1,$s3,4");
//			var inst = BusKt.toBus(instBin);
//			BusKt.set(processor.ifid.instruction, inst);
//			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
//			processor.ifid.eval();
//		});
		//todo hossain check it's shift right

		TestKt.testOn(processor.ifid, "test shiftL", () -> {
			var instBin = parseInstructionToBin("sll $s1,$t1,6");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});

		TestKt.testOn(processor.ifid, "test and", () -> {
			var instBin = parseInstructionToBin("and $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});


		TestKt.testOn(processor.ifid, "test or", () -> {
			var instBin = parseInstructionToBin("or $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});


		TestKt.testOn(processor.ifid, "test sub", () -> {
			var instBin = parseInstructionToBin("sub $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});


		TestKt.testOn(processor.ifid, "test addi", () -> {
			var instBin = parseInstructionToBin("addi $s1,$zero,5");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});


		TestKt.testOn(processor.ifid, "test add", () -> {
			var instBin = parseInstructionToBin("add $s1,$t1,$t2");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});



		TestKt.testOn(processor.ifid, "test SW", () -> {
			var instBin = parseInstructionToBin("sw $t1,6($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});

		TestKt.testOn(processor.ifid, "test LW", () -> {
			var instBin = parseInstructionToBin("lw $t1,5($t2)");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});

		TestKt.testOn(processor.ifid, "Jump ", () -> {

			var instBin = parseInstructionToBin("j 50");
			var inst = BusKt.toBus(instBin);
			BusKt.set(processor.ifid.instruction, inst);
			BusKt.set(processor.ifid.pc, BusKt.toBus(20, 32));
			processor.pc.eval();
			processor.ifid.eval();
		});


	}

}
