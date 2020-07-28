package mipsim.console;

import kotlin.Pair;
import kotlin.io.FilesKt;
import kotlin.text.Charsets;
import mipsim.sim.Simulator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mipsim.sim.ParserKt.HALT;
import static mipsim.sim.ParserKt.NOP;

public class Test {
	public static void test(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		boolean repeat = true;
		while (repeat) {
			System.out.println("\n\nDo you like to test it's power.?\n1.I like to see rType function with" +
				" hazard\n2.Do you like to see how can I sw and lw in memory and I detect Lw hazard for you\n" +
				"3.Time to shift your amount\n" +
				"4.Do you like to see how can I jump to any Address you want\n5.If condition be good I can come back I have Branch backward and forward\n" +
				"6.Time to check set less than\n" +

				"\n0.Back to before stage.");

			switch (Main.scanner.nextInt()) {
				case 0 -> repeat = false;
				case 1 -> testOne(stepByStep, debugLevel, memoryData);
				case 2 -> testTwo(stepByStep, debugLevel, memoryData);
				case 3 -> testTree(stepByStep, debugLevel, memoryData);
				case 4 -> testFour(stepByStep, debugLevel, memoryData);
				case 5 -> testFive(stepByStep, debugLevel, memoryData);
				case 6 -> testSix(stepByStep, debugLevel, memoryData);
				case 7 -> testZero(stepByStep, debugLevel, memoryData);
				default -> System.out.println("your input is wrong!");
			}
		}
	}

	public static void testZero(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
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
	public static void testOne(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $1,$zero,10",
			"addi $2,$zero,13",
			"add  $3,$2,$1 ",
			"sub  $3,$3,$2",
			"and  $zero,$3,$1"
		);
	}

	public static void testTwo(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $1,$0,4",
			"sw $1,4($1)",
			"lw $2,4($1)",
			"addi  $3,$2,3"
		);
	}

	public static void testTree(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $1,$0,10",
			"addi $2,$0,13",
			"sll  $4,$1,10",
			"srl  $3,$2,2"
		);
	}

	public static void testFour(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $1,$0,10",
			"addi $2,$0,2",
			"j 5",
			NOP,
			"add  $5,$1,$2 ",
			"add  $6,$1,$2"
		);
	}

	public static void testFive(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $3,$zero,4",
			"beq  $3,$3,3",
			NOP,
			NOP,
			"addi $2,$zero,4",
			"addi $4,$zero,4",
			"beq  $4,$2,-2"
		);
	}

	public static void testSix(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		testCase(stepByStep, debugLevel, memoryData,
			"addi $1,$zero,13",
			"addi $2,$zero,10",
			"addi $1,$1,-1",
			"slt  $3,$1,$2",
			"beq  $3,$zero,-3",
			NOP,
			NOP
		);
	}


	public static void testBundle(Pair<String, String> bundle, boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		final var loader = ClassLoader.getSystemClassLoader();
		final var path = bundle.getSecond();
		final var file = new File(loader.getResource(path).getFile());
		Test.testCase(stepByStep, debugLevel, memoryData, file);
	}


	public static void testFile(boolean stepByStep, int debugLevel, List<Integer> memoryData) {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = Main.scanner.nextLine();
		testCase(stepByStep, debugLevel, memoryData, FilesKt.readLines(new File(fileName), Charsets.UTF_8));
	}

	public static void testCase(boolean stepByStep, int debugLevel, List<Integer> memoryData, File instructionFile) {
		final var instructions = FilesKt.readLines(instructionFile, Charsets.UTF_8);
		testCase(stepByStep, debugLevel, memoryData, instructions);
	}


	public static void testCase(boolean stepByStep, int debugLevel, List<Integer> memoryData, String... instructions) {
		testCase(stepByStep, debugLevel, memoryData, Arrays.asList(instructions));
	}

	public static void testCase(boolean stepByStep, int debugLevel, List<Integer> memoryData, List<String> instructions) {
		System.out.println("Running following instructions: ");
		for (var i = 0; i < instructions.size(); i++) {
			System.out.println(String.format("%02d) %s", i + 1, instructions.get(i)));
		}

		final var insts = new ArrayList<>(instructions);
		for (var i = 0; i < 5; i++)
			insts.add(NOP);
		for (var i = 0; i < 10; i++)
			insts.add(HALT);

		System.out.println("Warm up simulator");

		final var simulator = new Simulator();
		simulator.loadInstructions(insts);
		simulator.loadDataMemory(memoryData);

		System.out.println("Please Enter to run processor: ");
		Main.scanner.nextLine();

		simulator.run(debugLevel, stepByStep);
	}

}
