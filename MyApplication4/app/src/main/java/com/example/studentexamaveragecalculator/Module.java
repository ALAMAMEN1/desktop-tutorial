package com.example.studentexamaveragecalculator;

public class Module {
    public String name;
    public int coefficient;
    public String semester;
    public double td = 0, tp = 0, exam = 0;

    public Module(String name, int coefficient, String semester) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Module name cannot be empty");
        }
        if (coefficient <= 0) {
            throw new IllegalArgumentException("Coefficient must be positive");
        }
        this.name = name;
        this.coefficient = coefficient;
        this.semester = semester;
    }

    public Module() {}

    public double getModuleAverage() {
        if ((td < 0 || td > 20) || (tp < 0 || tp > 20) || (exam < 0 || exam > 20)) {
            return 0;
        }

        double sum = (td > 0 ? td : 0) + (tp > 0 ? tp : 0);
        int count = (td > 0 ? 1 : 0) + (tp > 0 ? 1 : 0);
        double average = (count == 0) ? 0 : sum / count;
        return (average + exam) / 2;
    }
}