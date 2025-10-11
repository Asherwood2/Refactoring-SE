package edu.uca.registration.repo;

import edu.uca.registration.model.Course;
import edu.uca.registration.util.AppLogger;

import java.io.*;
import java.util.*;

// Stores courses in CSV file
public class CsvCourseRepository implements CourseRepository {
    private String filePath;
    private Map<String, Course> courses;  // Code -> Course
    private AppLogger logger;

    public CsvCourseRepository(String filePath, AppLogger logger) {
        this.filePath = filePath;
        this.courses = new HashMap<>();
        this.logger = logger;
    }

    // Read courses from CSV file
    @Override
    public void load() {
        File f = new File(filePath);
        if (!f.exists()) {
            logger.info("Courses file not found: " + filePath);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                try {
                    String[] p = line.split(",", -1);
                    if (p.length >= 3) {
                        String code = p[0].trim();
                        String title = p[1].trim();
                        int cap = Integer.parseInt(p[2].trim());
                        courses.put(code, new Course(code, title, cap));
                    } else {
                        logger.warn("Skipping invalid course line " + lineNum);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Invalid capacity on line " + lineNum);
                } catch (Exception e) {
                    logger.warn("Error on line " + lineNum + ": " + e.getMessage());
                }
            }
            logger.info("Loaded " + courses.size() + " courses");
        } catch (IOException e) {
            logger.error("Failed to load courses: " + e.getMessage());
        }
    }

    // Write courses to CSV file
    @Override
    public void saveAll() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (Course c : courses.values()) {
                pw.println(c.getCode() + "," + c.getTitle() + "," + c.getCapacity());
            }
            logger.info("Saved " + courses.size() + " courses");
        } catch (IOException e) {
            logger.error("Failed to save courses: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Course> getAll() {
        return courses;
    }

    @Override
    public void add(Course course) {
        courses.put(course.getCode(), course);
    }

    @Override
    public Course get(String code) {
        return courses.get(code);
    }
}