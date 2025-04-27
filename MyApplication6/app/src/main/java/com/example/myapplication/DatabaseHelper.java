package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.models.Student;
import com.example.myapplication.models.Assignment;
import com.example.myapplication.models.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_management.db";
    private static final int DATABASE_VERSION = 2; // تم زيادة رقم الإصدار

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء الجداول
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, formation TEXT, section TEXT, year TEXT, groupName TEXT)");
        db.execSQL("CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, studentId INTEGER, moduleName TEXT, td REAL, tp REAL, exam REAL, coefficient INTEGER)");
        db.execSQL("CREATE TABLE teachers (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE)");
        db.execSQL("CREATE TABLE assignments (id INTEGER PRIMARY KEY AUTOINCREMENT, teacher TEXT, subject TEXT, formation TEXT, section TEXT, year TEXT, groupName TEXT, coefficient INTEGER)");
        db.execSQL("CREATE TABLE formations (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE sections (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE years (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE groups (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");

        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        insertUserIfNotExists(db, "admin@example.com", "123456", "admin");
        insertUserIfNotExists(db, "teacher1@example.com", "123456", "teacher");
        insertUserIfNotExists(db, "teacher2@example.com", "123456", "teacher");

        insertSubjectIfNotExists(db, "رياضيات");
        insertSubjectIfNotExists(db, "برمجة");
        insertSubjectIfNotExists(db, "فيزياء");

        insertFormationIfNotExists(db, "العلوم");
        insertFormationIfNotExists(db, "الهندسة");

        insertSectionIfNotExists(db, "الشعبة 1");
        insertSectionIfNotExists(db, "الشعبة 2");

        insertYearIfNotExists(db, "السنة الأولى");
        insertYearIfNotExists(db, "السنة الثانية");

        insertGroupIfNotExists(db, "المجموعة 1");
        insertGroupIfNotExists(db, "المجموعة 2");
    }

    private void insertUserIfNotExists(SQLiteDatabase db, String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertSubjectIfNotExists(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("subjects", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertFormationIfNotExists(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("formations", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertSectionIfNotExists(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("sections", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertYearIfNotExists(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("years", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertGroupIfNotExists(SQLiteDatabase db, String name) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insertWithOnConflict("groups", null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // عند الترقية، نمسح الجداول القديمة ونعيد إنشائها
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS subjects");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS teachers");
        db.execSQL("DROP TABLE IF EXISTS assignments");
        db.execSQL("DROP TABLE IF EXISTS formations");
        db.execSQL("DROP TABLE IF EXISTS sections");
        db.execSQL("DROP TABLE IF EXISTS years");
        db.execSQL("DROP TABLE IF EXISTS groups");
        onCreate(db);
    }

    public boolean registerUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE email=? AND password=?", new String[]{email, password});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }
    public List<String> getAllTeachers() {
        List<String> teachers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE role='teacher'", null);
        if (cursor.moveToFirst()) {
            do {
                teachers.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return teachers;
    }

    public List<String> getAllStudentEmails() {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE role='student'", null);
        if (cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return emails;
    }

    public boolean insertStudent(String email, String formation, String section, String year, String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("formation", formation);
        values.put("section", section);
        values.put("year", year);
        values.put("groupName", groupName);
        long result = db.insert("students", null, values);
        return result != -1;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM students", null);
        if (cursor.moveToFirst()) {
            do {
                students.add(new Student(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("formation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("section")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("groupName"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }

    public boolean insertSubject(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        long result = db.insert("subjects", null, values);
        return result != -1;
    }

    public List<String> getAllSubjects() {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM subjects", null);
        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    // ربط مادة بمعلم
    public boolean assignSubject(String teacher, String subject, String formation, String section, String year, String groupName, int coefficient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("teacher", teacher);
        values.put("subject", subject);
        values.put("formation", formation);
        values.put("section", section);
        values.put("year", year);
        values.put("groupName", groupName);
        values.put("coefficient", coefficient);
        long result = db.insert("assignments", null, values);
        return result != -1;
    }

    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM assignments", null);
        if (cursor.moveToFirst()) {
            do {
                assignments.add(new Assignment(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("teacher")),
                        cursor.getString(cursor.getColumnIndexOrThrow("subject")),
                        cursor.getString(cursor.getColumnIndexOrThrow("formation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("section")),
                        cursor.getString(cursor.getColumnIndexOrThrow("year")),
                        cursor.getString(cursor.getColumnIndexOrThrow("groupName")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("coefficient"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignments;
    }

    public int getStudentIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM students WHERE email=?", new String[]{email});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    public boolean insertNote(int studentId, String moduleName, float td, float tp, float exam, int coefficient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentId", studentId);
        values.put("moduleName", moduleName);
        values.put("td", td);
        values.put("tp", tp);
        values.put("exam", exam);
        values.put("coefficient", coefficient);
        long result = db.insert("notes", null, values);
        return result != -1;
    }

    public List<Note> getNotesForStudent(int studentId) {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes WHERE studentId=?", new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            do {
                notes.add(new Note(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        studentId,
                        cursor.getString(cursor.getColumnIndexOrThrow("moduleName")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("td")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("tp")),
                        cursor.getFloat(cursor.getColumnIndexOrThrow("exam")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("coefficient"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public List<String> getSimpleList(String table, String column) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + column + " FROM " + table, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));  // إضافة القيمة الموجودة في العمود
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getFormations() {
        return getSimpleList("formations", "name");
    }

    public List<String> getSections() {
        return getSimpleList("sections", "name");
    }

    public List<String> getYears() {
        return getSimpleList("years", "name");
    }

    public List<String> getGroups() {
        return getSimpleList("groups", "name");
    }

    public List<String> getSubjectsForTeacher(String teacherEmail) {
        List<String> subjects = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT subject FROM assignments WHERE teacher=?",
                new String[]{teacherEmail}
        );
        if (cursor.moveToFirst()) {
            do {
                subjects.add(cursor.getString(0));  // إضافة المادة
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subjects;
    }

    public List<Student> getStudentsForTeacherAndSubject(String teacherEmail, String subject) {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor assignmentCursor = db.rawQuery(
                "SELECT formation, section, year, groupName FROM assignments WHERE teacher=? AND subject=?",
                new String[]{teacherEmail, subject}
        );

        if (assignmentCursor.moveToFirst()) {
            do {
                String formation = assignmentCursor.getString(0);
                String section = assignmentCursor.getString(1);
                String year = assignmentCursor.getString(2);
                String groupName = assignmentCursor.getString(3);

                Cursor studentCursor = db.rawQuery(
                        "SELECT * FROM students WHERE formation=? AND section=? AND year=? AND groupName=?",
                        new String[]{formation, section, year, groupName}
                );

                if (studentCursor.moveToFirst()) {
                    do {
                        students.add(new Student(
                                studentCursor.getInt(studentCursor.getColumnIndexOrThrow("id")),
                                studentCursor.getString(studentCursor.getColumnIndexOrThrow("email")),
                                formation,
                                section,
                                year,
                                groupName
                        ));
                    } while (studentCursor.moveToNext());
                }
                studentCursor.close();

            } while (assignmentCursor.moveToNext());
        }
        assignmentCursor.close();
        return students;
    }
}
