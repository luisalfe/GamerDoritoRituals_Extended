package com.luis.gamerdoritorituals.model;

public class Task {
    private int id; // Identificador único como entero
    private String title;
    private String description;
    private boolean isCompleted;

    // Constructor vacío por defecto (Enrique recomendó crearlo pero ahora mismo no caigo para
    // que lo usaré
    public Task() {
    }

    // Constructor sin ID (se usará al crear nuevas tareas antes de guardarlas en la base de datos)
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false; // Por defecto, la tarea no está completada
    }

    // Constructor completo (por ejemplo, al leer datos desde SQLite)
    public Task(int id, String title, String description, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
