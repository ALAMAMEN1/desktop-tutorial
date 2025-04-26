package com.example.myapplication;

public class Note {
    private String subject;
    private float td;
    private float tp;
    private float exam;
    private int coefficient;

    public Note(String subject, float td, float tp, float exam, int coefficient) {
        this.subject = subject;
        this.td = td;
        this.tp = tp;
        this.exam = exam;
        this.coefficient = coefficient;
    }

    // Getters
    public String getSubject() {
        return subject;
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

    // حساب المعدل لهذه المادة
    public float getAverage() {
        return (td * 0.2f) + (tp * 0.2f) + (exam * 0.6f); // يمكن تعديل الأوزان حسب المطلوب
    }

    // عرض تفاصيل النقطة (اختياري)
    public String getNoteDetails() {
        return "TD: " + td + ", TP: " + tp + ", Exam: " + exam + ", Average: " + getAverage();
    }
}

