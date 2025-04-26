package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.models.Assignment;
import com.example.myapplication.models.Note;
import com.example.myapplication.models.Student;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_management.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // إنشاء الجداول الأساسية
        db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, formation TEXT, section TEXT, year TEXT, groupName TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, studentId INTEGER, moduleName TEXT, td REAL, tp REAL, exam REAL, coefficient INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS teachers (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS assignments (id INTEGER PRIMARY KEY AUTOINCREMENT, teacher TEXT, subject TEXT, formation TEXT, section TEXT, year TEXT, groupName TEXT, coefficient INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS formations (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS sections (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS years (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
        db.execSQL("CREATE TABLE IF NOT EXISTS groups (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");

        // بيانات جاهزة أولية
        db.execSQL("INSERT INTO formations (name) VALUES ('علوم الحاسوب'), ('هندسة كهربائية'), ('رياضيات تطبيقية')");
        db.execSQL("INSERT INTO sections (name) VALUES ('A'), ('B'), ('C')");
        db.execSQL("INSERT INTO years (name) VALUES ('السنة الأولى'), ('السنة الثانية'), ('السنة الثالثة')");
        db.execSQL("INSERT INTO groups (name) VALUES ('G1'), ('G2'), ('G3')");
        db.execSQL("INSERT INTO subjects (name) VALUES ('رياضيات'), ('برمجة')");
        db.execSQL("INSERT INTO teachers (email) VALUES ('teacher1@example.com'), ('teacher2@example.com')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // تحديث القاعدة إذا تغيرت النسخة
    }

    // تسجيل مستخدم جديد مع دعم المعلمين
    public boolean registerUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("users", null, values);

        // إذا كان الدور معلم ➔ أضفه لجدول المعلمين
        if (result != -1 && role.equals("teacher")) {
            ContentValues teacherValues = new ContentValues();
            teacherValues.put("email", email);
            db.insert("teachers", null, teacherValues);
        }

        return result != -1;
    }

    // التحقق من تسجيل الدخول
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

    // جلب كل إيميلات الطلاب
    public List<String> getAllStudentEmails() {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE role = 'student'", null);
        if (cursor.moveToFirst()) {
            do {
                emails.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return emails;
    }

    // إضافة طالب
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

    // إضافة مادة
    public boolean insertSubject(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        long result = db.insert("subjects", null, values);
        return result != -1;
    }

    // جلب قائمة بسيطة من أي جدول
    public List<String> getSimpleList(String table, String column) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + column + " FROM " + table, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ربط مادة بمعلم ومجموعة
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

    // جلب الطلاب المرتبطين بمادة ومعلم
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
    public List<String> getSimpleListByCondition(String table, String column, String whereColumn, String whereValue) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + column + " FROM " + table + " WHERE " + whereColumn + "=?", new String[]{whereValue});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getSubjectsForTeacher(String teacherEmail) {
        return getSimpleListByCondition("assignments", "subject", "teacher", teacherEmail);
    }

    // إضافة تكوين/شعبة/سنة/مجموعة
    public boolean insertFormation(String name) { return insertSimple("formations", name); }
    public boolean insertSection(String name) { return insertSimple("sections", name); }
    public boolean insertYear(String name) { return insertSimple("years", name); }
    public boolean insertGroup(String name) { return insertSimple("groups", name); }

    private boolean insertSimple(String table, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        long result = db.insert(table, null, values);
        return result != -1;
    }

    public List<String> getFormations() { return getSimpleList("formations", "name"); }
    public List<String> getSections() { return getSimpleList("sections", "name"); }
    public List<String> getYears() { return getSimpleList("years", "name"); }
    public List<String> getGroups() { return getSimpleList("groups", "name"); }

    // إضافة النقاط
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

    // جلب النقاط لطالب
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

    // جلب ID طالب بالإيميل
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


}
