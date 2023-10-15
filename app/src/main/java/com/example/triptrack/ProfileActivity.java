package com.example.triptrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

/**
 * Esta clase representa la actividad Perfil.
 * Esta actividad muestra el perfil del usuario.
 * Muestra el nombre, los apellidos, el correo electrónico, el nivel y la animación del nivel del usuario.
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); //Generación de Toolbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    if (itemId == R.id.nav_my_trips) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_world) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent searchIntent = new Intent(this, MapamundiActivity.class);
                        startActivity(searchIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_profile) {
                        // Acción para la pestaña "Perfil"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent profileIntent = new Intent(this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }

                    return false;
                }); //Generación BottomNavigationBar

        SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String email = sharedPref.getString("Email: ", "");
        String name = sharedPref.getString("Nombre: ", "");
        String surname = sharedPref.getString("Apellidos: ", "");


        TextView userName = findViewById(R.id.user_name);
        userName.setText(name);

        TextView userSurname = findViewById(R.id.user_surname);
        userSurname.setText(surname);

        TextView userEmail = findViewById(R.id.user_email);
        userEmail.setText(email);

        TextView userLevel = findViewById(R.id.user_level);

        TextView congratulationsMessage = findViewById(R.id.congratText);

        int completedTrips = 10; // Reemplaza esto con el número de viajes completados

        int remainingTrips = 5; // Reemplaza esto con el número de viajes restantes para subir de nivel

// Aquí es donde determinarías el nivel del usuario y cargarías la animación correspondiente
        LottieAnimationView levelAnimation = findViewById(R.id.level_animation);
        String level = "2"; // Reemplaza esto con el nivel del usuario
        userLevel.setText("2");

        switch (level) {
            case "1":
                levelAnimation.setAnimation(R.raw.level1);
                levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
                congratulationsMessage.setText("Enhorabuena. Aún eres un viajero novato has realizado " + completedTrips + " viajes, completa " + remainingTrips + " más para subir de nivel!");
                break;
            case "2":
                levelAnimation.setAnimation(R.raw.level2);
                levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
                congratulationsMessage.setText("Enhorabuena. Aún eres un viajero principiante has realizado " + completedTrips + " viajes, completa " + remainingTrips + " más para subir de nivel!");

                break;
                default:
                // Código para ejecutar si no se encuentra ningún caso
                break;
        }

        levelAnimation.playAnimation();

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
                                Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(homeIntent);
                                return true;
                            case R.id.bottom_nav_world:
                                // Acción para la pestaña "Buscar"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent searchIntent = new Intent(ProfileActivity.this, MapamundiActivity.class);
                                startActivity(searchIntent);
                                return true;
                        }

                        return false;
                    }
                });

    }


}

