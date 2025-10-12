package edu.uca.registration.repo;

import edu.uca.registration.model.Course;
import edu.uca.registration.util.AppLogger;

import java.io.*;
import java.util.*;

public class CsvEnrollmentRepository implements EnrollmentRepository{
    private String filePath;
    private AppLogger logger;
    
    public CsvEnrollmentRepository(String filePath, AppLogger logger){
        this.filePath = filePath;
        this.logger = logger;
    }
    
    @Override
    public void load(Map<String, Course> courses){
        File f = new File(filePath);
        if (!f.exists()){
            logger.info("Enrollments file not found: " + filePath);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null){
                lineNum++;
                try{
                    String[]p = line.split("\\|", -1);
                    if (p.length >= 3){
                        String code = p[0], sid = p[1], status = p[2];
                        Course c = courses.get(code);
                        if (c != null){
                            if ("ENROLLED".equalsIgnoreCase(status)){
                                if (!c.getRoster().contains(sid)) c.getRoster().add(sid);
                            } else if ("WAITLIST".equalsIgnoreCase(status)){
                                if (!c.getWaitlist().contains(sid)) c.getWaitlist().add(sid);
                            }
                        }
                    } else {
                        logger.warn("Skipping invalid enrollment line " + lineNum);
                    }
                } catch (Exception e){
                    logger.warn("Error parsing enrollment line " + lineNum + ": " + e.getMessage());
                }
            }
            logger.info("Loaded enrollments");
        } catch (IOException e){
            logger.error("Failed to load enrollments: " + e.getMessage());
        }
    }
    
}
