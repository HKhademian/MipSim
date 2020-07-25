package mipsim;

import mipsim.sim.ParserKt;
import sim.tool.DebugKt;

import java.io.File;
import java.util.Scanner;

import static sim.tool.TestKt.testOn;

public class Main {
	public static void main(String[] args) {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = new Scanner(System.in).nextLine();
		final var file = new File(fileName);

		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor.instructionMemory._memory, file, false);
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 20; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}

		testOn(processor.dataMemory._memory, "dataMemory");
	}
}
