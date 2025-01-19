package com.luis.gamerdoritorituals;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luis.gamerdoritorituals.database.DatabaseManager;
import com.luis.gamerdoritorituals.model.Task;

import java.util.ArrayList;

public class TasksFragment extends Fragment {

    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        // Configurar la lógica de las tareas
        ListView lvTasks = view.findViewById(R.id.lvTasks);
        Button btnAddTask = view.findViewById(R.id.btnAddTask);

        DatabaseManager dbManager = new DatabaseManager(requireContext());
        taskList = dbManager.getAllTasks();
        taskAdapter = new TaskAdapter(requireContext(), taskList);
        lvTasks.setAdapter(taskAdapter);

        // Configuración para editar/eliminar tareas
        lvTasks.setOnItemClickListener((parent, view1, position, id) -> {
            Task selectedTask = taskList.get(position);
            showEditDeleteDialog(selectedTask, position, dbManager);
        });

        // Botón para añadir tareas
        btnAddTask.setOnClickListener(v -> showAddTaskDialog(dbManager));

        return view;
    }

    private void showAddTaskDialog(DatabaseManager dbManager) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        Button btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String description = etTaskDescription.getText().toString().trim();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Task newTask = new Task(title, description);
                long id = dbManager.addTask(newTask);

                if (id != -1) {
                    newTask.setId((int) id);
                    taskList.add(newTask);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Tarea añadida", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error al añadir tarea", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    private void showEditDeleteDialog(Task task, int position, DatabaseManager dbManager) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText etTaskTitle = dialogView.findViewById(R.id.etTaskTitle);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        Button btnSaveTask = dialogView.findViewById(R.id.btnSaveTask);

        etTaskTitle.setText(task.getTitle());
        etTaskDescription.setText(task.getDescription());
        btnSaveTask.setText("Actualizar");

        btnSaveTask.setOnClickListener(v -> {
            String updatedTitle = etTaskTitle.getText().toString().trim();
            String updatedDescription = etTaskDescription.getText().toString().trim();

            if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                task.setTitle(updatedTitle);
                task.setDescription(updatedDescription);
                int rowsAffected = dbManager.updateTask(task);

                if (rowsAffected > 0) {
                    taskList.set(position, task);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Tarea actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}

