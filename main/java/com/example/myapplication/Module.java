package com.example.myapplication;

public class Module {
    public String name;
    public float td;
    public float tp;
    public float exam;
    public int coefficient;

    public Module(String name, float td, float tp, float exam, int coefficient) {
        this.name = name;
        this.td = td;
        this.tp = tp;
        this.exam = exam;
        this.coefficient = coefficient;
    }

    public float getAverage() {
        return (td * 0.2f + tp * 0.2f + exam * 0.6f);
    }
}