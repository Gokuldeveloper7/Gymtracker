package com.gokul.gymtracker;

public class Main1 {
	public class Main {
	    public static void main(String[] args) {
	        while(true) {
	            System.out.println("\n=== Gym Tracker Menu ===");
	            System.out.println("1. Add User");
	            System.out.println("2. Add Exercise");
	            System.out.println("3. Add Workout");
	            System.out.println("4. Log Workout");
	            System.out.println("5. Show Users");
	            System.out.println("6. Exit");
	            System.out.print("Choose option: ");
	            int choice = GymBackend.sc.nextInt();
	            GymBackend.sc.nextLine(); // consume newline

	            if(choice == 1) GymBackend.addUser();
	            else if(choice == 2) GymBackend.addExercise();
	            else if(choice == 3) GymBackend.addWorkout();
	            else if(choice == 4) GymBackend.logWorkout();
	            else if(choice == 5) GymBackend.showUsers();
	            else if(choice == 6) break;
	            else System.out.println("Invalid option!");
	        }
	        System.out.println("Exiting Gym App...");
	    }
}}
