package mipsim;

import mipsim.sim.InstructionParserKt;
import sim.DebugKt;

import java.util.Arrays;
import java.util.List;

import static sim.test.TestKt.testOn;

public class Main {
	static List<String> instructions = Arrays.asList(
		"addi $1, $0, 15",
		"addi $2, $0, 10",
		"add $3, $t1, $t2",
		"or $4, $0, $3"
	);

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();
		InstructionParserKt.loadInstructions(processor, instructions);
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 10; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}
	}
}
