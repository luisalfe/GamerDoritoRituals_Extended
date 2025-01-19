package com.luis.gamerdoritorituals;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Cargar el fragmento de preferencias
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
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
}

