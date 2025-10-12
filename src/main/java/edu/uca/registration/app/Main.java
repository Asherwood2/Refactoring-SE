package edu.uca.registration.app;

import edu.uca.registration.model.Student;
import edu.uca.registration.model.Course;
import edu.uca.registration.repo.*;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.util.AppLogger;

import java.util.*;


public class Main {
    private static StudentRepository studentRepo;
    private static CourseRepository courseRepo;
    private static RegistrationService registrationService;


    public static void main(String[] args) {
        // Initialize logger and repositories
        AppLogger logger = new AppLogger(false);

        // External file paths
        String studentsFile = System.getProperty("students.file", "students.csv");
        String coursesFile = System.getProperty("courses.file", "courses.csv");
        String enrollmentsFile = System.getProperty("enrollments.file", "enrollments.csv");

        studentRepo = new CsvStudentRepository(studentsFile, logger);
        courseRepo = new CsvCourseRepository(coursesFile, logger);
        EnrollmentRepository enrollmentRepo = new CsvEnrollmentRepository(enrollmentsFile, logger);

        registrationService = new RegistrationService(studentRepo, courseRepo);

// Load data or demo
        boolean demo = args.length > 0 && "--demo".equalsIgnoreCase(args[0]);
        if (demo) {
            seedDemo();
        } else {
            studentRepo.load();
            courseRepo.load();
            enrollmentRepo.load(courseRepo.getAll());
        }

        System.out.println("=== UCA Course Registration ===\n");
        menuLoop();

        // Save all data before exit
        studentRepo.saveAll();
        courseRepo.saveAll();
        enrollmentRepo.saveAll(courseRepo.getAll());

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
            System.out.println("5) Enroll student in course");
            System.out.println("6) Drop student from course");
            System.out.println("7) Search students");
            System.out.println("8) Search courses");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": addStudent(sc); break;
                case "2": addCourse(sc); break;
                case "3": listStudents(); break;
                case "4": listCourses(); break;
                case "5": enrollStudent(sc); break;
                case "6": dropStudent(sc); break;
                case "7": searchStudents(sc); break;
                case "8": searchCourses(sc); break;
                case "0": return;
                default: System.out.println("Invalid choice"); break;
            }
        }
    }

    // Student info prompted and added to repo
    private static void addStudent(Scanner sc) {
        try {
            System.out.print("Banner ID: ");
            String id = sc.nextLine().trim();
            System.out.print("Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Email: ");
            String email = sc.nextLine().trim();

            registrationService.addStudent(id, name, email);
            System.out.println("Student added!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Course info prompted and added to repo
    private static void addCourse(Scanner sc) {
        try {
            System.out.print("Course Code: ");
            String code = sc.nextLine().trim();
            System.out.print("Title: ");
            String title = sc.nextLine().trim();
            System.out.print("Capacity: ");
            int capacity = Integer.parseInt(sc.nextLine().trim());

            registrationService.addCourse(code, title, capacity);
            System.out.println("Course added!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
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

    // Student id and course id prompted, adds entered student to entered course
    private static void enrollStudent(Scanner sc) {
        try {
            System.out.print("Student Banner ID: ");
            String studentId = sc.nextLine().trim();
            System.out.print("Course Code: ");
            String courseCode = sc.nextLine().trim();

            String result = registrationService.enroll(studentId, courseCode);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /* Course id is prompted, once entered shows a list of the roster/waitlist (unless class is empty); if valid
    student id is entered they are dropped. */
    private static void dropStudent(Scanner sc) {
        try {
            System.out.println("Course Code: ");
            String courseCode = sc.nextLine().trim();
            Course c = courseRepo.get(courseCode);
            if (c == null) {
                System.out.println("No such course"); return; }
            if (c.getRoster().size() <= 0) {
                System.out.println("Course Roster is Empty"); return; }
            else {
                System.out.println("Course Roster: ");
                for (int i = 0; i < c.getRoster().size(); i++)
                    System.out.println(c.getRoster().get(i));

                if (c.getWaitlist().size() <= 0)
                    System.out.println("Course Waitlist: Empty\n");
                else {
                    System.out.println("Course Waitlist: \n");
                    for (int i = 0; i < c.getWaitlist().size(); i++)
                        System.out.println(c.getWaitlist().get(i));
                }
            }

            System.out.print("Student Banner ID: ");
            String studentId = sc.nextLine().trim();

            String result = registrationService.drop(studentId, courseCode);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Search students by any value (id, name, email)
    private static void searchStudents(Scanner sc) {
        System.out.print("Search Student (leave empty for all students): ");
        String keyword = sc.nextLine().trim();

        List<Student> results = registrationService.searchStudents(keyword);
        System.out.println("Found " + results.size() + " student(s):");
        for (Student s : results) {
            System.out.println(" - " + s);
        }
    }

    // Search courses by id or title
    private static void searchCourses(Scanner sc) {
        System.out.print("Search Course (leave empty for all courses): ");
        String keyword = sc.nextLine().trim();

        List<Course> results = registrationService.searchCourses(keyword);
        System.out.println("Found " + results.size() + " course(s):");
        for (Course c : results) {
            System.out.println(" - " + c.getCode() + " " + c.getTitle() +
                    " (capacity: " + c.getCapacity() + ")");
        }
    }
    // Load demo data
    private static void seedDemo() {
        registrationService.addStudent("B001", "Alice", "alice@uca.edu");
        registrationService.addStudent("B002", "Brian", "brian@uca.edu");
        registrationService.addCourse("CSCI4490", "Software Engineering", 2);
        registrationService.addCourse("MATH1496", "Calculus I", 50);
        System.out.println("Demo data loaded!\n");
    }

}