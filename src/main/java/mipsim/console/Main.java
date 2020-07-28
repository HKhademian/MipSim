package mipsim.console;

import java.io.File;
import java.util.ArrayList;

public final class Main {

	public static void main(String... args) {
		System.out.println("*** MipSim ***");
		System.out.println("Welcome to Mips Gate-Level Multi-Cycle Real Simulator");
		System.out.println();

		boolean loop = true;
		while (loop) {
			System.out.println("1. Run a pre baked program examples/testCases");
			System.out.println("2. Run your Own code from File");
			System.out.println("3. Run Bundled Programs");
			System.out.println("0. Exit from Program");
			System.out.println("---------");
			boolean ask = true;
			while (ask) {
				switch (Console.askInteger("Print enter your command: ", -1)) {
					case 0 -> {
						ask = false;
						loop = false;
					}
					case 1 -> {
						ask = false;
						runTest();
					}
					case 2 -> {
						ask = false;
						runFile();
					}
					case 3 -> {
						ask = false;
						runProgram();
					}
				}
			}
		}
		System.out.println("Good Bye");
	}

	public static void runTest() {
		final var numbers = new ArrayList<Integer>();
		final var debugLevel = find_DebugLevel();
		final var isStepByStep = find_stepShow();
		if (find_heWantToSaveInMemory()) {
			fillMemoryData(numbers);
		}
		Test.test(isStepByStep, debugLevel, numbers);
	}

	public static void runFile() {
		final var numbers = new ArrayList<Integer>();
		final var debugLevel = find_DebugLevel();
		final var isStepByStep = find_stepShow();
		if (find_heWantToSaveInMemory()) {
			fillMemoryData(numbers);
		}
		Test.testFile(isStepByStep, debugLevel, numbers);
	}

	public static void runProgram() {
		int choose;
		while (true) {
			for (var i = 0; i < Console.bundles.length; i++) {
				final var bundle = Console.bundles[i];
				System.out.println(String.format("%01d) %s", i + 1, bundle.getFirst()));
			}
			System.out.println(String.format("%01d) %s", 0, "Back to menu"));
			choose = Console.askInteger("please choose program to run:", 0);
			if (choose < 0 || choose > Console.bundles.length) continue;
			break;
		}
		if (choose != 0) {
			final var bundle = Console.bundles[choose - 1];
			final var numbers = new ArrayList<Integer>();
			fillMemoryData(numbers);
			final var debugLevel = find_DebugLevel();
			final var isStepByStep = find_stepShow();
			Test.testBundle(bundle, isStepByStep, debugLevel, numbers);
		}
	}

	public static int find_DebugLevel() {
		System.out.println("--- Debug Levels ---");
		System.out.println("1. Easy   : ");
		System.out.println("2. medium : ");
		System.out.println("3. advance: ");
		return Console.askInteger("please choice Debug level:", 0);
	}

	public static boolean find_stepShow() {
		return Console.askYesNo("Do you like to see step by step of code that you Run in Cpu ?", false);
	}

	public static boolean find_heWantToSaveInMemory() {
		return Console.askYesNo("Do you like to save some information in memory?", false);
	}

	public static void fillMemoryData(ArrayList<Integer> array) {
		array.add(0);
		while (true) {
			var res = Console.askInteger("please Enter a number or empty to end: ", null);
			if (res == null) break;
			array.add(res);
		}
		array.set(0, array.size() - 1);
	}
}
