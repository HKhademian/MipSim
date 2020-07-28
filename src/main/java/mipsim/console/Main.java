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

	public static boolean yesNo(final String message, boolean def) {
		System.out.print(message);
		if (def) {
			System.out.print("([Y]es/[n]o)");
			return !scanner.nextLine().toLowerCase().equals("n");
		} else {
			System.out.print("([y]es/[N]o)");
			return scanner.nextLine().toLowerCase().equals("y");
		}
	}

	//todo: plz make me
	public static void main(String... args) {
		System.out.println("*** MipSim ***");
		System.out.println("Welcome to Mips Gate-Level Multi-Cycle Real Simulator");
		System.out.println();

		while (true) {
			System.out.println("1. Run a pre baked program examples/testCases");
			System.out.println("2. Run your Own code from File");
			System.out.println("3. Run Bundled Programs");
			System.out.println("0. Exit from Program");
			System.out.println("---------");
			System.out.print("Print enter your command: ");
			var choice = scanner.nextInt();
			if (choice == 1) {
				final var numbers = new ArrayList<Integer>();
				final var debugLevel = find_DebugLevel();
				final var isStepByStep = find_stepShow();

				if (find_heWantToSaveInMemory()) {
					fillMemoryData(numbers);
				}
				Test.test(isStepByStep, debugLevel, numbers);

			} else if (choice == 2) {
				final var numbers = new ArrayList<Integer>();
				final var debugLevel = find_DebugLevel();
				final var isStepByStep = find_stepShow();
				if (find_heWantToSaveInMemory()) {
					fillMemoryData(numbers);
				}
				Test.testFile(isStepByStep, debugLevel, numbers);
			} else if (choice == 3) {
				int choose;
				while (true) {
					for (var i = 0; i < bundles.length; i++) {
						final var bundle = bundles[i];
						System.out.println(String.format("%01d) %s", i + 1, bundle.getFirst()));
					}
					System.out.println(String.format("%01d) %s", 0, "Back to menu"));
					choose = scanner.nextInt();
					scanner.nextLine();
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
			} else if (choice == 0) {
				System.out.println("Good Bye");
				break;
			} else {
				System.out.println("Wrong Input!");
			}
		}
	}

	public static int find_DebugLevel() {
		System.out.println("please choice Debug level:");
		System.out.println("1.Easy");
		System.out.println("2.medium");
		System.out.println("3.advance");
		int result = scanner.nextInt();
		scanner.nextLine();
		return result;
	}

	public static boolean find_stepShow() {
		return yesNo("Do you like to see step by step of code that you Run in Cpu ?", false);
	}

	public static boolean find_heWantToSaveInMemory() {
		return yesNo("Do you like to save some information in memory?", false);
	}

	public static void fillMemoryData(ArrayList<Integer> array) {
		array.add(0);
		while (true) {
			System.out.print("please Enter a number or empty to end: ");
			try {
				var inp = Integer.parseInt(scanner.nextLine());
				array.add(inp);
			} catch (Exception ex) {
				break;
			}
		}
		array.set(0, array.size() - 1);
	}
}
