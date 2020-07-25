package mipsim;

import mipsim.sim.InstructionParserKt;
import sim.tool.DebugKt;

import java.util.Arrays;
import java.util.List;

import static sim.tool.TestKt.testOn;

public class Main {
	final static String NOP = "sll $0, $0, 0";
	static List<String> instructions = Arrays.asList(
		NOP,
		"addi $1, $0, 27",
		"addi $t0, $0, 0",
		NOP,
		NOP,
		NOP,
		"sw $1, 0($t0)",
		NOP,
		NOP,
		NOP,
		"lw  $6, 0($t0)",
		"or   $4, $0, $3",
		NOP,
		"addi $8, $6, 15",
		NOP
	);

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();
		InstructionParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 20; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}

		testOn(processor.dataMemory, "dataMemory");
	}
}
