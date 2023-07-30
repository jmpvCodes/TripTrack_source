package com.example.triptrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreatingNewTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_new_trip);
        Button saveTripButton;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Obtener el ID del item seleccionado
                        int itemId = item.getItemId();

                        // Realizar acciones basadas en el item seleccionado
                        switch (itemId) {
                            case R.id.nav_my_trips:
                                // Acción para la pestaña "Buscar"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent mainIntent = new Intent(CreatingNewTripActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                return true;
                            case R.id.bottom_nav_world:
                                // Acción para la pestaña "Buscar"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent searchIntent = new Intent(CreatingNewTripActivity.this, MapamundiActivity.class);
                                startActivity(searchIntent);
                                return true;
                            case R.id.bottom_nav_settings:
                                // Acción para la pestaña "Perfil"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent profileIntent = new Intent(CreatingNewTripActivity.this, ConfigurationActivity.class);
                                startActivity(profileIntent);
                                return true;
                        }

                        return false;
                    }
                });


        saveTripButton = findViewById(R.id.save_trip_button);
        saveTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(CreatingNewTripActivity.this)
                        .setTitle("Confirmar viaje")
                        .setMessage("¿Desea guardar este viaje?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // El usuario confirmó, guarda los datos del formulario en la colección "viajes"
                                saveTrip();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void saveTrip() {
        EditText destinationEditText = findViewById(R.id.destination_edit_text);
        EditText departureDateEditText = findViewById(R.id.departure_date_edit_text);
        EditText returnDateEditText = findViewById(R.id.return_date_edit_text);
        EditText peopleCountEditText = findViewById(R.id.people_count_edit_text);
        EditText priceEditText = findViewById(R.id.price_edit_text);

        // Obtener los valores de los campos del formulario
        String destination = destinationEditText.getText().toString();
        String departureDate = departureDateEditText.getText().toString();
        String returnDate = returnDateEditText.getText().toString();
        String peopleCount = peopleCountEditText.getText().toString();
        String price = priceEditText.getText().toString();

        // Verificar si alguno de los campos está vacío
        boolean hasEmptyFields = false;
        if (destination.isEmpty()) {
            destinationEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        }
        if (departureDate.isEmpty()) {
            departureDateEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        }
        if (returnDate.isEmpty()) {
            returnDateEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        }
        if (peopleCount.isEmpty()) {
            peopleCountEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        } else if (!peopleCount.matches("\\d+")) {
            peopleCountEditText.setError("Ingrese un valor numérico válido");
            hasEmptyFields = true;
        }

        if (price.isEmpty()) {
            priceEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        } else if (!price.matches("\\d+")) {
            priceEditText.setError("Ingrese un valor numérico válido");
            hasEmptyFields = true;
        }


        if (hasEmptyFields) {
            // Mostrar un mensaje al usuario
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto Map con los datos del formulario
        Map<String, Object> trip = new HashMap<>();
        trip.put("destination", destination);
        trip.put("departureDate", departureDate);
        trip.put("returnDate", returnDate);
        trip.put("peopleCount", peopleCount);
        trip.put("price", price);

        // Agregar un nuevo documento a la colección "viajes" con los datos del formulario
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("viajes")
                .add(trip)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // El viaje se guardó correctamente
                        Toast.makeText(CreatingNewTripActivity.this, "Viaje guardado", Toast.LENGTH_SHORT).show();

                        // Obtener el ID del documento de Firestore
                        String tripId = documentReference.getId();

                        // Actualizar el documento para agregar el campo tripId
                        Map<String, Object> update = new HashMap<>();
                        update.put("tripId", tripId);
                        documentReference.update(update);

                        Intent intent = new Intent(CreatingNewTripActivity.this, MainActivity.class);
                        intent.putExtra("destination", destination);
                        intent.putExtra("departureDate", departureDate);
                        intent.putExtra("returnDate", returnDate);
                        intent.putExtra("peopleCount", peopleCount);
                        intent.putExtra("price", price);

                        // Pasar el ID del viaje a la actividad MainActivity
                        intent.putExtra("tripId", tripId);

                        startActivity(intent);
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al guardar el viaje
                        Toast.makeText(CreatingNewTripActivity.this, "Error al guardar el viaje", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    }