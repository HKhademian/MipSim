package mipsim.sim;

import mipsim.Processor;
import mipsim.console.Console;
import sim.base.BusKt;

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
		this.processor.instructionMemory._memory.clear(64);
		this.processor.dataMemory._memory.clear(64);
		this.processor.registerFile._memory.clear();
	}

	public void loadDataMemory(final List<Integer> binaries) {
		processor.dataMemory._memory.bulkWrite(binaries);
	}

	public void loadInstructions(final List<String> instructions) {
		ParserKt.loadInstructions(processor, instructions, false);
	}

	public void loadInstructions(final String... instructions) {
		loadInstructions(Arrays.asList(instructions));
	}

	public void loadInstructions(final File instructionsFile) {
		ParserKt.loadInstructions(processor, instructionsFile, false);
	}

	public void run(int debugLevel, boolean stepByStep) {
		testOn(processor.dataMemory, "dataMemory - before run");

		for (var i = 0; ; i++) {
			if (runStep(i, debugLevel)) break;

			if (i > 128) {
				System.out.println("too many cycles runned!");
				break;
			}

			if (stepByStep && !Console.askYesNo("Do tou want to continue?", true)) {
				System.out.println("processor stopped by user!");
				break;
			}
		}

		testOn(processor.registerFile, "registerFile - after run");
		testOn(processor.dataMemory, "dataMemory - after run");
	}

	boolean runStep(int step, int debugLevel) {
		testOn(processor, "clock " + step, () -> {
			processor.eval(System.nanoTime());
		});
		if (BusKt.toInt(processor.instructionMemory.instruction) == ParserKt.HALT_BIN) {
			System.out.println("Processor Halt!");
			return true;
		}
		if (step > 1 && processor.done.get()) {
			System.out.println("Processor Done!");
			return true;
		}
		return false;
	}
}
