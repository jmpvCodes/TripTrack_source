package com.example.triptrack;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Esta actividad permite al usuario crear un nuevo viaje.
 * La actividad tiene una barra de herramientas en la parte superior y una barra de navegación en la parte inferior.
 * En el medio, hay un formulario para ingresar los datos del viaje.
 */
public class CreatingNewTripActivity extends AppCompatActivity {

    private Calendar departureCalendar;
    private Calendar returnCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_new_trip);
        Button saveTripButton;

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());; //Generación de Toolbar

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
                });

        ImageButton calendarDepartureButton = findViewById(R.id.calendarDeparture);
        calendarDepartureButton.setOnClickListener(v -> showDatePickerDialog(true));

        ImageButton calendarReturnButton = findViewById(R.id.calendarReturn);
        calendarReturnButton.setOnClickListener(v -> showDatePickerDialog(false));

        saveTripButton = findViewById(R.id.save_trip_button);
        saveTripButton.setOnClickListener(v -> new AlertDialog.Builder(CreatingNewTripActivity.this)
                .setTitle("Confirmar viaje")
                .setMessage("¿Desea guardar este viaje?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // El usuario confirmó, guarda los datos del formulario en la colección "viajes"
                    saveTrip();
                })
                .setNegativeButton("No", null)
                .show());
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
        } else {
            // Verificar si la fecha de ida está en el formato correcto
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(departureDate);
            } catch (ParseException e) {
                departureDateEditText.setError("Ingrese una fecha válida en el formato DD/MM/YYYY");
                hasEmptyFields = true;
            }
        }
        if (returnDate.isEmpty()) {
            returnDateEditText.setError("Este campo es obligatorio");
            hasEmptyFields = true;
        } else {
            // Verificar si la fecha de vuelta está en el formato correcto
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(returnDate);
            } catch (ParseException e) {
                returnDateEditText.setError("Ingrese una fecha válida en el formato DD/MM/YYYY");
                hasEmptyFields = true;
            }
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


            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();

                // Agregar un nuevo documento a la colección "viajes" del usuario con los datos del formulario
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(uid).collection("viajes")
                        .add(trip)
                        .addOnSuccessListener(documentReference -> {
                            // El viaje se guardó correctamente
                            Toast.makeText(CreatingNewTripActivity.this, "Viaje guardado", Toast.LENGTH_SHORT).show();

                            // Obtener el ID del documento de Firestore
                            String tripId = documentReference.getId();

                            // Actualizar el documento para agregar el campo tripId
                            Map<String, Object> update = new HashMap<>();
                            update.put("tripId", tripId);
                            documentReference.update(update);

                            //Actualizar el documento para agregar el campo "status"
                            Map<String, Object> update2 = new HashMap<>();
                            update2.put("status", "Activo");
                            documentReference.update(update2);


                            Intent intent = new Intent(CreatingNewTripActivity.this, MainActivity.class);
                            intent.putExtra("destination", destination);
                            intent.putExtra("departureDate", departureCalendar);
                            intent.putExtra("returnDate", returnCalendar);
                            intent.putExtra("peopleCount", peopleCount);
                            intent.putExtra("price", price);

                            // Pasar el ID del viaje a la actividad MainActivity
                            intent.putExtra("tripId", tripId);

                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // Ocurrió un error al guardar el viaje
                            Toast.makeText(CreatingNewTripActivity.this, "Error al guardar el viaje", Toast.LENGTH_SHORT).show();
                        });
            }

        }

    private void showDatePickerDialog(final boolean isDeparture) {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();

        // Crear un DatePickerDialog y mostrarlo
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Crear un objeto Calendar con la fecha seleccionada
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);

            // Guardar la fecha seleccionada en la variable correspondiente
            if (isDeparture) {
                departureCalendar = selectedDate;

                // Mostrar la fecha seleccionada en el campo de texto correspondiente
                EditText departureDateEditText = findViewById(R.id.departure_date_edit_text);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                departureDateEditText.setText(dateFormat.format(departureCalendar.getTime()));
            } else {
                returnCalendar = selectedDate;

                // Mostrar la fecha seleccionada en el campo de texto correspondiente
                EditText returnDateEditText = findViewById(R.id.return_date_edit_text);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                returnDateEditText.setText(dateFormat.format(returnCalendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


}