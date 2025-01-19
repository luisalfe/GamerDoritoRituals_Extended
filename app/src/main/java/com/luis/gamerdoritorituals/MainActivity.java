package com.luis.gamerdoritorituals;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luis.gamerdoritorituals.database.DatabaseManager;
import com.luis.gamerdoritorituals.model.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> taskList; // Lista de tareas
    private TaskAdapter taskAdapter; // Adaptador personalizado para el ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajustes para insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar el DatabaseManager
        DatabaseManager dbManager = new DatabaseManager(this);

        // Inicializar la lista y el adaptador
        taskList = dbManager.getAllTasks(); // Cargar tareas desde la base de datos
        taskAdapter = new TaskAdapter(this, taskList); // Crear el adaptador
        ListView lvTasks = findViewById(R.id.lvTasks);
        lvTasks.setAdapter(taskAdapter); // Conectar el adaptador al ListView

        // Botón para abrir la AccelerationActivity
        Button btnAcceleration = findViewById(R.id.btnAcceleration);
        btnAcceleration.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccelerationActivity.class);
            startActivity(intent);
        });

        // Configurar el listener para editar/eliminar tareas
        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener la tarea seleccionada
            Task selectedTask = taskList.get(position);

            // Mostrar el diálogo de editar/eliminar
            showEditDeleteDialog(selectedTask, position, dbManager);
        });

        // Configurar el listener para eliminar tareas con clic largo
        lvTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Task selectedTask = taskList.get(position);

            // Mostrar un diálogo de confirmación
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Eliminar tarea")
                    .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        // Eliminar la tarea de la base de datos
                        int rowsDeleted = dbManager.deleteTask(selectedTask.getId());
                        if (rowsDeleted > 0) {
                            taskList.remove(position); // Eliminar de la lista
                            taskAdapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();

            return true; // Indica que manejamos el evento de clic largo
        });

        // Zona para ir vinculando los elementos excepto el LV que va arriba con el adaptador
        Button btnAddTask = findViewById(R.id.btnAddTask);

        // Configuracion del boton flotante para el concurso
        FloatingActionButton fabVote = findViewById(R.id.fabVote);
        fabVote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VoteActivity.class);
            startActivity(intent);
        });

        // Configuración del botón para añadir tareas
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflar el diseño del diálogo
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

                // Crear el diálogo
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.show();

                // Vincular los elementos del diálogo
                EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
                EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
                Button btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

                // Configurar el botón "Guardar"
                btnSaveTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = etTaskTitle.getText().toString().trim();
                        String description = etTaskDescription.getText().toString().trim();

                        // Validar que no estén vacíos
                        if (title.isEmpty() || description.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                        } else {
                            // Crear y añadir la nueva tarea
                            Task newTask = new Task(title, description);
                            long id = dbManager.addTask(newTask);

                            if (id != -1) {
                                newTask.setId((int) id);
                                taskList.add(newTask);
                                taskAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Tarea añadida", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Error al añadir tarea", Toast.LENGTH_SHORT).show();
                            }

                            dialog.dismiss(); // Cerrar el diálogo
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflar el menú con el archivo XML
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main) {
            // Ya estás en la MainActivity
            Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_games) {
            // Navegar a la lista de juegos
            Intent intent = new Intent(this, GamesActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_about) {
            // Mostrar el diálogo "Acerca de"
            showAboutDialog();
            return true;
        } else if (id == R.id.menu_settings) {
            // Navegar a la actividad de preferencias
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para mostrar el diálogo "Acerca de"
    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Acerca de")
                .setMessage("Esta aplicación está diseñada para organizar rituales gamer. ¡Diviértete!\n\n" +
                        "Instrucciones:\n" +
                        "• Para añadir una tarea, pulsa el botón 'Añadir Tarea'.\n" +
                        "• Para editar una tarea, pulsa una vez sobre la tarea a modificar.\n" +
                        "• Para eliminar una tarea, mantén pulsada la tarea durante unos instantes.")
                .setPositiveButton("OK", null)
                .show();
    }


    // Método para mostrar el diálogo de editar
    private void showEditDeleteDialog(Task task, int position, DatabaseManager dbManager) {
        // Inflar el diseño del diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        // Crear el diálogo
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Vincular los elementos del diálogo
        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        Button btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        // Rellenar los campos con los datos actuales de la tarea
        etTaskTitle.setText(task.getTitle());
        etTaskDescription.setText(task.getDescription());
        btnSaveTask.setText("Actualizar");

        // Configurar el botón "Actualizar"
        btnSaveTask.setOnClickListener(v -> {
            String updatedTitle = etTaskTitle.getText().toString().trim();
            String updatedDescription = etTaskDescription.getText().toString().trim();

            // Validar campos no vacíos
            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                // Actualizar la tarea
                task.setTitle(updatedTitle);
                task.setDescription(updatedDescription);

                int rowsAffected = dbManager.updateTask(task);
                if (rowsAffected > 0) {
                    taskList.set(position, task); // Actualizar en la lista
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss(); // Cerrar el diálogo
            }
        });

        // Añadir una opción para eliminar la tarea
        builder.setNeutralButton("Eliminar", (d, which) -> {
            int rowsDeleted = dbManager.deleteTask(task.getId());
            if (rowsDeleted > 0) {
                taskList.remove(position); // Eliminar de la lista
                taskAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Error al eliminar tarea", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss(); // Cerrar el diálogo
        });
    }
}
/*
package com.luis.gamerdoritorituals;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Configurar adaptador
        TabsAdapter tabsAdapter = new TabsAdapter(this);
        viewPager.setAdapter(tabsAdapter);

        // Vincular TabLayout con ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Tareas");
                    break;
                case 1:
                    tab.setText("Acelerómetro");
                    break;
                case 2:
                    tab.setText("Registros");
                    break;
            }
        }).attach();
    }
}

*/


