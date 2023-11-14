package com.example.triptrack;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendIncidents extends AppCompatActivity {

    private String firstName;
    private String lastName;
    private String email;
    private String incidentType;
    private String problemDescription;
    private String problemFrequency;

    private Button sendButton, attachButton;

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
        firstName = findViewById(R.id.edittext_first_name).toString();
        lastName = findViewById(R.id.edittext_last_name).toString();
        email = findViewById(R.id.edittext_email).toString();
        incidentType = findViewById(R.id.spinner_incident_type).toString();
        problemDescription = findViewById(R.id.edittext_problem_description).toString();
        problemFrequency = findViewById(R.id.spinner_problem_frequency).toString();
        sendButton = findViewById(R.id.button_submit);
        attachButton = findViewById(R.id.button_attach_screenshot);

        sendButton.setOnClickListener(v -> sendIncidentReport(firstName, lastName, email, incidentType, problemDescription, problemFrequency));
        attachButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }



    public void sendIncidentReport(String firstName, String lastName, String email, String incidentType, String problemDescription, String problemFrequency) {
        // Comprueba si todos los campos están rellenos y si el correo electrónico es válido
        if (!areFieldsFilled(firstName, lastName, email, incidentType, problemDescription, problemFrequency)) {
            System.out.println("Por favor, rellena todos los campos.");
            return;
        }

        if (!isValidEmail(email)) {
            System.out.println("Por favor, introduce un correo electrónico válido.");
            return;
        }

        // Tu dirección de correo electrónico
        String to = "juanmanuel_perniavalencia@outlook.es";

        // El servidor SMTP de tu correo electrónico
        String host = "smtp.outlook.com";

        // Obtén las propiedades del sistema
        Properties properties = System.getProperties();

        // Configura el servidor de correo
        properties.setProperty("mail.smtp.host", host);

        // Obtiene el objeto Session por defecto
        Session session = Session.getDefaultInstance(properties);

        try {
            // Crea un objeto MimeMessage por defecto
            MimeMessage message = new MimeMessage(session);

            // Establece el remitente
            message.setFrom(new InternetAddress(email));

            // Establece el destinatario
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Establece el asunto
            message.setSubject("Informe de incidencia de " + firstName + " " + lastName);

            // Establece el contenido del mensaje
            message.setText("Tipo de incidencia: " + incidentType + "\n" +
                    "Descripción del problema: " + problemDescription + "\n" +
                    "Frecuencia del problema: " + problemFrequency);

            // Envía el mensaje
            Transport.send(message);
            System.out.println("Mensaje enviado correctamente");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public boolean areFieldsFilled(String firstName, String lastName, String email, String incidentType, String problemDescription, String problemFrequency) {
        return !(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || incidentType.isEmpty() || problemDescription.isEmpty() || problemFrequency.isEmpty());
    }


}