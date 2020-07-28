package mipsim.console;

import kotlin.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public final class Main {
	private static final Pair<String, String> bundles[] = new Pair[]{
		new Pair<>("Sum", "sum.asm"),
		new Pair<>("Max", "max.inst.txt"),
		new Pair<>("Fac", "fac.inst.txt"),
		new Pair<>("Fib", "fibo.inst.txt"),
		new Pair<>("Sort", "bubble_sort.inst.txt"),
	};

	public static final Scanner scanner = new Scanner(System.in);

	public static boolean askYesNo(final String message, boolean def) {
		if (message != null) System.out.print(message);
		if (def) {
			System.out.print("([Y]es/[n]o)");
			return !scanner.nextLine().toLowerCase().equals("n");
		} else {
			System.out.print("([y]es/[N]o)");
			return scanner.nextLine().toLowerCase().equals("y");
		}
	}

	public static Integer askInteger(final String message, Integer def) {
		if (message != null) System.out.print(message);
		try {
			return Integer.parseInt(scanner.nextLine());
		} catch (Exception ignored) {
		}
		return def;
	}

	//todo: plz make me
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
				switch (askInteger("Print enter your command: ", -1)) {
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
			for (var i = 0; i < bundles.length; i++) {
				final var bundle = bundles[i];
				System.out.println(String.format("%01d) %s", i + 1, bundle.getFirst()));
			}
			System.out.println(String.format("%01d) %s", 0, "Back to menu"));
			choose = askInteger("please choose program to run:", 0);
			if (choose < 0 || choose > bundles.length) continue;
			if (choose != 0) {
				final var bundle = bundles[choose - 1];
				final var numbers = new ArrayList<Integer>();
				fillMemoryData(numbers);
				final var debugLevel = find_DebugLevel();
				final var isStepByStep = find_stepShow();
				Test.testBundle(bundle, isStepByStep, debugLevel, numbers);
			}
			break;
		}
		if (choose != 0) {
			final var loader = ClassLoader.getSystemClassLoader();
			final var numbers = new ArrayList<Integer>();
			final var bundle = bundles[choose - 1];
			final var path = bundle.getSecond();
			final var file = new File(loader.getResource(path).getFile());
			fillMemoryData(numbers);
			final var debugLevel = find_DebugLevel();
			final var isStepByStep = find_stepShow();
			Test.testCase(isStepByStep, debugLevel, numbers, file);
		}
	}

	public static int find_DebugLevel() {
		System.out.println("--- Debug Levels ---");
		System.out.println("1. Easy   : ");
		System.out.println("2. medium : ");
		System.out.println("3. advance: ");
		return askInteger("please choice Debug level:", 0);
	}

	public static boolean find_stepShow() {
		return askYesNo("Do you like to see step by step of code that you Run in Cpu ?", false);
	}

	public static boolean find_heWantToSaveInMemory() {
		return askYesNo("Do you like to save some information in memory?", false);
	}

	public static void fillMemoryData(ArrayList<Integer> array) {
		array.add(0);
		while (true) {
			var res = askInteger("please Enter a number or empty to end: ", null);
			if (res == null) break;
			array.add(res);
		}
		array.set(0, array.size() - 1);
	}
}
