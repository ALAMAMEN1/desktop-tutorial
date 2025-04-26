package com.example.myapplication;

public class Assignment {
    private String subject;
    private String formation;
    private String section;
    private String year;
    private String group;

    // Constructor
    public Assignment(String subject, String formation, String section, String year, String group) {
        this.subject = subject;
        this.formation = formation;
        this.section = section;
        this.year = year;
        this.group = group;
    }

    // Getters
    public String getSubject() {
        return subject;
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

    public String getGroup() {
        return group;
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

    public void setGroup(String group) {
        this.group = group;
    }


}
