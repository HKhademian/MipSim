package mipsim;

import kotlin.io.FilesKt;
import kotlin.text.Charsets;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static mipsim.sim.ParserKt.HALT;
import static mipsim.sim.ParserKt.NOP;
import static sim.tool.TestKt.testOn;

public class Test {
	public static void Test(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		boolean contuie = true;
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		while (contuie) {
			System.out.println("\n\nDo you like to test it's power.?\n1.I like to see rType function with" +
				" hazard\n2.Do you like to see how can I sw and lw in memory and I detect Lw hazard for you\n" +
				"3.Time to shift your amount\n" +
				"4.Do you like to see how can I jump to any Address you want\n5.If condition be good I can come back I have Branch backward and forward\n" +
				"6.Time to check set less than\n" +

				"\n8.Back to before stage.");
			int choice = myObj.nextInt();
			switch (choice) {
				case 0:
					testZero( have_memeory_information,  stepByStep, debugLevel,  MemoryInformation);
					break;
				case 1:
					testOne( have_memeory_information,  stepByStep, debugLevel,  MemoryInformation);
					break;
				case 2:
					testTwo( have_memeory_information, stepByStep,  debugLevel,  MemoryInformation);
					break;
				case 3:
					testTree(have_memeory_information, stepByStep, debugLevel, MemoryInformation);
					break;
				case 4:
					testFour(have_memeory_information, stepByStep, debugLevel,MemoryInformation);
					break;
				case 5:
					testFive(have_memeory_information,stepByStep, debugLevel,  MemoryInformation);
					break;
				case 6:
					testSix( have_memeory_information,  stepByStep, debugLevel, MemoryInformation);
					break;

				case 8:
					contuie = false;
					break;
				default:
					System.out.println("your input is wrong");
			}
		}
	}

	public static void testZero(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information
			,stepByStep
			,debugLevel,
			MemoryInformation,
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
	}

	/**
	 * in this test we check hazard detection And this fact we can't write in zero register
	 */
	public static void testOne(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information,
			stepByStep,
			debugLevel,
			MemoryInformation,
			"addi $1,$zero,10",
			"addi $2,$zero,13",
			"add  $3,$2,$1 ",
			"sub  $3,$3,$2",
			"and  $zero,$3,$1",
			NOP,
			HALT
		);
	}

	public static void testTwo(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information,
			stepByStep,
			debugLevel,
			MemoryInformation,
			"addi $1,$0,4",
			"sw $1,4($1)",
			"lw $2,4($1)",
			"addi  $3,$2,3",
			NOP,
			NOP,
			NOP,
			HALT
		);
	}

	public static void testTree(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information,
			stepByStep,
			debugLevel,
			MemoryInformation,
			"addi $1,$0,10",
			"addi $2,$0,13",
			"sll  $4,$1,10",
			"srl  $3,$2,2",
			NOP,
			NOP,
			NOP,
			HALT
		);
	}

	public static void testFour(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information,
			stepByStep,
			debugLevel,
			MemoryInformation,
			"addi $1,$0,10",
			"addi $2,$0,2",
			"j 5",
			NOP,
			"add  $5,$1,$2 ",
			"add  $6,$1,$2",
			NOP,
			NOP,
			NOP,
			HALT
		);
	}

	public static void testFive(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(
			have_memeory_information,
			stepByStep,
			debugLevel,
			MemoryInformation,
			"addi $3,$zero,4",
			"beq  $3,$3,3",
			NOP,
			NOP,
			"addi $2,$zero,4",
			"addi $4,$zero,4",
			"beq  $4,$2,-2",
			NOP,
			NOP,
			NOP,
			HALT

		);
	}

	public static void testSix(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		testCase(have_memeory_information
			,stepByStep
			,debugLevel
			,MemoryInformation,
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
			NOP,
			NOP,
			NOP,
			HALT

		);
	}

	public static void readFile(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation) {
		System.out.println("Please enter mips inst file to run:");
		final var fileName = new Scanner(System.in).nextLine();
		final var file = new File(fileName);
		testCase(have_memeory_information, stepByStep,debugLevel,  MemoryInformation,FilesKt.readLines(file, Charsets.UTF_8));
	}


	public static void testCase(Boolean have_memeory_information, Boolean stepByStep, int debugLevel, List<Integer> MemoryInformation, String... instructions) {
		testCase( have_memeory_information,  stepByStep,  debugLevel,  MemoryInformation,Arrays.asList(instructions));
	}

	public static void testCase(Boolean have_memeory_information,Boolean stepByStep,int debugLevel,List<Integer>MemoryInformation,List<String> instructions) {
		for (String instruction : instructions) {
			System.out.println(instruction);
		}
		System.out.println("Please Enter to see happen that care in cpu");
		new Scanner(System.in).nextLine();

		final var simulator = new Simulator();
		simulator.loadInstructions(instructions);
		if (have_memeory_information)
			simulator.loadDataMemory(MemoryInformation); //todo: for Main

		simulator.run(debugLevel, stepByStep);
	}

}
