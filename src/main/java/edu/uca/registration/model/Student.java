package edu.uca.registration.model;

public class Student {

    //student ID
    public String id;
    // student full name
    public String name;
    // student email address
    public String email;

    public Student(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    //Getter for id
    public String getId(){
        return id;
    }
    
    @Override
    public String toString() {
        return id + " " + name + " <" + email + ">";
    }
}