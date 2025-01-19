package com.luis.gamerdoritorituals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.luis.gamerdoritorituals.database.DatabaseManager;
import com.luis.gamerdoritorituals.model.Task;

import java.util.ArrayList;

public class TaskAdapter extends android.widget.ArrayAdapter<Task> {

    public TaskAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task, parent, false);
        }

        // Obtener la tarea actual
        Task task = getItem(position);

        // Vincular los elementos del diseño
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        CheckBox cbCompleted = convertView.findViewById(R.id.cbCompleted);

        // Configurar los datos
        tvTitle.setText(task.getTitle());
        tvDescription.setText(task.getDescription());
        cbCompleted.setChecked(task.isCompleted());

        // Establecer el fondo según el estado de completado al cargar
        if (task.isCompleted()) {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
        } else {
            convertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
        }

        // Listener para actualizar el estado de completado
        View finalConvertView = convertView;
        cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Actualizar el estado de completado en la tarea
            task.setCompleted(isChecked);

            // Cambiar el fondo según el estado
            if (isChecked) {
                finalConvertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_green_light));
            } else {
                finalConvertView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
            }

            // Actualizar el estado en la base de datos
            DatabaseManager dbManager = new DatabaseManager(getContext());
            dbManager.updateTask(task);
        });

        return convertView;
    }


}
