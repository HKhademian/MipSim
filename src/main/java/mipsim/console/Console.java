package mipsim.console;

import kotlin.Pair;

import java.util.Scanner;

public final class Console {
	public static final Scanner scanner = new Scanner(System.in);

	public static final Pair<String, String>[] bundles = new Pair[]{
		new Pair<>("Sum(numbers)", "mipsim/bundle/sum.asm"),
		new Pair<>("Maximum(numbers)", "mipsim/bundle/max.asm"),
		new Pair<>("Factorial(n)", "mipsim/bundle/fac.asm"),
		new Pair<>("Fibonacci(n)", "mipsim/bundle/fibo.asm"),
		new Pair<>("BubbleSort(numbers)", "mipsim/bundle/sort.asm"),
	};

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
}
