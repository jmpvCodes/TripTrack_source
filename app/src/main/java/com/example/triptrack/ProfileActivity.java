package com.example.triptrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tooltip.Tooltip;
import org.w3c.dom.Text;

import java.util.Objects;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta clase representa la actividad Perfil.
 * Esta actividad muestra el perfil del usuario.
 * Muestra el nombre, los apellidos, el correo electrónico, el nivel y la animación del nivel del usuario.
 */
public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

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
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_profile);
        bottomNavigationView.setOnItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    if (itemId == R.id.bottom_nav_my_trips) {
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

        TextView expText = findViewById(R.id.user_exp);

        TextView levelText = findViewById(R.id.user_level);
        TextView congratulationsMessage = findViewById(R.id.congratText);
        LottieAnimationView levelAnimation = findViewById(R.id.level_animation);

        ImageButton level_icon = findViewById(R.id.level_icon);

        level_icon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setTitle("Tabla de niveles del viajero");

            ImageView image = new ImageView(ProfileActivity.this);
            image.setImageResource(R.drawable.levels_table);

            builder.setView(image);

            builder.setPositiveButton("Cerrar", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        SharedPreferences prefs = getSharedPreferences("ViajesFinalizados", Context.MODE_PRIVATE);
        int finishedTrips = prefs.getInt("viajes_finalizados", 0);
        int expTotal = prefs.getInt("exp", 0);

        Log.d(TAG, "viajes finalizados: " + finishedTrips);
        Log.d(TAG, "expTotal: " + expTotal);

        expText.setText(String.valueOf(expTotal));


        if (finishedTrips == 0) {
            levelText.setText("0");
            levelAnimation.setAnimation(R.raw.level0);
            levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
            congratulationsMessage.setText("Oh! Aún no has registrado ningun viaje! Anímate y empieza el primero! Completa " + (1  - finishedTrips) + " viaje para subir de nivel");
            Tooltip tooltip = new Tooltip.Builder(level_icon)
                    .setText("Pulse aquí para ver la tabla de niveles del viajero")
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.verde_claro))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))

                    .setGravity(Gravity.BOTTOM)
                    .setCornerRadius(8f)
                    .setDismissOnClick(true)
                    .show();
            // Programar la eliminación del mensaje después de 5 segundos
            new Handler(Looper.getMainLooper()).postDelayed(tooltip::dismiss, 4000);
        }
        else if (finishedTrips < 4) {
            levelText.setText("1");
            levelAnimation.setAnimation(R.raw.level1);
            levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
            congratulationsMessage.setText("Enhorabuena. Eres un viajero EXPLORADOR! Has realizado " + finishedTrips + " viajes!. Completa " + (4 - finishedTrips) + " viajes más para subir de nivel");
        }
        else if (finishedTrips == 4 || finishedTrips > 4 && finishedTrips < 6) {
            levelText.setText("2");
            levelAnimation.setAnimation(R.raw.level2);
            levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
            congratulationsMessage.setText("Enhorabuena. Eres un viajero AVENTURERO! Has realizado " + finishedTrips + " viajes!. Completa " + (6 - finishedTrips) + " viajes más para subir de nivel");
        }
        else if (finishedTrips >= 6 && finishedTrips < 8) {
            levelText.setText("3");
            levelAnimation.setAnimation(R.raw.level3);
            levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
            congratulationsMessage.setText("Enhorabuena. Eres un viajero CONQUISTADOR! Has realizado " + finishedTrips + " viajes!. Completa " + (8 - finishedTrips) + " viajes más para subir de nivel");
        }
        else {
            levelText.setText("4");
            levelAnimation.setAnimation(R.raw.level4);
            levelAnimation.setRepeatCount(LottieDrawable.INFINITE);
            congratulationsMessage.setText("Enhorabuena. Eres un MAESTRO DE RUTAS! Has realizado " + finishedTrips + " viajes!. Sigue así!");
        }


        levelAnimation.playAnimation();
    }

    @Override
    public void onBackPressed() {

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

        // Configurar el ítem seleccionado en BottomNavigationView
        // Cambia esto al ID del ítem correspondiente a la actividad a la que estás volviendo
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_my_trips);
    }


}

