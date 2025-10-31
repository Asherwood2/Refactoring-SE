package edu.uca.registration;

import edu.uca.registration.exception.*;
import edu.uca.registration.model.Course;
import edu.uca.registration.repo.*;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.util.AppLogger;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import static org.junit.Assert.*;

public class RegistrationServiceTest {
    
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
    
    // adding student
    @Test
    public void testAddStudent() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        assertTrue(studentRepo.exists("B001"));
    }
    
    @Test
    public void test_Student_With_Invalid_Names() {
        int successCount = 0;
        int errorCount = 0;
    
        for (int i = 1; i <= 500; i++) {
            String id = String.format("B%03d", i);
            try {
                if (i % 5 == 0) {
                    service.addStudent(id, "", "s" + i + "@uca.edu"); // Invalid name
                } else {
                    service.addStudent(id, "Student" + i, "s" + i + "@uca.edu");
                    successCount++;
                }
            } catch (Exception e) {
                // Expected for invalid data
                errorCount++;
            }
        }
    
        assertEquals(successCount, studentRepo.getAll().size());
        assertEquals(100, errorCount); // 100 empty names
    }
    
    @Test(expected = ValidationException.class)
    public void testAddStudent_InvalidBannerId() {
        service.addStudent("invalid", "Alice", "alice@uca.edu");
    }
    
    @Test(expected = ValidationException.class)
    public void testAddStudent_Duplicate() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B001", "Bob", "bob@uca.edu");
    }
    
    // adding course
    @Test
    public void testAddCourse() {
        service.addCourse("CSCI4490", "Software Engineering", 30);
        assertNotNull(courseRepo.get("CSCI4490"));
    }
    
    @Test(expected = ValidationException.class)
    public void testAddCourse_Duplicate() {
        service.addCourse("CSCI4490", "SE", 30);
        service.addCourse("CSCI4490", "Different", 40);
    }
    
    // listing students and course
    @Test
    public void testListStudents() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
        assertEquals(2, studentRepo.getAll().size());
    }
    
    @Test
    public void testListStudents_Empty() {
        assertEquals(0, studentRepo.getAll().size());
    }
    
    @Test
    public void testListCourses() {
        service.addCourse("CSCI4490", "SE", 30);
        service.addCourse("MATH1496", "Calculus", 50);
        assertEquals(2, courseRepo.getAll().size());
    }
    
    @Test
    public void testListCourses_Empty() {
        assertEquals(0, courseRepo.getAll().size());
    }
    
    // enrolling
    @Test
    public void testEnroll() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addCourse("CSCI4490", "SE", 30);
        
        String result = service.enroll("B001", "CSCI4490");
        assertEquals("Enrolled.", result);
    }
    
    @Test
    public void testEnroll_Waitlist() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
        service.addCourse("CSCI4490", "SE", 1);
        
        service.enroll("B001", "CSCI4490");
        String result = service.enroll("B002", "CSCI4490");
        
        assertTrue(result.contains("WAITLIST"));
    }
    
    @Test(expected = EnrollmentException.class)
    public void testEnroll_AlreadyEnrolled() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addCourse("CSCI4490", "SE", 30);
        
        service.enroll("B001", "CSCI4490");
        service.enroll("B001", "CSCI4490"); // Should fail
    }
    
    // drop
    @Test
    public void testDrop() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addCourse("CSCI4490", "SE", 30);
        service.enroll("B001", "CSCI4490");
        
        String result = service.drop("B001", "CSCI4490");
        assertEquals("Dropped.", result);
    }
    
    @Test
    public void testDrop_AutoPromote() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
        service.addCourse("CSCI4490", "SE", 1);
        
        service.enroll("B001", "CSCI4490");
        service.enroll("B002", "CSCI4490"); // waitlisted
        
        String result = service.drop("B001", "CSCI4490");
        assertTrue(result.contains("Promoted B002"));
    }
    
    @Test(expected = EnrollmentException.class)
    public void testDrop_NotEnrolled() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addCourse("CSCI4490", "SE", 30);
        service.drop("B001", "CSCI4490");
    }
    
    // searching students
    @Test
    public void testSearchStudents() {
        service.addStudent("B001", "Alice Smith", "alice@uca.edu");
        service.addStudent("B002", "Bob Jones", "bob@uca.edu");
        
        assertEquals(1, service.searchStudents("Alice").size());
        assertEquals(2, service.searchStudents("").size());
    }
    
    @Test
    public void testSearchStudents_NoResults() {
        service.addStudent("B001", "Alice Smith", "alice@uca.edu");
        service.addStudent("B002", "Bob Jones", "bob@uca.edu");
    
        assertEquals(0, service.searchStudents("Nonexistent").size());
    }
    
    //  searching courses
    @Test
    public void testSearchCourses() {
        service.addCourse("CSCI4490", "Software Engineering", 30);
        service.addCourse("MATH1496", "Calculus", 50);
        
        assertEquals(1, service.searchCourses("Software").size());
        assertEquals(2, service.searchCourses("").size());
    }
    
    @Test
    public void testSearchCourses_NoResults() {
        service.addCourse("CSCI4490", "Software Engineering", 30);
        service.addCourse("MATH1496", "Calculus", 50);
    
        assertEquals(0, service.searchCourses("Nonexistent").size());
    }
    
    @Test
    public void CompleteSystemTest() {
        // Add students
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B002", "Bob", "bob@uca.edu");
    
        // Add course with capacity 1
        service.addCourse("CSCI4490", "SE", 1);
    
        // Enroll first student
        service.enroll("B001", "CSCI4490");
    
        // Enroll second student (goes to waitlist)
        String result = service.enroll("B002", "CSCI4490");
        assertTrue(result.contains("WAITLIST"));
    
        // Drop first student (promotes second from waitlist)
        result = service.drop("B001", "CSCI4490");
        assertTrue(result.contains("Promoted B002"));
    
        // List students and verify
        Course course = courseRepo.get("CSCI4490");
        assertEquals(1, course.getRoster().size());
        assertTrue(course.getRoster().contains("B002"));
        assertEquals(0, course.getWaitlist().size());
    }
}