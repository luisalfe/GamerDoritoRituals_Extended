package com.luis.gamerdoritorituals;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vote);

        // Ajustar insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
            // Navegar a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_games) {
            // Navegar a GamesActivity
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

    // Método para votar por Waifu 1
    public void voteWaifu1(android.view.View view) {
        showVoteDialog("Tifa Lockhart (Final Fantasy VII)");
    }

    // Método para votar por Waifu 2
    public void voteWaifu2(android.view.View view) {
        showVoteDialog("2B (NieR: Automata)");
    }

    // Método para votar por Waifu 3
    public void voteWaifu3(android.view.View view) {
        showVoteDialog("Geralt de Rivia (The Witcher)");
    }

    // Método para votar por Waifu 4
    public void voteWaifu4(android.view.View view) {
        showVoteDialog("Ugandan Knuckles-chan (RV Chat)");
    }

    // Mostrar el diálogo de votación
    private void showVoteDialog(String waifuName) {
        // Crear el cuadro de texto para el correo electrónico
        EditText emailInput = new EditText(this);
        emailInput.setHint("Introduce tu correo electrónico");

        // Crear y mostrar el diálogo
        new AlertDialog.Builder(this)
                .setTitle("¡Vota por " + waifuName + "!")
                .setMessage("Introduce tu correo electrónico para participar en el sorteo.\n\n" +
                        "Si usted se llama Enrique, acepta que al proporcionarnos su correo acepta poner una buena nota en este feedback " +
                        "y poner un examen sencillito.\n\n" +
                        "Esto no es un contrato vinculante pero todos los alumnos se lo agradecerán.")
                .setView(emailInput)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = emailInput.getText().toString().trim();
                    if (!email.isEmpty()) {
                        Toast.makeText(this, "¡Gracias por votar por " + waifuName + "!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Por favor, introduce tu correo.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
