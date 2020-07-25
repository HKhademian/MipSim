package mipsim;

import kotlinx.coroutines.flow.internal.NopCollector;
import mipsim.sim.InstructionParserKt;
import sim.tool.DebugKt;

import java.util.Arrays;
import java.util.List;

import static sim.tool.TestKt.testOn;

public class Main {
	final static String NOP = "sll $0, $0, 0";
	static List<String> instructions = Arrays.asList(
		NOP,
		"addi $1, $31, 15",
		NOP,
		"addi $2, $30, 10",
		"add  $3, $2, $1",
		"or   $4, $0, $3",
		"j  0",
		NOP
	);

	public static void main(String[] args) {
		final var processor = new Processor();
		processor.init();
		InstructionParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 15; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}
	}
}
