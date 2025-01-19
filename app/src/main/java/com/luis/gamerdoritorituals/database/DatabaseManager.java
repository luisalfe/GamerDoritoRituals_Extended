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
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COMPLETED = "is_completed";

    // Query para crear la tabla
    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_COMPLETED + " INTEGER DEFAULT 0" +
            ");";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla al inicializar la base de datos
        db.execSQL(CREATE_TABLE_TASKS);

        // Insertar tareas iniciales
        db.execSQL("INSERT INTO tasks (title, description, is_completed) VALUES " +
                "('Elden Ring', 'Intentar invadir y eliminar a 5 pobres almas', 0), " +
                "('Nutrición', 'Tomarme un Monster y terminar la bolsa de Doritos que tengo a la mitad', 1), " +
                "('Ritual Opcional', 'Intentar acceder al habitáculo llamado ducha y desafiar mi instinto de supervivencia abriendo el grifo', 0)," +
                "('Mirar las Instrucciones', 'Pulsar el icono de ayuda en el menu para saber como funciona este bicho', 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualizar la base de datos si cambia la versión
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }


    // Creacion de todos los metodos CRUD para manejar la base

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

        // Insertar la tarea y devolver el ID generado
        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id; // Devuelve el ID de la nueva tarea
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

        // Actualizar la tarea por ID
        int rowsAffected = db.update(TABLE_TASKS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsAffected; // Devuelve el número de filas afectadas
    }

    public int deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar la tarea por ID
        int rowsDeleted = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted; // Devuelve el número de filas eliminadas
    }






}
