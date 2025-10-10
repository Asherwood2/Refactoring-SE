package edu.uca.registration.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String code;
    private String title;
    private int capacity;
    private List<String> roster = new ArrayList<>();
    private List<String> waitlist = new ArrayList<>();

    public Course(String code, String title, int capacity) {
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<String> getRoster() {
        return roster;
    }

    public List<String> getWaitlist() {
        return waitlist;
    }

    // Setters
    public void setCode(String code) {
        this.code = code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setRoster(List<String> roster) {
        this.roster = roster;
    }

    public void setWaitlist(List<String> waitlist) {
        this.waitlist = waitlist;
    }
}