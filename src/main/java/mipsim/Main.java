package mipsim;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static final Scanner scanner = new Scanner(System.in);

	//todo: plz make me
	public static void main(String... args) {
		System.out.println("*** MipSim ***");
		System.out.println("Welcome to Mips Gate-Level Multi-Cycle Real Simulator");
		System.out.println();

		while (true) {
			System.out.println("1. Run a pre baked program examples/testCases");
			System.out.println("2. Run your Own code from File");
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
			} else if (choice == 0) {
				System.out.println("Good Bye");
				break;
			} else {
				System.out.println("Wrong Input!");
			}

		}

	}

	//A) input all numbers end with -1
//B) 0: count 1...n:  numbers
//C) load all data mem
//E) Exec
//1. sort
//2. find
//3. fib
//4. sum
//5. max
//6. fac
	public static int find_DebugLevel() {
		System.out.println("please choice Debug level:");
		System.out.println("1.Easy");
		System.out.println("2.medium");
		System.out.println("3.advance");
		return scanner.nextInt();
	}

	public static boolean find_stepShow() {
		System.out.print("Do you like to see step by step of code that you Run in Cpu ? (yes/No)");
		return scanner.nextLine().equals("y");
	}

	public static boolean find_heWantToSaveInMemory() {
		System.out.print("Do you like to save some information in memory? (yes/No)");
		return scanner.nextLine().equals("y");
	}

	public static void fillMemoryData(ArrayList<Integer> array) {
		array.add(0);
		while (true) {
			System.out.print("Do you like to add number to mem? (yes/ No)");
			if (!scanner.nextLine().equals("y")) {
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
