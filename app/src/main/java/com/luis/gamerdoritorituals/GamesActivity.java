package com.luis.gamerdoritorituals;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luis.gamerdoritorituals.model.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_games);

        // Ajustes para insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración del RecyclerView para mostrar la lista de juegos
        RecyclerView rvGames = findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(this));

        // Crear la lista estática de juegos
        List<Game> gameList = new ArrayList<>();
        gameList.add(new Game("Elden Ring", R.drawable.elden_ring)); // Asegúrate de que esta imagen exista en res/drawable
        gameList.add(new Game("God of War", R.drawable.god_of_war)); // Asegúrate de que esta imagen exista en res/drawable
        gameList.add(new Game("The Witcher 3", R.drawable.the_witcher_3)); // Asegúrate de que esta imagen exista en res/drawable

        // Configurar el adaptador
        GameAdapter gameAdapter = new GameAdapter(this, gameList);
        rvGames.setAdapter(gameAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflar el menú
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main) {
            // Volver a la MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_games) {
            // Ya estás en GamesActivity
            Toast.makeText(this, "Ya estás en Juegos", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_about) {
            // Mostrar el diálogo "Acerca de"
            showAboutDialog();
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
                        "Esta Activity sería una lista estática de las últimas novedades top en juegos.\n" +
                        "Realmente es puramente informativa para el usuario ya que yo como desarrollador iría incluyéndolos.\n" +
                        "Lo ideal sería que se fuera actualizando de manera dinámica ¡Pero en este primer ejercicio prefería no complicarme la existencia!.")
                .setPositiveButton("OK", null)
                .show();
    }
}
