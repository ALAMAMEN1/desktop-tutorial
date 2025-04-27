package com.example.myapplication.models;

public class Student {

    private int id;
    private String email;
    private String formation;
    private String section;
    private String year;
    private String groupName;

    public Student(int id, String email, String formation, String section, String year, String groupName) {
        this.id = id;
        this.email = email;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.groupName = groupName;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFormation() {
        return formation;
    }

    public String getSection() {
        return section;
    }

    public String getYear() {
        return year;
    }

    public String getGroupName() {
        return groupName;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
