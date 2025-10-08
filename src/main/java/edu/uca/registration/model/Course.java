package edu.uca.registration.model;
//test

import java.util.ArrayList;
import java.util.List;

public class Course {
    //course identifier
    public String code;

    //Title of course
    public String title;

    //max students in course
    public int capacity;

    //list of students ID in course
    public List<String> roster = new ArrayList<>();

    //list of student IDs on waitlist
    public List<String> waitlist = new ArrayList<>();

    public Course(String code, String title, int capacity) {
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }
}