package com.example.triptrack;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta actividad muestra una lista de viajes finalizados.
 * La actividad tiene una barra de herramientas en la parte superior y una barra de navegación en la parte inferior.
 * En el medio, hay un contenedor que muestra un CardView para cada viaje.
 */
public class FinishedTripsActivity extends AppCompatActivity {

    private String tripId;

    private CardView warningCard;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_trips);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        warningCard = findViewById(R.id.warning_no_trips);
        warningCard.setVisibility(View.GONE);

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        verifyTripsFinished();

        IntentFilter filter = new IntentFilter("com.example.triptrack.TRIP_DATA");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Recuperar los datos del viaje del intent
                intent.getStringExtra("destination");
                intent.getStringExtra("departureDate");
                intent.getStringExtra("returnDate");
                intent.getIntExtra("peopleCount", 0);
                intent.getDoubleExtra("price", 0);
                intent.getStringExtra("tripId");

                // TODO: Agregar código para procesar los datos del viaje
            }
        };
        registerReceiver(receiver, filter);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Obtener el ID del item seleccionado
            int itemId = item.getItemId();

            // Realizar acciones basadas en el item seleccionado
            if (itemId == R.id.bottom_nav_my_trips) {
                // Acción para la pestaña "Buscar"
                // Ejemplo: iniciar la actividad correspondiente
                Intent mainIntent = new Intent(FinishedTripsActivity.this, MainActivity.class);
                startActivity(mainIntent);
                return true;
            } else if (itemId == R.id.bottom_nav_world) {
                // Acción para la pestaña "Buscar"
                // Ejemplo: iniciar la actividad correspondiente
                Intent searchIntent = new Intent(FinishedTripsActivity.this, MapamundiActivity.class);
                startActivity(searchIntent);
                return true;
            } else if (itemId == R.id.bottom_nav_profile) {
                // Acción para la pestaña "Perfil"
                // Ejemplo: iniciar la actividad correspondiente
                Intent profileIntent = new Intent(FinishedTripsActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
                return true;
            }

            return false;
        });

    }

    private void loadTrips() {
        // Obtener una referencia a la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Obtener todos los documentos de la colección "viajes" que tienen el atributo "status" con valor "finalizado"
        db.collection("users").document(uid).collection("viajes")
                .whereEqualTo("status", "finalizado")
                .get()
                .addOnCompleteListener(task -> {
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
                            tripId = document.getString("tripId");


                            addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
                        }
                    } else {
                        // Ocurrió un error al obtener los viajes
                        Toast.makeText(FinishedTripsActivity.this, "Error al cargar los viajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addTripCard(String destination, String departureDate, String returnDate, String peopleCount, String price, String tripId) {
        // Crear un nuevo CardView
        CardView tripCard = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, margin, margin, margin);
        tripCard.setLayoutParams(layoutParams);
        tripCard.setRadius(30);
        tripCard.setCardElevation(10);
        tripCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Agregar el contenido del CardView
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.setPadding(45, 45, 45, 45);
        tripCard.addView(cardContentLayout);

        TextView destinationText = new TextView(this);
        destinationText.setText(destination);
        destinationText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        destinationText.setTextSize(30);
        destinationText.setPadding(0, 0, 0, 50);
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
        departureDateText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        departureDateText.setTextSize(15);

// Crear el TextView para la fecha de vuelta
        TextView returnDateText = new TextView(this);
        returnDateText.setText("Vuelta: " + returnDate);
        returnDateText.setPadding(30, 0, 0, 0);
        returnDateText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        returnDateText.setTextSize(15);

        // Agregar los TextViews al LinearLayout
        datesLayout.addView(departureDateText);
        datesLayout.addView(returnDateText);

        // Agregar el LinearLayout al CardView
        cardContentLayout.addView(datesLayout);

        TextView peopleCountText = new TextView(this);
        peopleCountText.setText(peopleCount + " Personas");
        peopleCountText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        peopleCountText.setTextSize(15);
        cardContentLayout.addView(peopleCountText);

        TextView priceText = new TextView(this);
        priceText.setText(price + " €");
        priceText.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        priceText.setTextSize(15);
        cardContentLayout.addView(priceText);

        // Agregar el CardView al contenedor
        LinearLayout tripsContainer = findViewById(R.id.trips_container);
        tripsContainer.addView(tripCard);

        // Crear un LinearLayout con orientación horizontal
        LinearLayout iconsLayout = new LinearLayout(this);
        iconsLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        iconsLayout.setGravity(Gravity.END);


        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());


        // Crear el ImageView para el icono de eliminar
        ImageView deleteIcon = new ImageView(this);
        deleteIcon.setImageResource(R.drawable.delete_icon);
        deleteIcon.setBackgroundColor(Color.TRANSPARENT);
        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        deleteIcon.setPadding(10, 10, 10, 10);
        iconsLayout.addView(deleteIcon);
        Log.d(TAG, "tripId: " + tripId);

        // Agregar un OnClickListener al icono de eliminar
        deleteIcon.setOnClickListener(v -> {
            // Mostrar un AlertDialog para confirmar la eliminación del viaje
            new AlertDialog.Builder(FinishedTripsActivity.this)
                    .setTitle("Eliminar viaje")
                    .setMessage("¿Está seguro de eliminar este viaje?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        // Eliminar el viaje de la base de datos de Firebase
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                        db.collection("users").document(uid).collection("viajes").document(tripId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {

                                    // Eliminar el CardView correspondiente al viaje de la pantalla principal
                                    @SuppressLint("CutPasteId") LinearLayout tripsContainer1 = findViewById(R.id.trips_container);
                                    tripsContainer1.removeView(tripCard);
                                })
                                .addOnFailureListener(e -> {
                                    // Ocurrió un error al intentar eliminar el viaje de la base de datos
                                    Toast.makeText(FinishedTripsActivity.this, "Error al eliminar el viaje", Toast.LENGTH_SHORT).show();
                                });
                        verifyTripsFinished();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
        // Agregar el LinearLayout al CardView
        cardContentLayout.addView(iconsLayout);

    }
    private void verifyTripsFinished (){
        db.collection("users").document(uid).collection("viajes")
                .whereEqualTo("status", "finalizado")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Hay viajes en la colección "viajes" con el atributo "status" con valor "finalizado"
                            warningCard.setVisibility(View.GONE);
                            loadTrips();
                        } else {
                            // No hay viajes en la colección "viajes" con el atributo "status" con valor "finalizado"
                            warningCard.setVisibility(View.VISIBLE);
                        }
                    }  // Error al obtener los datos de la colección "viajes"

                });
    }
}
