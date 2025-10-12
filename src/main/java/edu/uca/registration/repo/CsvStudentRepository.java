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
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null){
                lineNum++;
                try{
                    String[] p = line.split(",", -1);
                    if (p.length >= 3){
                        students.put(p[0], new Student(p[0],p[1],p[2]));
                    } else{
                        logger.warn("Skipping invalid student line" + lineNum);
                    }
                } catch (Exception e){
                    logger.warn("Error parsing student line " + lineNum + ": " + e.getMessage());
                }
            }
            logger.info("Loaded" + students.size() + " students");
        } catch (IOException e){
            logger.error("Failed to load students: " + e.getMessage());
        }
    }
    
}

