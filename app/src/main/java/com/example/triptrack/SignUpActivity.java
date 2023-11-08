package com.example.triptrack;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Clase que maneja la actividad de registro de usuario.
 * Permite al usuario ingresar su nombre, apellido, correo electrónico y contraseña para crear una cuenta.
 * La clase utiliza Firebase Authentication para registrar al usuario y Firestore para almacenar su información.
 * La clase también realiza validaciones en los campos de entrada antes de registrar al usuario.
 */
public class SignUpActivity extends AppCompatActivity {

    // Declaración de variables

    TextView otherUsersText, welcomeLabel, headLabel;
    ImageView signUpImageView;
    TextInputLayout nameText,surnameText, passwordText;
    MaterialButton SignUpButton;
    TextInputEditText nameInputText, surnameInputText, emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth mAuth; // Instancia de FirebaseAuth

    /**
     * Método que se ejecuta cuando se crea la actividad
     * @param savedInstanceState estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inicialización de variables
        signUpImageView = findViewById(R.id.signUpImageView);
        welcomeLabel = findViewById(R.id.welcomeLabel);
        headLabel = findViewById(R.id.headLabel);
        nameText = findViewById(R.id.nameText);
        nameInputText = findViewById(R.id.nameInputText);
        surnameText =findViewById(R.id.surnameText);
        surnameInputText=findViewById(R.id.surnameInputText);
        passwordText = findViewById(R.id.passwordText);
        SignUpButton = findViewById(R.id.SignUpButton);
        otherUsersText = findViewById(R.id.otherUsersText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Transición de elementos
        otherUsersText.setOnClickListener(v -> transitionBack());

        // Registro de usuario
        SignUpButton.setOnClickListener(v -> validate());
        // Inicialización de Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Método para registrar al usuario en Firebase Authentication y guardar su información en Firestore
     * @param email correo electrónico del usuario
     * @param password contraseña del usuario
     */
    private void register(String email, String password){

        // Crea un nuevo usuario con el correo electrónico y la contraseña proporcionados
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Obtén el usuario recién creado
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Crea un UserProfileChangeRequest para establecer el nombre de perfil del usuario
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(Objects.requireNonNull(nameInputText.getText()) + " " + Objects.requireNonNull(surnameInputText.getText()))
                                    .build();

                            // Actualiza el perfil del usuario
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "Nombre de usuario actualizado.");
                                        }
                                    });

                            // Guarda el nombre y apellido del usuario en Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("Nombre: ", nameInputText.getText().toString());
                            userData.put("Apellidos: ", surnameInputText.getText().toString());
                            userData.put("Email: ", Objects.requireNonNull(emailEditText.getText()).toString());
                            db.collection("users").document(user.getUid()).set(userData);
                        }

                        // Inicia la actividad de inicio de sesión
                        Intent intent = new Intent (SignUpActivity.this, LoginActivity.class);
                        Toast.makeText(SignUpActivity.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();
                    }

                    // Si falla el registro, muestra un mensaje de error
                    else{
                        Toast.makeText(SignUpActivity.this, "Fallo al registrarse", Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
        * Método para validar los campos de entrada
        * Verifica que el correo electrónico sea válido, que la contraseña tenga más de 8 caracteres y que la confirmación de la contraseña coincida con la contraseña
        */

    private void validate () {

        // Obtiene el texto de los campos de entrada
        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        String confirmpassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();

        // Valida los campos de entrada
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo inválido!");
            return;
        }

        else{
            emailEditText.setError(null);
        }
        // Valida la contraseña
        if (password.isEmpty() || password.length() < 8){
            passwordEditText.setError("Se necesitan más de 8 caracteres");
            return;
        }

        else if (!Pattern.compile("[0-9]").matcher(password).find()){
            passwordEditText.setError("Se necesita al menos un número");
            return;
        }

        else {
            passwordEditText.setError(null);
        }

        // Valida la confirmación de la contraseña
        if (!confirmpassword.equals(password)){
            confirmPasswordEditText.setError("Las contraseñas no coinciden. Compruébalo de nuevo!");
        }

        else{
            confirmPasswordEditText.setError(null);
            register(email,password);
        }
    }

    /**
     * Método para volver a la actividad de inicio de sesión cuando se presiona el botón de retroceso
     */

    @Override
    public void onBackPressed() {
        transitionBack();
    }

    /**
     * Método para realizar la transición de elementos a la actividad de inicio de sesión
    */    

    private void transitionBack() {

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);

        // Transición de elementos
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(signUpImageView, "logoImageTrans");
        pairs[1] = new Pair<View, String>(welcomeLabel, "welcomeTrans");
        pairs[2] = new Pair<View, String>(headLabel, "headTextTrans");
        pairs[3] = new Pair<View, String>(nameText, "nameTextTrans");
        pairs[4] = new Pair<View, String>(passwordText, "passwordTextTrans");
        pairs[5] = new Pair<View, String>(SignUpButton, "signUpButtonTrans");
        pairs[6] = new Pair<View, String>(otherUsersText, "otherUsersTrans");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, pairs);
        startActivity(intent, options.toBundle());

    }

}
