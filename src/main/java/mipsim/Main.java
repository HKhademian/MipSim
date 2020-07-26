package mipsim;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
//todo: plz make me
	public static void main(String args[]) {
		System.out.println("\n\nHi Lovely\nWelcome to my cpu simulator.");
		System.out.println("What do you like to :)");
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object

		while (true){
			System.out.println("1.Do you like to test our cpu ?");
			System.out.println("2.Run your Own code ?");
			System.out.println("3.mExit from Program");
			var choice = myObj.nextInt();
			if (choice == 1){
				var DebugeLevel =	find_DebugLevel();
				var is_stepBystep = find_stepShow();
				var is_memory_information = find_heWantToSaveInMemory();
				ArrayList<Integer> numbers = new ArrayList<>();
				if (is_memory_information){
						numbers = memoryData();
				}
				Test.Test(is_memory_information,is_stepBystep,DebugeLevel,numbers);

			}
			else if (choice == 2){
				var DebugeLevel =	find_DebugLevel();
				var is_stepBystep = find_stepShow();
				var is_memory_information = find_heWantToSaveInMemory();
				ArrayList<Integer> numbers = new ArrayList<>();
				if (is_memory_information){
					numbers = memoryData();

				}
				Test.readFile(is_memory_information,is_stepBystep,DebugeLevel,numbers);
			}
			else if(choice == 3){
				System.out.println("Good Bye");
				break;
			}
			else
				System.out.println("Wrong Input");

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
		public static int find_DebugLevel(){
			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			System.out.println("please choice Debug level :");
			System.out.println("1.Easy");
			System.out.println("2.medium");
			System.out.println("3.advance");
			return myObj.nextInt();
		}
		public static boolean find_stepShow(){
			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			System.out.println("Do you like to see step by step of code that you Run in Cpu ?");
			System.out.println("yes(y),No(any String you love)");
			if (myObj.nextLine().equals("y"))return true;
			return false;
		}
		public static boolean find_heWantToSaveInMemory(){
			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			System.out.println("Do you like to save some information in memory?");
			System.out.println("yes(y),No(any String you love)");
			if(myObj.nextLine().equals("y"))return true;
			return false;
		}
		public static ArrayList<Integer> memoryData(){
			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			ArrayList<Integer> array = new ArrayList<>();
			array.add(0);
			while (true){
				System.out.println("Do you like to add number to mem?(enter)/n");
				var x = myObj.nextLine().equals('n');
				if(x) {
					array.add(-1);
					return array;
				}
				System.out.println("please Enter number:");
				array.add(myObj.nextInt());
			}

		}
}

//"7.Okey I'm good horse you can run any program that you like to run please Entere name of your file\n" +
