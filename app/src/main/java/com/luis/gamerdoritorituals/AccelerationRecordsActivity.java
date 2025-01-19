package com.luis.gamerdoritorituals;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.luis.gamerdoritorituals.database.DatabaseManager;

import java.util.ArrayList;

public class AccelerationRecordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration_records);

        // Título
        setTitle("Registros de Aceleración");

        // Obtener la lista de registros desde la base de datos
        DatabaseManager dbManager = new DatabaseManager(this);
        ArrayList<String> accelerationRecords = dbManager.getAllAccelerationData();

        // Configurar el ListView
        ListView lvAccelerationRecords = findViewById(R.id.lvAccelerationRecords);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, accelerationRecords);
        lvAccelerationRecords.setAdapter(adapter);

        // Configurar el botón para compartir estadísticas
        Button btnShareStats = findViewById(R.id.btnShareStats);
        btnShareStats.setOnClickListener(v -> {
            String estadisticas = calcularEstadisticas();

            // Crear Intent para compartir
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, estadisticas);

            // Mostrar selector para compartir
            startActivity(Intent.createChooser(shareIntent, "Compartir estadísticas con:"));
        });
    }

    private String calcularEstadisticas() {
        DatabaseManager dbManager = new DatabaseManager(this);
        ArrayList<String> registros = dbManager.getAllAccelerationData();

        if (registros.isEmpty()) {
            return "No hay registros de aceleración disponibles.";
        }

        // Variables para estadísticas
        float suma = 0;
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        int totalRegistros = 0;

        // Obtener datos de la tabla y calcular estadísticas
        SQLiteDatabase db = dbManager.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT average FROM acceleration", null);

        if (cursor.moveToFirst()) {
            do {
                float promedio = cursor.getFloat(0);
                suma += promedio;
                max = Math.max(max, promedio);
                min = Math.min(min, promedio);
                totalRegistros++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // Calcular estadísticas
        float promedioGeneral = suma / totalRegistros;

        return "Estadísticas de Aceleración:\n" +
                "Promedio: " + promedioGeneral + " m/s²\n" +
                "Máximo: " + max + " m/s²\n" +
                "Mínimo: " + min + " m/s²\n" +
                "Total registros: " + totalRegistros;
    }

}
