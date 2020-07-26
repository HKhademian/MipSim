package mipsim;

import mipsim.sim.ParserKt;
import sim.tool.DebugKt;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static mipsim.sim.ParserKt.NOP;
import static sim.tool.TestKt.testOn;

public class TestMain {
	  // Create a Scanner object

	/**
	 * in this test we check hazard detection And this fact we can't write in zero register
	 */
	public static void testOne(){
		List<String> instructions = Arrays.asList(
			"addi $1,$zero,10",
			"addi $2,$zero,13",
			"add  $3,$2,$1 ",
			"sub  $3,$3,$2",
			"and  $zero,$3,$1"
		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testTwo() {
		List<String> instructions = Arrays.asList(
			"addi $1,$0,4",
			"sw $1,4($1)",
			"lw $2,4($1)",
			"addi  $3,$2,3"
		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

		}

		testOn(processor.dataMemory, "dataMemory");

	}

	public static void testTree() {
		List<String> instructions = Arrays.asList(
			"addi $1,$0,10",
			"addi $2,$0,13",
			"sll  $4,$1,10",
			"srl  $3,$2,2"

		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

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
			"add  $6,$1,$2"
		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

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
			"addi $2,$zero,4",
			"beq  $4,$2,-2",
			NOP,
			NOP,
			NOP


		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void testSix() {
		List<String> instructions = Arrays.asList(
			"addi $1,$zero,13",
			"addi $2,$zero,10",
			"slt  $3,$1,$2",
			"addi $1,$1,-1",
			"beq  $3,$zero,-2",
			NOP,
			NOP,
			"j 0",
			NOP,
			NOP

		);
		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor, instructions, false);
		DebugKt.println(processor.instructionMemory._memory);

		var clockContinue = true;
		int i = 0;
		while (clockContinue) {
			testOn(processor, "clock " + i, () -> {
				processor.eval(System.nanoTime());
			});
			i++;
			System.out.println("\n Do you like to see next clock cycle ? Y/N");
			clockContinue = new Scanner(System.in).nextLine().equals("Y");

		}

		testOn(processor.dataMemory, "dataMemory");
	}

	public static void readFile() {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = new Scanner(System.in).nextLine();
		final var file = new File(fileName);

		final var processor = new Processor();
		processor.init();
		ParserKt.loadInstructions(processor.instructionMemory._memory, file, false);
		DebugKt.println(processor.instructionMemory._memory);
	}
}




