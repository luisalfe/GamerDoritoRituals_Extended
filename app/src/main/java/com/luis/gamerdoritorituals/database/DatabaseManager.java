package com.luis.gamerdoritorituals.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.luis.gamerdoritorituals.model.Task;

import java.util.ArrayList;

public class DatabaseManager extends SQLiteOpenHelper {

    // Nombre de la base de datos y versión
    private static final String DATABASE_NAME = "gamer_tasks.db";
    private static final int DATABASE_VERSION = 2; // Incrementado a 2

    // Nombre de la tabla y columnas para tareas
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COMPLETED = "is_completed";

    // Nombre de la tabla y columnas para aceleración
    public static final String TABLE_ACCELERATION = "acceleration";
    public static final String COLUMN_ACCELERATION_ID = "id";
    public static final String COLUMN_ACCELERATION_AVERAGE = "average";
    public static final String COLUMN_ACCELERATION_TIME = "time_in_motion";
    public static final String COLUMN_ACCELERATION_TIMESTAMP = "timestamp";

    // Query para crear la tabla de tareas
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_COMPLETED + " INTEGER DEFAULT 0" +
            ");";

    // Query para crear la tabla de aceleración
    private static final String CREATE_TABLE_ACCELERATION = "CREATE TABLE " + TABLE_ACCELERATION + " (" +
            COLUMN_ACCELERATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ACCELERATION_AVERAGE + " REAL NOT NULL, " +
            COLUMN_ACCELERATION_TIME + " INTEGER NOT NULL, " +
            COLUMN_ACCELERATION_TIMESTAMP + " TEXT NOT NULL" +
            ");";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tablas al inicializar la base de datos
        db.execSQL(CREATE_TABLE_TASKS);
        db.execSQL(CREATE_TABLE_ACCELERATION);

        // Insertar tareas iniciales
        db.execSQL("INSERT INTO tasks (title, description, is_completed) VALUES " +
                "('Elden Ring', 'Intentar invadir y eliminar a 5 pobres almas', 0), " +
                "('Nutrición', 'Tomarme un Monster y terminar la bolsa de Doritos que tengo a la mitad', 1), " +
                "('Ritual Opcional', 'Intentar acceder al habitáculo llamado ducha y desafiar mi instinto de supervivencia abriendo el grifo', 0)," +
                "('Mirar las Instrucciones', 'Pulsar el icono de ayuda en el menu para saber como funciona este bicho', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualizar la base de datos según la versión
        if (oldVersion < 2) {
            // Añadir la tabla de aceleración si no existe
            db.execSQL(CREATE_TABLE_ACCELERATION);
        }
    }

    // Métodos CRUD para tareas
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TASKS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1;

                tasks.add(new Task(id, title, description, isCompleted));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        int rowsAffected = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsAffected;
    }

    public int deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }

    // Métodos CRUD para aceleración
    public long addAccelerationData(float averageAcceleration, long timeInMotion, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCELERATION_AVERAGE, averageAcceleration);
        values.put(COLUMN_ACCELERATION_TIME, timeInMotion);
        values.put(COLUMN_ACCELERATION_TIMESTAMP, timestamp);

        long id = db.insert(TABLE_ACCELERATION, null, values);
        db.close();
        return id;
    }

    public ArrayList<String> getAllAccelerationData() {
        ArrayList<String> accelerationData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ACCELERATION;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String data = "Aceleración Media: " + cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_ACCELERATION_AVERAGE)) +
                        " m/s², Tiempo: " + cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ACCELERATION_TIME)) +
                        " ms, Inicio: " + cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCELERATION_TIMESTAMP));
                accelerationData.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return accelerationData;
    }

    public int deleteAccelerationData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_ACCELERATION, COLUMN_ACCELERATION_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted;
    }
}

