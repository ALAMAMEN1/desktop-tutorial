package com.example.myapplication.models;

public class Assignment {
    private int id;
    private String teacher;
    private String subject;
    private String formation;
    private String section;
    private String year;
    private String groupName;
    private int coefficient; // معامل المادة

    public Assignment(int id, String teacher, String subject, String formation, String section, String year, String groupName, int coefficient) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.groupName = groupName;
        this.coefficient = coefficient;
    }

    // Getters
    public int getId() { return id; }
    public String getTeacher() { return teacher; }
    public String getSubject() { return subject; }
    public String getFormation() { return formation; }
    public String getSection() { return section; }
    public String getYear() { return year; }
    public String getGroupName() { return groupName; }
    public int getCoefficient() { return coefficient; }
}
