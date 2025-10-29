package edu.uca.registration;

import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.model.*;
import edu.uca.registration.repo.*;
import edu.uca.registration.util.AppLogger;
import org.junit.*;
import org.junit.rules.TemporaryFolder;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RepositoryTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    private AppLogger logger;
    
    @Before
    public void setUp() {
        logger = new AppLogger(false);
    }
    
    @Test
    public void testStudentRepository_SaveAndLoad() throws Exception {
        File file = tempFolder.newFile("students.csv");
        
        // save
        CsvStudentRepository repo1 = new CsvStudentRepository(file.getAbsolutePath(), logger);
        repo1.add(new Student("B001", "Alice", "alice@uca.edu"));
        repo1.saveAll();
        
        // load
        CsvStudentRepository repo2 = new CsvStudentRepository(file.getAbsolutePath(), logger);
        repo2.load();
        
        assertTrue(repo2.exists("B001"));
        assertEquals("Alice", repo2.getAll().get("B001").getName());
    }
    
    @Test
    public void testCourseRepository_SaveAndLoad() throws Exception {
        File file = tempFolder.newFile("courses.csv");
        
        CsvCourseRepository repo1 = new CsvCourseRepository(file.getAbsolutePath(), logger);
        repo1.add(new Course("CSCI4490", "SE", 30));
        repo1.saveAll();
        
        CsvCourseRepository repo2 = new CsvCourseRepository(file.getAbsolutePath(), logger);
        repo2.load();
        
        assertNotNull(repo2.get("CSCI4490"));
        assertEquals(30, repo2.get("CSCI4490").getCapacity());
    }
    
    @Test
    public void testEnrollmentRepository_SaveAndLoad() throws Exception {
        File file = tempFolder.newFile("enrollments.csv");
        
        // create course with enrollment
        Course course = new Course("CSCI4490", "SE", 30);
        course.getRoster().add("B001");
        course.getWaitlist().add("B002");
        
        Map<String, Course> courses = new HashMap<>();
        courses.put("CSCI4490", course);
        
        // Save
        CsvEnrollmentRepository repo1 = new CsvEnrollmentRepository(file.getAbsolutePath(), logger);
        repo1.saveAll(courses);
        
        // Load
        Course freshCourse = new Course("CSCI4490", "SE", 30);
        Map<String, Course> newCourses = new HashMap<>();
        newCourses.put("CSCI4490", freshCourse);
        
        CsvEnrollmentRepository repo2 = new CsvEnrollmentRepository(file.getAbsolutePath(), logger);
        repo2.load(newCourses);
        
        assertTrue(freshCourse.getRoster().contains("B001"));
        assertTrue(freshCourse.getWaitlist().contains("B002"));
    }
    
    @Test
    public void testCompleteSystemPersistence() throws Exception {
        // setup
        File studentsFile = tempFolder.newFile("students.csv");
        File coursesFile = tempFolder.newFile("courses.csv");
        File enrollmentsFile = tempFolder.newFile("enrollments.csv");
        
        // create & save data
        StudentRepository studentRepo = new CsvStudentRepository(studentsFile.getAbsolutePath(), logger);
        CourseRepository courseRepo = new CsvCourseRepository(coursesFile.getAbsolutePath(), logger);
        EnrollmentRepository enrollmentRepo = new CsvEnrollmentRepository(enrollmentsFile.getAbsolutePath(), logger);
        RegistrationService service = new RegistrationService(studentRepo, courseRepo);
        
        service.addStudent("B001", "Alice", "alice@uca.edu");
        service.addCourse("CSCI4490", "SE", 30);
        service.enroll("B001", "CSCI4490");
        
        studentRepo.saveAll();
        courseRepo.saveAll();
        enrollmentRepo.saveAll(courseRepo.getAll());
        
        // load & verify
        StudentRepository newStudentRepo = new CsvStudentRepository(studentsFile.getAbsolutePath(), logger);
        CourseRepository newCourseRepo = new CsvCourseRepository(coursesFile.getAbsolutePath(), logger);
        EnrollmentRepository newEnrollmentRepo = new CsvEnrollmentRepository(enrollmentsFile.getAbsolutePath(), logger);
        
        newStudentRepo.load();
        newCourseRepo.load();
        newEnrollmentRepo.load(newCourseRepo.getAll());
        
        assertTrue(newStudentRepo.exists("B001"));
        assertNotNull(newCourseRepo.get("CSCI4490"));
        assertTrue(newCourseRepo.get("CSCI4490").getRoster().contains("B001"));
    }
}