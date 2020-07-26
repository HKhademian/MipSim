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
		// fixme:  BusKt.set(this.processor.instructionMemory._memory, 0);
		// fixme:  BusKt.set(this.processor.dataMemory._memory, 0);
		BusKt.set(this.processor.registerFile._memory, 0);
	}

	public void loadInstructions(final List<String> instructions) {
		reset();
		ParserKt.loadInstructions(processor.instructionMemory, instructions, true);
	}

	public void loadDataMemory(final List<Integer> binaries) {

	}

	public void loadInstructions(final String... instructions) {
		loadInstructions(Arrays.asList(instructions));
	}

	public void loadInstructions(final File instructionsFile) {
		reset();
		// fixme:  ParserKt.loadInstructions(processor.instructionMemory._memory, instructionsFile, true);
	}

	public void run(int debugLevel, boolean stepByStep) {
		DebugKt.println(processor.instructionMemory);

		for (var i = 0; i < 100; i++) {
			if (runStep(i, debugLevel)) break;

			if (stepByStep) {
				System.out.println("If you want to close program Enter? y");
				// fixme: new Scanner(System.in).nextLine().equals("y");
			}
		}

		testOn(processor.dataMemory, "dataMemory");
	}

	boolean runStep(int step, int debugLevel) {
		testOn(processor, "clock " + step, () -> {
			processor.eval(System.nanoTime());
		});
		return BusKt.toInt(processor.instructionMemory.instruction) == ParserKt.HALT_BIN;
	}
}
