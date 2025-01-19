package com.luis.gamerdoritorituals;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;
import android.Manifest;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.luis.gamerdoritorituals.database.DatabaseManager;

public class AccelerationActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerationSensor;
    private TextView currentAccelerationText, averageAccelerationText, timeText;

    private boolean enMovimiento = false;
    private long tiempoInicio, tiempoFin;
    private float aceleracionTotal = 0;
    private int conteoDatos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración Edge-to-Edge
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acceleration);

        // Configuración de Insets para ajustar el padding según las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Crear el canal de notificaciones
        createNotificationChannel();


        // Solicitar permiso en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Vincular las vistas del diseño
        currentAccelerationText = findViewById(R.id.currentAcceleration);
        averageAccelerationText = findViewById(R.id.averageAcceleration);
        timeText = findViewById(R.id.timeText);

        // Inicializar el SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }

        Button btnViewRecords = findViewById(R.id.btnViewRecords);
        btnViewRecords.setOnClickListener(v -> {
            Intent intent = new Intent(AccelerationActivity.this, AccelerationRecordsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el listener del sensor
        if (accelerationSensor != null) {
            sensorManager.registerListener(this, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        // Preferencias
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Obtener las preferencias
        String textSize = prefs.getString("text_size", "16"); // Valor predeterminado 16
        String textColor = prefs.getString("text_color", "#000000"); // Valor predeterminado negro

        // Aplicar las preferencias
        currentAccelerationText.setTextSize(Float.parseFloat(textSize));
        currentAccelerationText.setTextColor(Color.parseColor(textColor));

        averageAccelerationText.setTextSize(Float.parseFloat(textSize));
        averageAccelerationText.setTextColor(Color.parseColor(textColor));

        timeText.setTextSize(Float.parseFloat(textSize));
        timeText.setTextColor(Color.parseColor(textColor));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener del sensor
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float aceleracionActual = (float) Math.sqrt(x * x + y * y + z * z);
            currentAccelerationText.setText(String.format("Aceleración actual: %.2f m/s²", aceleracionActual));

            if (aceleracionActual > 1.0) { // Considerar movimiento si la aceleración supera un umbral
                if (!enMovimiento) {
                    enMovimiento = true;
                    tiempoInicio = System.currentTimeMillis();
                    aceleracionTotal = 0;
                    conteoDatos = 0;
                }
                aceleracionTotal += aceleracionActual;
                conteoDatos++;
            } else if (enMovimiento) {
                enMovimiento = false;
                tiempoFin = System.currentTimeMillis();
                float aceleracionMedia = aceleracionTotal / conteoDatos;
                long tiempoEnMilis = tiempoFin - tiempoInicio;

                averageAccelerationText.setText(String.format("Aceleración media: %.2f m/s²", aceleracionMedia));
                timeText.setText(String.format("Tiempo en movimiento: %d ms", tiempoEnMilis));

                // Placeholder para guardar los datos en la base de datos
                guardarEnBaseDeDatos(aceleracionMedia, tiempoEnMilis, tiempoInicio);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se utiliza, pero debe implementarse
    }

    private void guardarEnBaseDeDatos(float aceleracionMedia, long tiempoEnMilis, long timestampInicio) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(timestampInicio));

        DatabaseManager dbManager = new DatabaseManager(this);
        long id = dbManager.addAccelerationData(aceleracionMedia, tiempoEnMilis, timestamp);

        if (id != -1) {
            // Iniciar el servicio de notificación
            Intent notificationIntent = new Intent(this, NotificationService.class);
            notificationIntent.putExtra("message", "Se registró una aceleración media de " + aceleracionMedia + " m/s²");
            startService(notificationIntent);

            Toast.makeText(this, "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
        }
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
            // Navegar a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
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
                .setMessage("Esta aplicación está diseñada para organizar rituales gamer y votar por tu waifu favorita. ¡Diviértete!")
                .setPositiveButton("OK", null)
                .show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "acceleration_channel";
            CharSequence name = "Registros de Aceleración";
            String description = "Notificaciones sobre nuevos registros de aceleración";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Registrar el canal con el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

}
