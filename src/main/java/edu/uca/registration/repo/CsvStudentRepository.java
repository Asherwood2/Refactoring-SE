package edu.uca.registration.repo;

import edu.uca.registration.model.Student;
import edu.uca.registration.util.AppLogger;

import java.io.*;
import java.util.*;

public class CsvStudentRepository implements StudentRepository{
    private String filePath;
    private Map<String, Student> students;
    private AppLogger logger;
    
    public CsvStudentRepository(String filePath, AppLogger logger){
        this.filePath = filePath;
        this.students = new HashMap<>();
        this.logger = logger;
    }
    
    @Override
    public void load(){
        File f = new File(filePath);
        if (!f.exists()){
            logger.info("Students file not found: " + filePath);
            return;
        }
    }
}

