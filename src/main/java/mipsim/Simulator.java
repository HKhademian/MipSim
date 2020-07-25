package mipsim;

import mipsim.sim.ParserKt;
import sim.base.BusKt;
import sim.tool.DebugKt;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static sim.tool.TestKt.testOn;

public class Simulator {
	public final Processor processor;

	public Simulator() {
		this.processor = new Processor();
		processor.init();
	}

	void reset() {
		BusKt.set(this.processor.currentState, 0);
		BusKt.set(this.processor.nextState, 0);
		BusKt.set(this.processor.instructionMemory._memory, 0);
		BusKt.set(this.processor.dataMemory._memory, 0);
		BusKt.set(this.processor.registerFile._memory, 0);
	}

	void loadInstructions(final List<String> instructions) {
		reset();
		ParserKt.loadInstructions(processor.instructionMemory._memory, instructions, true);
	}

	void loadInstructions(final String... instructions) {
		loadInstructions(Arrays.asList(instructions));
	}

	void loadInstructions(final File instructionsFile) {
		reset();
		ParserKt.loadInstructions(processor.instructionMemory._memory, instructionsFile, true);
	}

	void run() {
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 20; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}

		testOn(processor.dataMemory._memory, "dataMemory");
	}
}
