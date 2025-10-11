package edu.uca.registration.repo;

import edu.uca.registration.model.Student;

import java.util.Map;

public interface StudentRepository {
    void load();
    void saveAll();
    Map<String, Student> getAll();
    void add(Student student);
    boolean exists(String id);
}