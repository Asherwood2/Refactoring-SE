package edu.uca.registration.repo;

import edu.uca.registration.model.Course;

import java.util.Map;

public interface CourseRepository {
    void load();
    void saveAll();
    Map<String, Course> getAll();
    void add(Course course);
    Course get(String code);
}