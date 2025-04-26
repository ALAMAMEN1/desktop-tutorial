package com.example.myapplication;

public class Student {
    private int id;
    private String email;
    private String formation;
    private String section;
    private String year;
    private String group;

    public Student(int id, String email, String formation, String section, String year, String group) {
        this.id = id;
        this.email = email;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.group = group;
    }

    // Getter methods
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFormation() { return formation; }
    public String getSection() { return section; }
    public String getYear() { return year; }
    public String getGroup() { return group; }
}
