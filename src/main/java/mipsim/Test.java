package mipsim;

import kotlin.io.FilesKt;
import kotlin.text.Charsets;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static mipsim.sim.ParserKt.HALT;
import static mipsim.sim.ParserKt.NOP;

public class Test {
	public static void test(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		boolean repeat = true;
		while (repeat) {
			System.out.println("\n\nDo you like to test it's power.?\n1.I like to see rType function with" +
				" hazard\n2.Do you like to see how can I sw and lw in memory and I detect Lw hazard for you\n" +
				"3.Time to shift your amount\n" +
				"4.Do you like to see how can I jump to any Address you want\n5.If condition be good I can come back I have Branch backward and forward\n" +
				"6.Time to check set less than\n" +

				"\n8.Back to before stage.");

			switch (Main.scanner.nextInt()) {
				case 0 -> testZero(stepByStep, debugLevel, MemoryInformation);
				case 1 -> testOne(stepByStep, debugLevel, MemoryInformation);
				case 2 -> testTwo(stepByStep, debugLevel, MemoryInformation);
				case 3 -> testTree(stepByStep, debugLevel, MemoryInformation);
				case 4 -> testFour(stepByStep, debugLevel, MemoryInformation);
				case 5 -> testFive(stepByStep, debugLevel, MemoryInformation);
				case 6 -> testSix(stepByStep, debugLevel, MemoryInformation);
				case 8 -> repeat = false;
				default -> System.out.println("your input is wrong!");
			}
		}
	}

	public static void testZero(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
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
			"addi $8, $6, 15"
		);
	}

	/**
	 * in this test we check hazard detection And this fact we can't write in zero register
	 */
	public static void testOne(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $1,$zero,10",
			"addi $2,$zero,13",
			"add  $3,$2,$1 ",
			"sub  $3,$3,$2",
			"and  $zero,$3,$1"
		);
	}

	public static void testTwo(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $1,$0,4",
			"sw $1,4($1)",
			"lw $2,4($1)",
			"addi  $3,$2,3"
		);
	}

	public static void testTree(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $1,$0,10",
			"addi $2,$0,13",
			"sll  $4,$1,10",
			"srl  $3,$2,2"
		);
	}

	public static void testFour(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $1,$0,10",
			"addi $2,$0,2",
			"j 5",
			NOP,
			"add  $5,$1,$2 ",
			"add  $6,$1,$2"
		);
	}

	public static void testFive(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $3,$zero,4",
			"beq  $3,$3,3",
			NOP,
			NOP,
			"addi $2,$zero,4",
			"addi $4,$zero,4",
			"beq  $4,$2,-2"
		);
	}

	public static void testSix(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(stepByStep, debugLevel, MemoryInformation,
			"addi $1,$zero,13",
			"addi $2,$zero,10",
			"addi $1,$1,-1",
			"slt  $3,$1,$2",
			"beq  $3,$zero,-3",
			NOP,
			NOP,
			"j 0"
		);
	}

	public static void readFile(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = Main.scanner.nextLine();
		testCase(stepByStep, debugLevel, MemoryInformation, FilesKt.readLines(new File(fileName), Charsets.UTF_8));
	}


	public static void testCase(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation, String... instructions) {
		testCase(stepByStep, debugLevel, MemoryInformation, Arrays.asList(instructions));
	}

	public static void testCase(boolean stepByStep, int debugLevel, List<Integer> MemoryInformation, List<String> instructions) {
		System.out.println("Running following instructions: ");
		for (var i = 0; i < instructions.size(); i++) {
			System.out.println(String.format("%02d) %s", i + 1, instructions.get(i)));
		}

		instructions.add(NOP);
		instructions.add(NOP);
		instructions.add(NOP);
		instructions.add(NOP);
		instructions.add(HALT);

		System.out.println("Warm up simulator");

		final var simulator = new Simulator();
		simulator.loadInstructions(instructions);
		simulator.loadDataMemory(MemoryInformation);

		System.out.println("Please Enter to see happen that care in cpu");
		Main.scanner.nextLine();

		simulator.run(debugLevel, stepByStep);
	}

}
