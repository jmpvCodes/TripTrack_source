package com.example.triptrack;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


public class SendIncidentsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_incidents);
        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());//Generación de Toolbar

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

        // Recoge los datos del formulario
        EditText firstNameEditText = findViewById(R.id.edittext_first_name);
        EditText lastNameEditText = findViewById(R.id.edittext_last_name);
        EditText emailEditText = findViewById(R.id.edittext_email);
        Spinner incidentTypeSpinner = findViewById(R.id.spinner_incident_type);
        EditText problemDescriptionEditText = findViewById(R.id.edittext_problem_description);
        Spinner problemFrequencySpinner = findViewById(R.id.spinner_problem_frequency);
        Button sendButton = findViewById(R.id.button_submit);

        sendButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String incidentType = incidentTypeSpinner.getSelectedItem().toString().trim();
            String problemDescription = problemDescriptionEditText.getText().toString().trim();
            String problemFrequency = problemFrequencySpinner.getSelectedItem().toString().trim();

            sendIncidentReport(firstName, lastName, email, incidentType, problemDescription, problemFrequency);
        });

    }

    public void sendIncidentReport(String firstName, String lastName, String email, String incidentType, String problemDescription, String problemFrequency) {

        // Crear un objeto Map con los datos del formulario
        Map<String, Object> incident = new HashMap<>();
        incident.put("firstName", firstName);
        incident.put("lastName", lastName);
        incident.put("email", email);
        incident.put("incidentType", incidentType);
        incident.put("problemDescription", problemDescription);
        incident.put("problemFrequency", problemFrequency);
        incident.put("status", "Abierta");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Comprueba si todos los campos están rellenos y si el correo electrónico es válido
            if (!areFieldsFilled(firstName, lastName, email, incidentType, problemDescription, problemFrequency)) {
                System.out.println("Por favor, rellena todos los campos.");
                Toast.makeText(this, "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                System.out.println("Por favor, introduce un correo electrónico válido.");
                Toast.makeText(this, "Por favor, introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Agregar un nuevo documento a la colección "incidencias" del usuario con los datos del formulario
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).collection("incidencias")
                    .add(incident)
                    .addOnSuccessListener(documentReference -> {
                        // La incidencia se guardó correctamente
                        Toast.makeText(SendIncidentsActivity.this, "Incidencia guardada", Toast.LENGTH_SHORT).show();

                        // Obtener el ID del documento de Firestore
                        String incidentId = documentReference.getId();

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    });
        }
    }

    public boolean areFieldsFilled(String firstName, String lastName, String email, String incidentType, String problemDescription, String problemFrequency) {
        return !(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || incidentType.isEmpty() || problemDescription.isEmpty() || problemFrequency.isEmpty());
    }

}

