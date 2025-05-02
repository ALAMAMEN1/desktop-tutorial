package com.example.studentexamaveragecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StudentDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_SPECIALIZATION = "specialization";
    private static final String TABLE_MODULES = "modules";
    private static final String TABLE_RESULTS = "results";
    private static final String COLUMN_MODULE_ID = "module_id";
    private static final String COLUMN_MODULE_NAME = "module_name";
    private static final String COLUMN_COEFFICIENT = "coefficient";
    private static final String COLUMN_SEMESTER = "semester";
    private static final String COLUMN_TD = "td";
    private static final String COLUMN_TP = "tp";
    private static final String COLUMN_EXAM = "exam";
    private static final String COLUMN_STUDENT_ID = "student_id";
    private static final String COLUMN_AVERAGE = "average";
    private static final String COLUMN_STATUS = "status";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_SPECIALIZATION + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        String CREATE_MODULES_TABLE = "CREATE TABLE " + TABLE_MODULES + "("
                + COLUMN_MODULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MODULE_NAME + " TEXT,"
                + COLUMN_COEFFICIENT + " INTEGER,"
                + COLUMN_SEMESTER + " TEXT)";
        db.execSQL(CREATE_MODULES_TABLE);
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_STUDENT_ID + " INTEGER,"
                + COLUMN_MODULE_ID + " INTEGER,"
                + COLUMN_TD + " REAL,"
                + COLUMN_TP + " REAL,"
                + COLUMN_EXAM + " REAL,"
                + COLUMN_AVERAGE + " REAL,"
                + COLUMN_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_STUDENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_MODULE_ID + ") REFERENCES " + TABLE_MODULES + "(" + COLUMN_MODULE_ID + "))";
        db.execSQL(CREATE_RESULTS_TABLE);


    }
    public boolean isModuleExists(String moduleName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MODULES,
                new String[]{COLUMN_MODULE_ID},
                COLUMN_MODULE_NAME + "=?",
                new String[]{moduleName},
                null, null, null);

        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public List<Module> getModulesBySemester(String semester) {
        List<Module> modules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MODULES,
                new String[]{COLUMN_MODULE_ID, COLUMN_MODULE_NAME, COLUMN_COEFFICIENT, COLUMN_SEMESTER},
                COLUMN_SEMESTER + "=?",
                new String[]{semester},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Module module = new Module();
                module.name = cursor.getString(1);
                module.coefficient = cursor.getInt(2);
                module.semester = cursor.getString(3);
                modules.add(module);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return modules;
    }
    public boolean hasModulesForSemester(int semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MODULES,
                new String[]{COLUMN_MODULE_ID},
                COLUMN_SEMESTER + "=?",
                new String[]{String.valueOf(semester)},
                null, null, null);

        boolean hasModules = cursor.getCount() > 0;
        cursor.close();
        return hasModules;
    }

    public void importModulesFromJson(Context context, int semester, JSONArray jsonArray, ImportCallback callback) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_MODULES, COLUMN_SEMESTER + "=?", new String[]{String.valueOf(semester)});

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String moduleName = obj.getString("Nom_module");
                int coefficient = obj.getInt("Coefficient");

                ContentValues values = new ContentValues();
                values.put(COLUMN_MODULE_NAME, moduleName);
                values.put(COLUMN_COEFFICIENT, coefficient);
                values.put(COLUMN_SEMESTER, String.valueOf(semester));

                db.insert(TABLE_MODULES, null, values);
            }

            db.setTransactionSuccessful();
            callback.onComplete();
        } catch (Exception e) {
            callback.onError(e.getMessage());
            Log.e("DatabaseHelper", "Error importing modules", e);
        } finally {
            db.endTransaction();
        }
    }
    public boolean saveStudentResults(int studentId, List<Module> modules) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE_RESULTS, COLUMN_STUDENT_ID + "=?",
                    new String[]{String.valueOf(studentId)});

            for (Module module : modules) {
                if (!isModuleExists(module.name)) {
                    continue;
                }

                ContentValues values = new ContentValues();
                values.put(COLUMN_STUDENT_ID, studentId);
                values.put(COLUMN_MODULE_ID, getModuleId(module.name));
                values.put(COLUMN_TD, Math.max(0, Math.min(20, module.td)));
                values.put(COLUMN_TP, Math.max(0, Math.min(20, module.tp)));
                values.put(COLUMN_EXAM, Math.max(0, Math.min(20, module.exam)));
                values.put(COLUMN_AVERAGE, module.getModuleAverage());
                values.put(COLUMN_STATUS, module.getModuleAverage() >= 10 ? "ناجح" : "راسب");

                db.insert(TABLE_RESULTS, null, values);
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }
    private int getModuleId(String moduleName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MODULES,
                new String[]{COLUMN_MODULE_ID},
                COLUMN_MODULE_NAME + "=?",
                new String[]{moduleName},
                null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }
    public String getOverallResults(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder results = new StringBuilder();

        results.append("نتائج الفصل الأول:\n");
        results.append(getSemesterResults(studentId, "1"));
        results.append("\n\n");

        results.append("نتائج الفصل الثاني:\n");
        results.append(getSemesterResults(studentId, "2"));

        return results.toString();
    }
    private String getSemesterResults(int studentId, String semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder semesterResults = new StringBuilder();

        String query = "SELECT m." + COLUMN_MODULE_NAME + ", r." + COLUMN_TD + ", r." + COLUMN_TP +
                ", r." + COLUMN_EXAM + ", r." + COLUMN_AVERAGE + ", r." + COLUMN_STATUS +
                " FROM " + TABLE_RESULTS + " r INNER JOIN " + TABLE_MODULES + " m " +
                "ON r." + COLUMN_MODULE_ID + " = m." + COLUMN_MODULE_ID +
                " WHERE r." + COLUMN_STUDENT_ID + " = ? AND m." + COLUMN_SEMESTER + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId), semester});

        if (cursor.moveToFirst()) {
            do {
                semesterResults.append(String.format(
                        "%s: TD=%.1f, TP=%.1f, Exam=%.1f, Average=%.2f, Status=%s\n",
                        cursor.getString(0),
                        cursor.getDouble(1),
                        cursor.getDouble(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return semesterResults.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean addUser(String username, String password, String fullName, String specialization) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_SPECIALIZATION, specialization);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User authenticate(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_FULL_NAME, COLUMN_SPECIALIZATION},
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
            );
            cursor.close();
            return user;
        }
        return null;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public interface ImportCallback {
        void onComplete();
        void onError(String message);
    }
}

