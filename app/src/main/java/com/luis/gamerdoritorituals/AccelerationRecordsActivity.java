package com.luis.gamerdoritorituals;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
    }
}
