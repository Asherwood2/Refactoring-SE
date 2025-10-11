package edu.uca.registration.repo;

import edu.uca.registration.model.Course;

import java.util.Map;

public interface EnrollmentRepository {
    void load(Map<String, Course> courses);
    void saveAll(Map<String, Course> courses);
}