package com.example.triptrack;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Esta clase representa la pantalla de inicio de sesión de la aplicación.
 * Contiene campos de texto para el correo electrónico y la contraseña, así como botones para iniciar sesión y registrarse.
 */
public class LoginActivity extends AppCompatActivity {

    // Declaración de variables de vistas
    TextView otherUsersText, welcomeLabel, headLabel, forgotPassText;
    ImageView logoImageView;
    TextInputLayout nameText, passwordText;
    MaterialButton SignUpButton;
    TextInputEditText emailEditText, passwordEditText;

    // Declaración de variables de autenticación
    private FirebaseAuth mAuth; // Instancia de FirebaseAuth

    /**
     * Método que se llama cuando se crea la actividad.
     * Se inicializan las vistas y se configuran los listeners de los botones.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de vistas
        logoImageView = findViewById(R.id.logoImageView);
        welcomeLabel = findViewById(R.id.welcomeLabel);
        headLabel = findViewById(R.id.headLabel);
        nameText = findViewById(R.id.nameText);
        passwordText = findViewById(R.id.passwordText);
        SignUpButton = findViewById(R.id.SignUpButton);
        otherUsersText = findViewById(R.id.otherUsersText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPassText = findViewById(R.id.forgotPassText);

        // Inicialización de autenticación
        mAuth = FirebaseAuth.getInstance();

        // Configuración de listener para el botón de registro
        otherUsersText.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

            // Transiciones de elementos compartidos
            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<View, String>(logoImageView, "logoImageTrans");
            pairs[1] = new Pair<View, String>(welcomeLabel, "welcomeTrans");
            pairs[2] = new Pair<View, String>(headLabel, "headTextTrans");
            pairs[3] = new Pair<View, String>(nameText, "nameTextTrans");
            pairs[4] = new Pair<View, String>(passwordText, "passwordTextTrans");
            pairs[5] = new Pair<View, String>(SignUpButton, "signUpButtonTrans");
            pairs[6] = new Pair<View, String>(otherUsersText, "otherUsersTrans");

            // Comprueba la versión de Android

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());

        });

        // Configuración de listener para el botón de inicio de sesión
        SignUpButton.setOnClickListener(v -> validate());

        // Configuración de listener para el botón de recuperación de contraseña
        forgotPassText.setOnClickListener(v -> {
            String emailAddress = Objects.requireNonNull(emailEditText.getText()).toString().trim();

            // Comprueba si el correo electrónico está registrado en Firebase
            mAuth.fetchSignInMethodsForEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

                        // Si el correo electrónico no está registrado, se muestra un mensaje de error
                        if (isNewUser) {
                            emailEditText.setError("¡No se ha encontrado un correo electrónico registrado!");
                            Toast.makeText(LoginActivity.this, "¡No se ha encontrado un correo electrónico registrado!", Toast.LENGTH_LONG).show();

                        } else {
                            mAuth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Email enviado", Toast.LENGTH_LONG).show();

                                        }
                                    });
                        }
                    });
        });

    }

    /**
     * Método que valida los campos de correo electrónico y contraseña.
     * Si los campos son válidos, se llama al método startSession().
     */
    private void validate() {

        // Obtención de los valores de los campos de texto
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

        // Validación de los campos de texto
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo inválido!");
            return;
        } else {
            emailEditText.setError(null);
        }
        // Validación de la contraseña
        if (password.isEmpty() || password.length() < 8) {
            passwordEditText.setError("Se necesitan más de 8 caracteres");
            return;
        } else if (!Pattern.compile("[0-9]").matcher(password).find()) {
            passwordEditText.setError("Se necesita al menos un número");
            return;
        } else {
            passwordEditText.setError(null);
        }

        // Llamada al método startSession()
        startSession(email, password);

    }

   /**
    * Método que inicia sesión con Firebase.
    * @param email correo electrónico
    * @param password contraseña
    */
    private void startSession(String email, String password) {

        // Inicio de sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Si la autenticación es exitosa, se llama al método writePrefs() y se inicia la actividad principal.
                    if (task.isSuccessful()) {
                        writePrefs();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Intentalo de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
     * Método que escribe las preferencias de usuario en SharedPreferences.
     * Se obtiene el nombre y apellidos del usuario de Firestore y se guardan en SharedPreferences junto con el correo electrónico.
     */
    private void writePrefs() {

        // Obtención del ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Si el usuario no es nulo, se obtiene el nombre y apellidos del usuario de Firestore y se guardan en SharedPreferences junto con el correo electrónico.
        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String name = documentSnapshot.getString("Nombre: ");
                        String surname = documentSnapshot.getString("Apellidos: ");
                        SharedPreferences prefs = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("Nombre: ", name);
                        editor.putString("Apellidos: ", surname);
                        editor.putString("Email: ", user.getEmail());
                        editor.apply();
                    });

        }

    }

}
