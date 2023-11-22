package com.example.triptrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

/**
 * Esta clase representa la pantalla de información del viaje.
 * Contiene información sobre el viaje, como el destino, las fechas de salida y regreso, el número de personas y el precio.
 * También contiene botones para acceder a las diferentes funcionalidades de la aplicación.
 */

public class InfoTripActivity extends AppCompatActivity {

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Código de solicitud para la selección de imágenes

    /**
     * Método que se llama cuando se crea la actividad.
     * @param savedInstanceState estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_trip);

        TextView destinationText = findViewById(R.id.destination_text);
        TextView departureDateText = findViewById(R.id.departure_date_text);
        TextView returnDateText = findViewById(R.id.return_date_text);
        TextView peopleCountText = findViewById(R.id.people_count_text);
        TextView priceText = findViewById(R.id.price_text);


        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Obtener los datos del intent

        String tripId = getIntent().getStringExtra("tripId");

        db.collection("users").document(uid).collection("viajes")
                .whereEqualTo("tripId", tripId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Recorrer todos los documentos y agregar un CardView para cada uno
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String destination = document.getString("destination");
                                String departureDate = document.getString("departureDate");
                                String returnDate = document.getString("returnDate");
                                String peopleCount = document.getString("peopleCount");
                                String price = document.getString("price");
                            // Hay viajes en la colección "viajes" que no tienen el atributo "status" con valor "finalizado"
                            destinationText.setText(destination);
                            departureDateText.setText("Ida: " + departureDate);
                            returnDateText.setText("Vuelta: " + returnDate);
                            peopleCountText.setText(peopleCount + " Personas");
                            priceText.setText(price + " €");
                            }

                        }
                    }  // Error al obtener los datos de la colección "viajes"
                });

        // Obtener una referencia al CardView
        findViewById(R.id.card_view);

        // Funcionalidad de importar documentos
        CardView importDocumentsCard = findViewById(R.id.import_documents);
        importDocumentsCard.setOnClickListener(v -> {
            Intent intent1 = new Intent(InfoTripActivity.this, ImportDocumentsActivity.class);
            intent1.putExtra("tripId", tripId);
            startActivity(intent1);
        });

        // Funcionalidad de calculo de gastos

        CardView expenseCalculationCard = findViewById(R.id.expense_calculation);
        expenseCalculationCard.setOnClickListener(v -> {
            Intent intent12 = new Intent(InfoTripActivity.this, ExpenseCalculationActivity.class);
            intent12.putExtra("tripId", tripId);
            startActivity(intent12);
        });

        // Funcionalidad de agenda de viaje
        
        CardView travelAgendaCard = findViewById(R.id.travel_agenda);
        travelAgendaCard.setOnClickListener(v -> {
            Intent intent13 = new Intent(InfoTripActivity.this, TravelAgendaActivity.class);
            intent13.putExtra("tripId", tripId);
            startActivity(intent13);
        });

        // Funcionalidad de álbum de fotos
        
        CardView AlbumPhotosCard = findViewById(R.id.album_photos);
        AlbumPhotosCard.setOnClickListener(v -> {
            Intent intent14 = new Intent(InfoTripActivity.this, AlbumPhotoActivity.class);
            intent14.putExtra("tripId", tripId);
            startActivity(intent14);
        });

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
                }); // Generar la barra de navegación inferior

    }

}
