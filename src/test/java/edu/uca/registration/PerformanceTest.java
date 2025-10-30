package edu.uca.registration;

import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.repo.*;
import edu.uca.registration.util.AppLogger;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class PerformanceTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private RegistrationService service;
    private StudentRepository studentRepo;
    private CourseRepository courseRepo;
    
    @Before
    public void setUp() throws Exception {
        AppLogger logger = new AppLogger(false);
        
        File studentsFile = tempFolder.newFile("students.csv");
        File coursesFile = tempFolder.newFile("courses.csv");
        
        studentRepo = new CsvStudentRepository(studentsFile.getAbsolutePath(), logger);
        courseRepo = new CsvCourseRepository(coursesFile.getAbsolutePath(), logger);
        service = new RegistrationService(studentRepo, courseRepo);
    }
    
    @Test
    public void testPerformance_Add500Students() {
        long start = System.currentTimeMillis();
        
        for (int i = 1; i <= 500; i++) {
            String id = String.format("B%03d", i);
            service.addStudent(id, "Student" + i, "s" + i + "@uca.edu");
        }
        
        long duration = System.currentTimeMillis() - start;
        
        System.out.println("Time to add 500 students: " + duration + "ms");
        assertEquals(500, studentRepo.getAll().size());
        assertTrue("Performance too slow: " + duration + "ms", duration < 3000);
    }
    
    @Test
    public void testPerformance_Enroll100Students() {
        // Setup
        service.addCourse("CSCI4490", "SE", 100);
        for (int i = 1; i <= 100; i++) {
            String id = String.format("B%03d", i);
            service.addStudent(id, "Student" + i, "s" + i + "@uca.edu");
        }
        
        long start = System.currentTimeMillis();
        
        for (int i = 1; i <= 100; i++) {
            String id = String.format("B%03d", i);
            service.enroll(id, "CSCI4490");
        }
        
        long duration = System.currentTimeMillis() - start;
        
        System.out.println("Time to enroll 100 students: " + duration + "ms");
        assertEquals(100, courseRepo.get("CSCI4490").getRoster().size());
        assertTrue("Enrollment too slow: " + duration + "ms", duration < 2000);
    }
    
    @Test
    public void testPerformance_Search() {
        // Add test data
        for (int i = 1; i <= 500; i++) {
            String id = String.format("B%03d", i);
            service.addStudent(id, "Student" + i, "s" + i + "@uca.edu");
        }
        
        long start = System.currentTimeMillis();
        
        // Perform 100 searches
        for (int i = 0; i < 100; i++) {
            service.searchStudents("Student");
        }
        
        long duration = System.currentTimeMillis() - start;
        
        System.out.println("Time for 100 searches: " + duration + "ms");
        assertTrue("Search too slow: " + duration + "ms", duration < 2000);
    }
    
    @Test
    public void testPerformance_WaitlistPromotion() {
        // Create small capacity course
        service.addCourse("CSCI4490", "SE", 10);
        
        // Add 50 students
        for (int i = 1; i <= 50; i++) {
            String id = String.format("B%03d", i);
            service.addStudent(id, "Student" + i, "s" + i + "@uca.edu");
        }
        
        // Enroll all (10 in roster, 40 in waitlist)
        for (int i = 1; i <= 50; i++) {
            String id = String.format("B%03d", i);
            service.enroll(id, "CSCI4490");
        }
        
        long start = System.currentTimeMillis();
        
        // Drop first 10, promote 10 from waitlist
        for (int i = 1; i <= 10; i++) {
            String id = String.format("B%03d", i);
            service.drop(id, "CSCI4490");
        }
        
        long duration = System.currentTimeMillis() - start;
        
        System.out.println("Time for 10 drops with promotions: " + duration + "ms");
        assertEquals(10, courseRepo.get("CSCI4490").getRoster().size());
        assertEquals(30, courseRepo.get("CSCI4490").getWaitlist().size());
        assertTrue("Drop/promote too slow: " + duration + "ms", duration < 1000);
    }
}