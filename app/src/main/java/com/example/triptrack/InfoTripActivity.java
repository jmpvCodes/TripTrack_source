package com.example.triptrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

/**
 * Esta clase representa la pantalla de información del viaje.
 * Contiene información sobre el viaje, como el destino, las fechas de salida y regreso, el número de personas y el precio.
 * También contiene botones para acceder a las diferentes funcionalidades de la aplicación.
 */

public class InfoTripActivity extends AppCompatActivity {

    // Código de solicitud para la selección de imágenes

    /**
     * Método que se llama cuando se crea la actividad.
     * @param savedInstanceState estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_trip);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        // Obtener los datos del intent
        Intent intent = getIntent();
        String destination = intent.getStringExtra("destination");
        String departureDate = intent.getStringExtra("departureDate");
        String returnDate = intent.getStringExtra("returnDate");
        String peopleCount = intent.getStringExtra("peopleCount");
        String price = intent.getStringExtra("price");
        String tripId = getIntent().getStringExtra("tripId");


        // Obtener una referencia al CardView
        findViewById(R.id.card_view);

        // Obtener las referencias a los TextViews
        TextView destinationText = findViewById(R.id.destination_text);
        destinationText.setText(destination);

        TextView departureDateText = findViewById(R.id.departure_date_text);
        departureDateText.setText("Ida: " + departureDate);

        TextView returnDateText = findViewById(R.id.return_date_text);
        returnDateText.setText("Vuelta: " + returnDate);

        TextView peopleCountText = findViewById(R.id.people_count_text);
        peopleCountText.setText(peopleCount + " Personas");

        TextView priceText = findViewById(R.id.price_text);
        priceText.setText(price + " €");

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
