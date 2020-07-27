package mipsim.console;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
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
				Test.readFile(isStepByStep, debugLevel, numbers);
			} else if (choice == 3) {
				// todo:
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
			if (!yesNo("Do you like to add number to mem?", false)) {
				array.add(-1);
				return;
			} else {
				System.out.print("please Enter number: ");
				array.add(scanner.nextInt());
				array.set(0, array.get(0) + 1);
			}
		}
	}
}

//"7.Okey I'm good horse you can run any program that you like to run please Entere name of your file\n" +
