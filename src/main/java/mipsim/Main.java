package mipsim;

import mipsim.sim.InstructionParserKt;

import java.util.Arrays;
import java.util.List;

public class Main {
	static List<String> instructions = Arrays.asList(
		"add $t0, $t1, $t2",
		"add $t3, $t0, $t2",
		"add $t4, $t0, $t2"
	);

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();

		InstructionParserKt.loadInstructions(processor, instructions);

		processor.eval(System.nanoTime());
		processor.eval(System.nanoTime());
		processor.eval(System.nanoTime());
		processor.eval(System.nanoTime());
	}
}
