/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.uca.registration.repo;

import edu.uca.registration.model.Student;
import java.util.*;


public class Studentrepository {
    private Map<String, Student> students = new LinkedHashMap<>();
    
    public void add(Student s) {students.put(s.getID(), s);}
    public Student get(String id) {return students.get(id);}
    public Collection<Student> getAll() {return students.values();}
}
