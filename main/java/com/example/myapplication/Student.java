package com.example.myapplication;

public class Student {
    private int id;
    private String name;
    private String email;
    private String formation;
    private String section;
    private String year;
    private String group;

    public Student(int id, String name, String email, String formation,
                   String section, String year, String group) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.group = group;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getFormation() { return formation; }
    public String getSection() { return section; }
    public String getYear() { return year; }
    public String getGroup() { return group; }
}