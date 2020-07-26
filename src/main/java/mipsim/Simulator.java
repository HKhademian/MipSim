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

	public void loadInstructions(final List<String> instructions) {
		reset();
		ParserKt.loadInstructions(processor.instructionMemory._memory, instructions, true);
	}

	public void loadInstructions(final String... instructions) {
		loadInstructions(Arrays.asList(instructions));
	}

	public void loadInstructions(final File instructionsFile) {
		reset();
		ParserKt.loadInstructions(processor.instructionMemory._memory, instructionsFile, true);
	}

	public void run() {
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 100; i++) {
			if (runStep(i)) break;
		}

		testOn(processor.dataMemory._memory, "dataMemory");
	}

	boolean runStep(int step) {
		testOn(processor, "clock " + step, () -> {
			processor.eval(System.nanoTime());
		});
		return BusKt.toInt(processor.instructionMemory.instruction) == ParserKt.HALT_BIN;
	}
}
