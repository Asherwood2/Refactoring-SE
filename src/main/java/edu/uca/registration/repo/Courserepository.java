/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uca.registration.repo;

import edu.uca.registration.model.Course;
import java.util.*;


public class Courserepository {
    private Map<String, Course> courses = new LinkedHashMap<>();
    
    public void add(Course c){
        courses.put(c.getCode(), c);
    }
    
    public Course get(String code){
        return courses.get(code);
    }
    
    public Collection<Course> getAll(){
        return courses.values();
    }
}
