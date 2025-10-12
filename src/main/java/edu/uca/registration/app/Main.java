package edu.uca.registration.app;

import edu.uca.registration.model.Student;
import edu.uca.registration.model.Course;
import edu.uca.registration.repo.*;
import edu.uca.registration.util.AppLogger;

import java.util.*;

// CLI application for course registration with file persistence
public class Main {
    private static StudentRepository studentRepo;
    private static CourseRepository courseRepo;

    public static void main(String[] args) {
        // Initialize logger and repositories
        AppLogger logger = new AppLogger(false);

        // External file paths
        String studentsFile = System.getProperty("students.file", "students.csv");
        String coursesFile = System.getProperty("courses.file", "courses.csv");

        studentRepo = new CsvStudentRepository(studentsFile, logger);
        courseRepo = new CsvCourseRepository(coursesFile, logger);

        // Load existing data
        studentRepo.load();
        courseRepo.load();

        System.out.println("=== UCA Course Registration ===\n");
        menuLoop();

        // Save all data before exit
        studentRepo.saveAll();
        courseRepo.saveAll();

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

    // Student info prompted and added to repo
    private static void addStudent(Scanner sc) {
        System.out.print("Banner ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        Student student = new Student(id, name, email);
        studentRepo.add(student);
        System.out.println("Student added!");
    }

    // Course info prompted and added to repo
    private static void addCourse(Scanner sc) {
        System.out.print("Course Code: ");
        String code = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Capacity: ");
        int capacity = Integer.parseInt(sc.nextLine().trim());

        Course course = new Course(code, title, capacity);
        courseRepo.add(course);
        System.out.println("Course added!");
    }

    // Display all students from repo
    private static void listStudents() {
        System.out.println("Students:");
        for (Student s : studentRepo.getAll().values()) {
            System.out.println(" - " + s);
        }
    }

    // Display all courses from repo
    private static void listCourses() {
        System.out.println("Courses:");
        for (Course c : courseRepo.getAll().values()) {
            System.out.println(" - " + c.getCode() + " " + c.getTitle() +
                    " (capacity: " + c.getCapacity() + ")");
        }
    }
}