package com.gokul.gymtracker;

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

public class GymBackend {

    // Database connection
    static final String URL = "jdbc:mysql://localhost:3306/gym_tracker_db1?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "Cr7#1234";

    static Scanner sc = new Scanner(System.in);

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
            int choice = sc.nextInt();
            sc.nextLine();

            if(choice == 1) addUser();
            else if(choice == 2) addExercise();
            else if(choice == 3) addWorkout();
            else if(choice == 4) logWorkout();
            else if(choice == 5) showUsers();
            else if(choice == 6) break;
            else System.out.println("Invalid option!");
        }

        System.out.println("Exiting Gym App...");
    }

    // ------------------- CRUD Methods -------------------

    static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            conn.setAutoCommit(true); // ensure inserts are saved immediately
            return conn;
        } catch(SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
            return null;
        }
    }

    static void addUser() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Weight: ");
        double weight = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Goal: ");
        String goal = sc.nextLine();

        String sql = "INSERT INTO users (name, weight, goal) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, weight);
            stmt.setString(3, goal);
            stmt.executeUpdate();
            System.out.println("User added successfully.");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    static void addExercise() {
        System.out.print("Enter Exercise Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Muscle Group: ");
        String muscle = sc.nextLine();
        System.out.print("Enter Equipment: ");
        String equip = sc.nextLine();
        System.out.print("Enter Instructions: ");
        String instr = sc.nextLine();

        String sql = "INSERT INTO exercises (name, muscle_group, equipment, instructions) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, muscle);
            stmt.setString(3, equip);
            stmt.setString(4, instr);
            stmt.executeUpdate();
            System.out.println("Exercise added successfully.");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    static void addWorkout() {
        System.out.print("Enter User ID: ");
        int userId = sc.nextInt(); sc.nextLine();
        System.out.print("Enter Workout Name: ");
        String name = sc.nextLine();

        String sql = "INSERT INTO workouts (user_id, name, date) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
            System.out.println("Workout added successfully.");
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Log workout with progress check
    static void logWorkout() {
        System.out.print("Enter Workout ID: ");
        int wId = sc.nextInt();
        System.out.print("Enter Exercise ID: ");
        int eId = sc.nextInt();
        System.out.print("Enter Sets: ");
        int sets = sc.nextInt();
        System.out.print("Enter Reps: ");
        int reps = sc.nextInt();
        System.out.print("Enter Weight: ");
        double weight = sc.nextDouble();
        sc.nextLine();

        try (Connection conn = connect()) {
            // Check previous max weight and reps
            String checkSql = "SELECT MAX(reps) AS max_reps, MAX(weight) AS max_weight " +
                              "FROM workout_logs WHERE workout_id=? AND exercise_id=?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, wId);
                checkStmt.setInt(2, eId);
                ResultSet rs = checkStmt.executeQuery();

                int maxReps = 0;
                double maxWeight = 0;
                if(rs.next()) {
                    maxReps = rs.getInt("max_reps");
                    maxWeight = rs.getDouble("max_weight");
                }

                // Check progress rules
                if(weight < maxWeight) {
                    System.out.println("Cannot log workout: Weight cannot decrease!");
                    return;
                }
                if(weight == maxWeight && reps < maxReps) {
                    System.out.println("Cannot log workout: Reps cannot be less than previous for same weight!");
                    return;
                }
            }

            // Insert new log if check passes
            String sql = "INSERT INTO workout_logs (workout_id, exercise_id, sets, reps, weight) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, wId);
                stmt.setInt(2, eId);
                stmt.setInt(3, sets);
                stmt.setInt(4, reps);
                stmt.setDouble(5, weight);
                stmt.executeUpdate();
                System.out.println("Workout logged successfully.");
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    static void showUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Users ---");
            while(rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Name: " + rs.getString("name") +
                                   " | Weight: " + rs.getDouble("weight") + " | Goal: " + rs.getString("goal"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
