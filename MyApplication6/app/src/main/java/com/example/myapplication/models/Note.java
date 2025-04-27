package com.example.myapplication.models;

public class Note {

    private int id;
    private int studentId;
    private String moduleName;
    private float td;
    private float tp;
    private float exam;
    private int coefficient;

    public Note(int id, int studentId, String moduleName, float td, float tp, float exam, int coefficient) {
        this.id = id;
        this.studentId = studentId;
        this.moduleName = moduleName;
        this.td = td;
        this.tp = tp;
        this.exam = exam;
        this.coefficient = coefficient;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public float getTd() {
        return td;
    }

    public float getTp() {
        return tp;
    }

    public float getExam() {
        return exam;
    }

    public int getCoefficient() {
        return coefficient;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setTd(float td) {
        this.td = td;
    }

    public void setTp(float tp) {
        this.tp = tp;
    }

    public void setExam(float exam) {
        this.exam = exam;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }
}
