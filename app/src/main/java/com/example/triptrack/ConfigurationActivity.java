package com.example.triptrack;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        generacionToolbar(); //Generación de Toolbar
        generacionBottomNavigationBar(); //Generación BottomNavigationBar

    }

    private void generacionToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

    }

    private void generacionBottomNavigationBar(){

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Obtener el ID del item seleccionado
                        int itemId = item.getItemId();

                        // Realizar acciones basadas en el item seleccionado
                        switch (itemId) {
                            case R.id.bottom_nav_my_trips:
                                // Acción para la pestaña "Inicio"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent homeIntent = new Intent(ConfigurationActivity.this, MainActivity.class);
                                startActivity(homeIntent);
                                return true;
                            case R.id.bottom_nav_world:
                                // Acción para la pestaña "Buscar"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent searchIntent = new Intent(ConfigurationActivity.this, MapamundiActivity.class);
                                startActivity(searchIntent);
                                return true;
                        }

                        return false;
                    }
                });

    }
}

