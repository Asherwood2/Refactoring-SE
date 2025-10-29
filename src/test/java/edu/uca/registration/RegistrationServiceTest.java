package edu.uca.registration;

import edu.uca.registration.exception.*;
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
    
    @Test(expected = ValidationException.class)
    public void testAddStudent_Duplicate() {
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addStudent("B001", "Bob", "bob@uca.edu");
    }
    
    @Test(expected = ValidationException.class)
    public void testAddStudent_InvalidBannerId() {
        service.addStudent("invalid", "Alice", "alice@uca.edu");
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
    public void testListCourses() {
        service.addCourse("CSCI4490", "SE", 30);
        service.addCourse("MATH1496", "Calculus", 50);
        assertEquals(2, courseRepo.getAll().size());
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
    
    // searching students
    @Test
    public void testSearchStudents() {
        service.addStudent("B001", "Alice Smith", "alice@uca.edu");
        service.addStudent("B002", "Bob Jones", "bob@uca.edu");
        
        assertEquals(1, service.searchStudents("Alice").size());
        assertEquals(2, service.searchStudents("").size());
    }
    
    //  searching courses
    @Test
    public void testSearchCourses() {
        service.addCourse("CSCI4490", "Software Engineering", 30);
        service.addCourse("MATH1496", "Calculus", 50);
        
        assertEquals(1, service.searchCourses("Software").size());
        assertEquals(2, service.searchCourses("").size());
    }
}