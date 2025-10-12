package edu.uca.registration.service;

import edu.uca.registration.exception.EnrollmentException;
import edu.uca.registration.exception.ValidationException;
import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.util.Validator;

import java.util.ArrayList;
import java.util.List;

public class RegistrationService {
    private StudentRepository studentRepo;
    private CourseRepository courseRepo;

    public RegistrationService(StudentRepository studentRepo, CourseRepository courseRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
    }

    public void addStudent(String id, String name, String email) {
        Validator.validateBannerId(id);
        Validator.validateNonEmpty(name, "Name");
        Validator.validateEmail(email);

        if (studentRepo.exists(id)) {
            throw new ValidationException("Student with ID " + id + " already exists");
        }
        studentRepo.add(new Student(id, name, email));
    }

    public void addCourse(String code, String title, int capacity) {
        Validator.validateNonEmpty(code, "Course code");
        Validator.validateNonEmpty(title, "Title");
        Validator.validateCapacity(capacity);

        if (courseRepo.get(code) != null) {
            throw new ValidationException("Course " + code + " already exists");
        }
        courseRepo.add(new Course(code, title, capacity));
    }

    public String enroll(String studentId, String courseCode) {
        if (!studentRepo.exists(studentId)) {
            throw new EnrollmentException("Student not found: " + studentId);
        }
        Course c = courseRepo.get(courseCode);
        if (c == null) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }
        if (c.getRoster().contains(studentId)) {
            throw new EnrollmentException("Already enrolled");
        }
        if (c.getWaitlist().contains(studentId)) {
            throw new EnrollmentException("Already waitlisted");
        }

        if (c.getRoster().size() >= c.getCapacity()) {
            c.getWaitlist().add(studentId);
            return "Course full. Added to WAITLIST.";
        } else {
            c.getRoster().add(studentId);
            return "Enrolled.";
        }
    }

    public String drop(String studentId, String courseCode) {
        Course c = courseRepo.get(courseCode);
        if (c == null) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }

        if (c.getRoster().remove(studentId)) {
            if (!c.getWaitlist().isEmpty()) {
                String promote = c.getWaitlist().remove(0);
                c.getRoster().add(promote);
                return "Dropped. Promoted " + promote + " from waitlist.";
            }
            return "Dropped.";
        } else if (c.getWaitlist().remove(studentId)) {
            return "Removed from waitlist.";
        } else {
            throw new EnrollmentException("Not enrolled or waitlisted");
        }
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(studentRepo.getAll().values());
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : studentRepo.getAll().values()) {
            if (s.getName().toLowerCase().contains(lowerKeyword) ||
                    s.getId().toLowerCase().contains(lowerKeyword) ||
                    s.getEmail().toLowerCase().contains(lowerKeyword)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(courseRepo.getAll().values());
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Course> results = new ArrayList<>();
        for (Course c : courseRepo.getAll().values()) {
            if (c.getCode().toLowerCase().contains(lowerKeyword) ||
                    c.getTitle().toLowerCase().contains(lowerKeyword)) {
                results.add(c);
            }
        }
        return results;
    }

    public StudentRepository getStudentRepo() {
        return studentRepo;
    }

    public CourseRepository getCourseRepo() {
        return courseRepo;
    }
}