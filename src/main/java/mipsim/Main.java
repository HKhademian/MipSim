package mipsim;

import mipsim.sim.ParserKt;
import sim.tool.DebugKt;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static sim.tool.TestKt.testOn;

public class Main {


	public static void main(String[] args){
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
			String choice = myObj.nextLine();
			switch (Integer.valueOf(choice)) {

				case 1:
					TestMain.testOne();
					break;
				case 2:
					TestMain.testTwo();
					break;
				case 3:
					TestMain.testTree();
					break;
				case 4:
					TestMain.testFour();
					break;
				case 5:
					TestMain.testFive();
					break;
				case 6:
					TestMain.testSix();
					break;
				case 7:
					TestMain.readFile();
					break;
				case 8:
					contuie = false;
					break;
			}
		}
	}
}

