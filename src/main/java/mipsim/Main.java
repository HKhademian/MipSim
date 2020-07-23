package mipsim;

import mipsim.sim.InstructionParserKt;

import java.util.Arrays;
import java.util.List;

import static sim.test.TestKt.testOn;

public class Main {
	static List<String> instructions = Arrays.asList(
		"add $t0, $t1, $t2",
		"add $t3, $t0, $t2",
		"add $t4, $t0, $t2"
	);

	public static void main(String[] args) {
		List<String> instructions = Arrays.asList(
			"add $t0, $t1, $t2",
			"add $t3, $t0, $t2",
			"add $t4, $t0, $t2"
		);

		final var processor = new Processor();
		processor.init();
		InstructionParserKt.loadInstructions(processor, instructions);
		System.out.println(processor.instructionMemory._memory);

		testOn(null, "clock 1", () -> {
			final var time = System.nanoTime();

			processor.pc.eval(time);
			processor.ifStage.eval(time);
			processor.ifid.eval(time);
		});

		testOn(null, "clock 2", () -> {
			processor.eval(System.nanoTime());
		});

		testOn(null, "clock 3", () -> {
			processor.eval(System.nanoTime());
		});

	}
}
