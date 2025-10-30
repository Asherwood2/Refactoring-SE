package edu.uca.registration;

import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.model.*;
import edu.uca.registration.repo.*;
import edu.uca.registration.util.AppLogger;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
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

        // save
        CsvEnrollmentRepository repo1 = new CsvEnrollmentRepository(file.getAbsolutePath(), logger);
        repo1.saveAll(courses);

        // load
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


     //Error case:  trying to load a non-existent file

    @Test
    public void testStudentRepository_LoadNonExistentFile() {
        String nonExistentPath = tempFolder.getRoot().getAbsolutePath() + "/nonexistent.csv";
        CsvStudentRepository repo = new CsvStudentRepository(nonExistentPath, logger);

        repo.load();
        assertEquals("Should have no students", 0, repo.getAll().size());
    }

    //Error case:  loading corrupted student CSV (missing fields and invalid data)

    @Test
    public void testStudentRepository_LoadCorruptedData() throws Exception {
        File file = tempFolder.newFile("students.csv");

        FileWriter writer = new FileWriter(file);
        writer.write("B001,Alice\n");
        writer.write("B002,Bob,bob@uca.edu\n");
        writer.write("InvalidLine\n");
        writer.close();

        CsvStudentRepository repo = new CsvStudentRepository(file.getAbsolutePath(), logger);
        repo.load();

        assertEquals("Should load only valid student", 1, repo.getAll().size());
        assertTrue("Should have B002", repo.exists("B002"));
        assertFalse("Should not have B001", repo.exists("B001"));
    }


     //Error case: loading empty CSV files

    @Test
    public void testRepository_LoadEmptyFiles() throws Exception {
        File studentsFile = tempFolder.newFile("students.csv");
        File coursesFile = tempFolder.newFile("courses.csv");
        File enrollmentsFile = tempFolder.newFile("enrollments.csv");

        CsvStudentRepository studentRepo = new CsvStudentRepository(studentsFile.getAbsolutePath(), logger);
        CsvCourseRepository courseRepo = new CsvCourseRepository(coursesFile.getAbsolutePath(), logger);
        CsvEnrollmentRepository enrollmentRepo = new CsvEnrollmentRepository(enrollmentsFile.getAbsolutePath(), logger);

        studentRepo.load();
        courseRepo.load();
        enrollmentRepo.load(courseRepo.getAll());

        assertEquals("Students should be empty", 0, studentRepo.getAll().size());
        assertEquals("Courses should be empty", 0, courseRepo.getAll().size());
    }

   //Error case: adding duplicate students (should overwrite existing entry)

    @Test
    public void testStudentRepository_AddDuplicate() throws Exception {
        File file = tempFolder.newFile("students.csv");
        CsvStudentRepository repo = new CsvStudentRepository(file.getAbsolutePath(), logger);

        repo.add(new Student("B001", "Alice", "alice@uca.edu"));
        repo.add(new Student("B001", "Bob", "bob@uca.edu"));

        assertEquals("Should have only 1 student", 1, repo.getAll().size());
        assertEquals("Should be Bob", "Bob", repo.getAll().get("B001").getName());
    }

     //Error case: loading enrollment file with non-existent course

    @Test
    public void testEnrollmentRepository_LoadWithMissingCourse() throws Exception {
        File file = tempFolder.newFile("enrollments.csv");

        FileWriter writer = new FileWriter(file);
        writer.write("FAKE1234|B001|ENROLLED\n");
        writer.close();

        Map<String, Course> courses = new HashMap<>();
        CsvEnrollmentRepository repo = new CsvEnrollmentRepository(file.getAbsolutePath(), logger);

        repo.load(courses);
    }

    //Edge case: very long names (200 characters)
    @Test
    public void testRepository_VeryLongName() throws Exception {
        File file = tempFolder.newFile("students.csv");
        String longName = "A".repeat(200);

        CsvStudentRepository repo1 = new CsvStudentRepository(file.getAbsolutePath(), logger);
        repo1.add(new Student("B001", longName, "test@uca.edu"));
        repo1.saveAll();

        CsvStudentRepository repo2 = new CsvStudentRepository(file.getAbsolutePath(), logger);
        repo2.load();

        assertTrue("Student should exist", repo2.exists("B001"));
        assertEquals("Name should be preserved", longName, repo2.getAll().get("B001").getName());
    }
}