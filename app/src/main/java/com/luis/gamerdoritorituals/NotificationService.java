package com.luis.gamerdoritorituals;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Obtener datos del Intent
        String message = intent.getStringExtra("message");

        // Crear la notificación
        Notification notification = new NotificationCompat.Builder(this, "acceleration_channel")
                .setSmallIcon(R.drawable.ic_info) // Asegúrate de tener un icono
                .setContentTitle("Nuevo registro de aceleración")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }

        // Detener el servicio después de enviar la notificación
        stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
