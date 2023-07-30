package com.example.triptrack;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private CardView createTripCard;

    private TextView list_trips_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar la Toolbar (AppBar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // Icono para abrir el drawer
        }
        CardView warningCard = findViewById(R.id.warning_no_trips);
        list_trips_text = findViewById(R.id.list_trips_text);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("viajes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (documents.size() > 0) {
                                // Hay viajes en la colección "viajes"
                                // Mostrar los viajes
                                warningCard.setVisibility(View.GONE);
                                list_trips_text.setVisibility(View.VISIBLE);
                                loadTrips();
                            } else {
                                // No hay viajes en la colección "viajes"
                                // Mostrar el Card "warning_no_trips"
                                warningCard.setVisibility(View.VISIBLE);

                            }
                        } else {
                            // Error al obtener los datos de la colección "viajes"
                        }
                    }
                });


        // Configurar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Obtener el ID del item seleccionado
                        int itemId = item.getItemId();

                        // Realizar acciones basadas en el item seleccionado
                        switch (itemId) {
                            case R.id.bottom_nav_world:
                                // Acción para la pestaña "Buscar"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent searchIntent = new Intent(MainActivity.this, MapamundiActivity.class);
                                startActivity(searchIntent);
                                return true;
                            case R.id.bottom_nav_settings:
                                // Acción para la pestaña "Perfil"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent profileIntent = new Intent(MainActivity.this, ConfigurationActivity.class);
                                startActivity(profileIntent);
                                return true;
                        }

                        return false;
                    }
                });


        // Configurar las Cards para crear un nuevo viaje y consultar un viaje
        createTripCard = findViewById(R.id.create_trip_card);
        ImageView iconImageView = findViewById(R.id.new_trip);
        iconImageView.setImageResource(R.drawable.new_trip);

        createTripCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatingNewTripActivity.class);
            startActivity(intent);
        });

        // Obtener los valores de los campos del Intent
        String destination = getIntent().getStringExtra("destination");
        String departureDate = getIntent().getStringExtra("departureDate");
        String returnDate = getIntent().getStringExtra("returnDate");
        String peopleCount = getIntent().getStringExtra("peopleCount");
        String price = getIntent().getStringExtra("price");
        String tripId = getIntent().getStringExtra("tripId");


        // Agregar el CardView con los datos del viaje
        if (destination != null) {
            addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
        }
    }


    // Abrir o cerrar el drawer cuando se presiona el ícono de la appbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTripCard(String destination, String departureDate, String returnDate, String peopleCount, String price, String tripId) {
        // Crear un nuevo CardView
        CardView tripCard = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, 30, margin, 20);
        tripCard.setLayoutParams(layoutParams);
        tripCard.setRadius(30);
        tripCard.setCardElevation(10);
        tripCard.setCardBackgroundColor(getResources().getColor(R.color.example_color));

        // Agregar el contenido del CardView
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.setPadding(45, 45, 45, 45);
        tripCard.addView(cardContentLayout);

        TextView destinationText = new TextView(this);
        destinationText.setText(destination);
        destinationText.setTextColor(getResources().getColor(R.color.black));
        destinationText.setTextSize(30);
        destinationText.setPadding(0,0,0,50);
        cardContentLayout.addView(destinationText);

        // Crear un LinearLayout con orientación horizontal
        LinearLayout datesLayout = new LinearLayout(this);
        datesLayout.setOrientation(LinearLayout.HORIZONTAL);
        datesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Crear el TextView para la fecha de ida
        TextView departureDateText = new TextView(this);
        departureDateText.setText("Ida: " + departureDate);
        departureDateText.setTextColor(getResources().getColor(R.color.black));
        departureDateText.setTextSize(18);

// Crear el TextView para la fecha de vuelta
        TextView returnDateText = new TextView(this);
        returnDateText.setText("Vuelta: " + returnDate);
        returnDateText.setPadding(30,0,0,0);
        returnDateText.setTextColor(getResources().getColor(R.color.black));
        returnDateText.setTextSize(18);

        // Agregar los TextViews al LinearLayout
        datesLayout.addView(departureDateText);
        datesLayout.addView(returnDateText);

        // Agregar el LinearLayout al CardView
        cardContentLayout.addView(datesLayout);

        TextView peopleCountText = new TextView(this);
        peopleCountText.setText(peopleCount + " Personas");
        peopleCountText.setTextColor(getResources().getColor(R.color.black));
        peopleCountText.setTextSize(18);
        cardContentLayout.addView(peopleCountText);

        TextView priceText = new TextView(this);
        priceText.setText(price + " €");
        priceText.setTextColor(getResources().getColor(R.color.black));
        priceText.setTextSize(18);
        cardContentLayout.addView(priceText);

        // Agregar el CardView al contenedor
        LinearLayout tripsContainer = findViewById(R.id.trips_container);
        tripsContainer.addView(tripCard);

        tripCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes agregar el código para abrir la nueva Activity
                Intent intent = new Intent(MainActivity.this, InfoTripActivity.class);
                // Puedes pasar información a la nueva Activity usando putExtra
                intent.putExtra("destination", destination);
                intent.putExtra("departureDate", departureDate);
                intent.putExtra("returnDate", returnDate);
                intent.putExtra("peopleCount", peopleCount);
                intent.putExtra("price", price);
                intent.putExtra("tripId",tripId);
                startActivity(intent);
            }
        });
    }



        private void loadTrips() {
        // Obtener una referencia a la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener todos los documentos de la colección "viajes"
        db.collection("viajes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Limpiar el contenedor de viajes
                            LinearLayout tripsContainer = findViewById(R.id.trips_container);
                            tripsContainer.removeAllViews();

                            // Recorrer todos los documentos y agregar un CardView para cada uno
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String destination = document.getString("destination");
                                String departureDate = document.getString("departureDate");
                                String returnDate = document.getString("returnDate");
                                String peopleCount = document.getString("peopleCount");
                                String price = document.getString("price");
                                String tripId = document.getString("tripId");

                                addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
                            }
                        } else {
                            // Ocurrió un error al obtener los viajes
                            Toast.makeText(MainActivity.this, "Error al cargar los viajes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
