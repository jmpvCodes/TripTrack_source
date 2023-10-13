package com.example.triptrack;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.FileNotFoundException;
import java.util.Objects;

public class InfoTripActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_trip);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

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

        // En el método onClick del botón import_photo_button
        ImageButton importPhotoButton = findViewById(R.id.import_photo_button);
        importPhotoButton.setOnClickListener(v -> {
            Intent intent15 = new Intent(Intent.ACTION_PICK);
            intent15.setType("image/*");
            startActivityForResult(intent15, PICK_IMAGE_REQUEST);
        });

        CardView importDocumentsCard = findViewById(R.id.import_documents);
        importDocumentsCard.setOnClickListener(v -> {
            Intent intent1 = new Intent(InfoTripActivity.this, ImportDocumentsActivity.class);
            // Iniciar la actividad ImportDocumentsActivity y pasar solo el campo tripId
            intent1.putExtra("tripId", tripId);
            startActivity(intent1);
        });

        CardView expenseCalculationCard = findViewById(R.id.expense_calculation);
        expenseCalculationCard.setOnClickListener(v -> {
            Intent intent12 = new Intent(InfoTripActivity.this, ExpenseCalculationActivity.class);
            intent12.putExtra("tripId", tripId);
            startActivity(intent12);
        });

        CardView travelAgendaCard = findViewById(R.id.travel_agenda);
        travelAgendaCard.setOnClickListener(v -> {
            Intent intent13 = new Intent(InfoTripActivity.this, TravelAgendaActivity.class);
            intent13.putExtra("tripId", tripId);
            startActivity(intent13);
        });

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
                    } else if (itemId == R.id.bottom_nav_my_trips) {
                        // Acción para la pestaña "Perfil"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent profileIntent = new Intent(this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }

                    return false;
                });

    }

    // Sobrescribe el método onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            // Carga la imagen seleccionada como fondo del CardView
            CardView cardView = findViewById(R.id.card_view);
            try {
                cardView.setBackground(Drawable.createFromStream(getContentResolver().openInputStream(selectedImageUri), selectedImageUri.toString()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Cambia el color de fondo de la vista View a #80000000
            View view = findViewById(R.id.view);
            view.setBackgroundColor(Color.parseColor("#80000000"));

        }
    }

}
