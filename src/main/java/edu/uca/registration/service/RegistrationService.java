package edu.uca.registration.service;

import edu.uca.registration.exception.EnrollmentException;
import edu.uca.registration.exception.ValidationException;
import edu.uca.registration.model.Course;
import edu.uca.registration.model.Student;
import edu.uca.registration.repository.DataRepository;
import edu.uca.registration.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistrationService {
    private DataRepository repository;

    public RegistrationService(DataRepository repository) {
        this.repository = repository;
    }

    public void addStudent(String id, String name, String email) {
        Validator.validateBannerId(id);
        Validator.validateNonEmpty(name, "Name");
        Validator.validateEmail(email);

        if (repository.getStudents().containsKey(id)) {
            throw new ValidationException("Student with ID " + id + " already exists");
        }
        repository.getStudents().put(id, new Student(id, name, email));
    }

    public void addCourse(String code, String title, int capacity) {
        Validator.validateNonEmpty(code, "Course code");
        Validator.validateNonEmpty(title, "Title");
        Validator.validateCapacity(capacity);

        if (repository.getCourses().containsKey(code)) {
            throw new ValidationException("Course " + code + " already exists");
        }
        repository.getCourses().put(code, new Course(code, title, capacity));
    }

    public String enroll(String studentId, String courseCode) {
        Map<String, Student> students = repository.getStudents();
        Map<String, Course> courses = repository.getCourses();

        if (!students.containsKey(studentId)) {
            throw new EnrollmentException("Student not found: " + studentId);
        }
        Course c = courses.get(courseCode);
        if (c == null) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }
        if (c.roster.contains(studentId)) {
            throw new EnrollmentException("Already enrolled");
        }
        if (c.waitlist.contains(studentId)) {
            throw new EnrollmentException("Already waitlisted");
        }

        if (c.roster.size() >= c.capacity) {
            c.waitlist.add(studentId);
            return "Course full. Added to WAITLIST.";
        } else {
            c.roster.add(studentId);
            return "Enrolled.";
        }
    }

    public String drop(String studentId, String courseCode) {
        Course c = repository.getCourses().get(courseCode);
        if (c == null) {
            throw new EnrollmentException("Course not found: " + courseCode);
        }

        if (c.roster.remove(studentId)) {
            if (!c.waitlist.isEmpty()) {
                String promote = c.waitlist.remove(0);
                c.roster.add(promote);
                return "Dropped. Promoted " + promote + " from waitlist.";
            }
            return "Dropped.";
        } else if (c.waitlist.remove(studentId)) {
            return "Removed from waitlist.";
        } else {
            throw new EnrollmentException("Not enrolled or waitlisted");
        }
    }

    public DataRepository getRepository() {
        return repository;
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(repository.getStudents().values());
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Student> results = new ArrayList<>();
        for (Student s : repository.getStudents().values()) {
            if (s.name.toLowerCase().contains(lowerKeyword) ||
                    s.id.toLowerCase().contains(lowerKeyword) ||
                    s.email.toLowerCase().contains(lowerKeyword)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(repository.getCourses().values());
        }
        String lowerKeyword = keyword.toLowerCase();
        List<Course> results = new ArrayList<>();
        for (Course c : repository.getCourses().values()) {
            if (c.code.toLowerCase().contains(lowerKeyword) ||
                    c.title.toLowerCase().contains(lowerKeyword)) {
                results.add(c);
            }
        }
        return results;
    }
}