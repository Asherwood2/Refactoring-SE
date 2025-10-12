package edu.uca.registration.app;

import edu.uca.registration.model.Student;
import edu.uca.registration.model.Course;

import java.util.*;


public class Main {
    // Temporary in-memory storage
    private static Map<String, Student> students = new HashMap<>();
    private static Map<String, Course> courses = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("=== UCA Course Registration ===\n");
        menuLoop();
        System.out.println("Goodbye!");
    }

    // Menu and user input
    private static void menuLoop() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1) Add student");
            System.out.println("2) Add course");
            System.out.println("3) List students");
            System.out.println("4) List courses");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addStudent(sc); break;
                case "2": addCourse(sc); break;
                case "3": listStudents(); break;
                case "4": listCourses(); break;
                case "0": return;
                default: System.out.println("Invalid choice"); break;
            }
        }
    }

    // Student info prompted and added to map
    private static void addStudent(Scanner sc) {
        System.out.print("Banner ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        Student student = new Student(id, name, email);
        students.put(id, student);
        System.out.println("Student added!");
    }

    // Course info prompted and added to map
    private static void addCourse(Scanner sc) {
        System.out.print("Course Code: ");
        String code = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(sc.nextLine().trim());

        Course course = new Course(code, title, capacity);
        courses.put(code, course);
        System.out.println("Course added!");
    }

    // Display all students
    private static void listStudents() {
        System.out.println("Students:");
        for (Student s : students.values()) {
            System.out.println(" - " + s);
        }
    }

    // Display all courses
    private static void listCourses() {
        System.out.println("Courses:");
        for (Course c : courses.values()) {
            System.out.println(" - " + c.getCode() + " " + c.getTitle() +
                    " (capacity: " + c.getCapacity() + ")");
        }
    }
}