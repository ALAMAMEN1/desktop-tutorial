package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SchoolDB";
    private static final int DATABASE_VERSION = 1;

    // Create tables SQL
    private static final String CREATE_USERS_TABLE = "CREATE TABLE users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "email TEXT UNIQUE," +
            "password TEXT," +
            "role TEXT)";

    private static final String CREATE_STUDENTS_TABLE = "CREATE TABLE students (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "email TEXT UNIQUE," +
            "formation TEXT," +
            "section TEXT," +
            "year TEXT," +
            "group TEXT)";

    private static final String CREATE_TEACHERS_TABLE = "CREATE TABLE teachers (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT," +
            "email TEXT UNIQUE)";

    private static final String CREATE_MODULES_TABLE = "CREATE TABLE modules (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "name TEXT UNIQUE," +
            "coefficient INTEGER)";

    private static final String CREATE_ASSIGNMENTS_TABLE = "CREATE TABLE assignments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "teacher_email TEXT," +
            "module_name TEXT," +
            "formation TEXT," +
            "section TEXT," +
            "year TEXT," +
            "group TEXT)";

    private static final String CREATE_NOTES_TABLE = "CREATE TABLE notes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "student_id INTEGER," +
            "module_name TEXT," +
            "td REAL," +
            "tp REAL," +
            "exam REAL," +
            "coefficient INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_STUDENTS_TABLE);
        db.execSQL(CREATE_TEACHERS_TABLE);
        db.execSQL(CREATE_MODULES_TABLE);
        db.execSQL(CREATE_ASSIGNMENTS_TABLE);
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS teachers");
        db.execSQL("DROP TABLE IF EXISTS modules");
        db.execSQL("DROP TABLE IF EXISTS assignments");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    // User methods
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
        Cursor cursor = db.query("users",
                new String[]{"role"},
                "email=? AND password=?",
                new String[]{email, password},
                null, null, null);
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }

    // Student methods
    public boolean addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("email", student.getEmail());
        values.put("formation", student.getFormation());
        values.put("section", student.getSection());
        values.put("year", student.getYear());
        values.put("group", student.getGroup());
        long result = db.insert("students", null, values);
        return result != -1;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("students",
                new String[]{"id", "name", "email", "formation", "section", "year", "group"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            students.add(new Student(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            ));
        }
        cursor.close();
        return students;
    }

    // Teacher methods
    public boolean addTeacher(Teacher teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", teacher.getName());
        values.put("email", teacher.getEmail());
        long result = db.insert("teachers", null, values);
        return result != -1;
    }

    public List<String> getAllTeachersEmails() {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("teachers",
                new String[]{"email"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            emails.add(cursor.getString(0));
        }
        cursor.close();
        return emails;
    }
    public List<Assignment> getAssignmentsForTeacher(String email) {
        List<Assignment> assignments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT module_name, formation, section, year, group FROM assignments WHERE teacher_email = ?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            do {
                String moduleName = cursor.getString(0);
                String formation = cursor.getString(1);
                String section = cursor.getString(2);
                String year = cursor.getString(3);
                String group = cursor.getString(4);

                assignments.add(new Assignment(moduleName, formation, section, year, group));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return assignments;
    }
    public List<String> getAssignedModulesForTeacher(String email) {
        List<String> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT module_name FROM assignments WHERE teacher_email = ?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            do {
                modules.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return modules;
    }

    // Module methods
    public boolean addModule(Module module) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", module.name);
        values.put("coefficient", module.coefficient);
        long result = db.insert("modules", null, values);
        return result != -1;
    }

    public List<String> getAllModuleNames() {
        List<String> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("modules",
                new String[]{"name"},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            modules.add(cursor.getString(0));
        }
        cursor.close();
        return modules;
    }

    // Assignment methods
    public boolean assignModuleToTeacher(String teacherEmail, String moduleName,
                                         String formation, String section, String year, String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("teacher_email", teacherEmail);
        values.put("module_name", moduleName);
        values.put("formation", formation);
        values.put("section", section);
        values.put("year", year);
        values.put("group", group);
        long result = db.insert("assignments", null, values);
        return result != -1;
    }

    public List<String> getAllAssignments() {
        List<String> assignments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT teacher_email, module_name FROM assignments", null);
        while (cursor.moveToNext()) {
            assignments.add(cursor.getString(0) + " - " + cursor.getString(1));
        }
        cursor.close();
        return assignments;
    }

    // Note methods
    public boolean insertNote(int studentId, String moduleName,
                              float td, float tp, float exam, int coefficient) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("module_name", moduleName);
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
        Cursor cursor = db.query("notes",
                new String[]{"module_name", "td", "tp", "exam", "coefficient"},
                "student_id=?",
                new String[]{String.valueOf(studentId)},
                null, null, null);
        while (cursor.moveToNext()) {
            notes.add(new Note(
                    cursor.getString(0),
                    cursor.getFloat(1),
                    cursor.getFloat(2),
                    cursor.getFloat(3),
                    cursor.getInt(4)
            ));
        }
        cursor.close();
        return notes;
    }

    // Utility methods
    public List<String> getAllFormations() {
        return getDistinctValues("students", "formation");
    }

    public List<String> getAllSections() {
        return getDistinctValues("students", "section");
    }

    public List<String> getAllYears() {
        return getDistinctValues("students", "year");
    }

    public List<String> getAllGroups() {
        return getDistinctValues("students", "group");
    }

    private List<String> getDistinctValues(String table, String column) {
        List<String> values = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, table,
                new String[]{column},
                null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            values.add(cursor.getString(0));
        }
        cursor.close();
        return values;
    }

    public int getStudentIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("students",
                new String[]{"id"},
                "email=?",
                new String[]{email},
                null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }
}