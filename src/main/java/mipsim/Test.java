package mipsim;

import mipsim.sim.ParserKt;
import sim.tool.DebugKt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static mipsim.sim.ParserKt.HALT;
import static mipsim.sim.ParserKt.NOP;
import static sim.tool.TestKt.testOn;

public class Test {
	public static void main(String[] args) {
		int i = 0;
		boolean contuie = true;
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		System.out.println("\n\nHi Lovely\nWelcome to my cpu simulator.");
		while (contuie) {
			System.out.println("\n\nDo you like to test it's power.?\n1.I like to see rType function with" +
				" hazard\n2.Do you like to see how can I sw and lw in memory and I detect Lw hazard for you\n" +
				"3.Time to shift your amount\n" +
				"4.Do you like to see how can I jump to any Address you want\n5.If condition be good I can come back I have Branch backward and forward\n" +
				"6.Time to check set less than\n" +
				"7.Okey I'm good horse you can run any program that you like to run please Entere name of your file\n" +
				"\n8.Exit.");
			int choice = myObj.nextInt();

			switch (choice) {

				case 1:
					testOne();
					break;
				case 2:
					testTwo();
					break;
				case 3:
					testTree();
					break;
				case 4:
					testFour();
					break;
				case 5:
					testFive();
					break;
				case 6:
					testSix();
					break;
				case 7:
					readFile();
					break;
				case 8:
					contuie = false;
					break;
				default:
					System.out.println("your input is wrong");


			}
		}
	}

	public static void testZero() {
		List<String> instructions = Arrays.asList(
			NOP,
			"addi $1, $0, 74",
			"addi $t0, $0, 4",
			NOP,
			NOP,
			"sll $12, $1, 30",
			"sw $1, 0($t0)",
			NOP,
			NOP,
			NOP,
			"lw  $6, 0($t0)",
			"or   $4, $0, $3",
			NOP,
			"addi $8, $6, 15",
			NOP,
			NOP,
			HALT
		);

		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		for (var i = 0; i < 20; i++) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
		}

		testOn(processor.dataMemory._memory, "dataMemory");
	}

	// Create a Scanner object

	/**
	 * in this test we check hazard detection And this fact we can't write in zero register
	 */
	public static void testOne() {
		List<String> instructions = Arrays.asList(
			"addi $1,$zero,10",
			"addi $2,$zero,13",
			"add  $3,$2,$1 ",
			"sub  $3,$3,$2",
			"and  $zero,$3,$1",
			NOP,
			HALT
		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? y");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testTwo() {
		List<String> instructions = Arrays.asList(
			"addi $1,$0,4",
			"sw $1,4($1)",
			"lw $2,4($1)",
			"addi  $3,$2,3",
			NOP,
			HALT
		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? y");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");

	}

	public static void testTree() {
		List<String> instructions = Arrays.asList(
			"addi $1,$0,10",
			"addi $2,$0,13",
			"sll  $4,$1,10",
			"srl  $3,$2,2",
			NOP,
			HALT

		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? y");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testFour() {
		List<String> instructions = Arrays.asList(
			"addi $1,$0,10",
			"addi $2,$0,2",
			"j 5",
			NOP,
			"add  $5,$1,$2 ",
			"add  $6,$1,$2",
			NOP,
			HALT
		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? y");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testFive() {
		List<String> instructions = Arrays.asList(
			"addi $3,$zero,4",
			"beq  $3,$3,3",
			NOP,
			NOP,
			"addi $2,$zero,4",
			"addi $4,$zero,4",
			"beq  $4,$2,-2",
			NOP,
			NOP,
			HALT

		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testSix() {
		List<String> instructions = Arrays.asList(
			"addi $1,$zero,13",
			"addi $2,$zero,10",
			"addi $1,$1,-1",
			"slt  $3,$1,$2",
			"beq  $3,$zero,-3",
			NOP,
			NOP,
			"j 0",
			NOP,
			NOP,
			NOP,
			HALT

		);
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(instructions.get(i));
		}
		System.out.println("Please Enter to see happen that care in cpu");
		var x = new Scanner(System.in).nextLine();
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = false;
		int i = 0;
		while (!clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("If you want to close program Enter? y");
			clockContinue = new Scanner(System.in).nextLine().equals("y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void readFile() {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = new Scanner(System.in).nextLine();
		try {
			Scanner input = new Scanner(new File(fileName));
			final var file = new File(fileName);

			final var processor = new Processor();
			processor.init();
			ParserKt.loadInstructions(processor.instructionMemory._memory, file, false);
			DebugKt.println(processor.instructionMemory._memory);
		} catch (FileNotFoundException s) {
			System.out.println("File does Not Exist Please Try Again: ");
		}

	}
}
