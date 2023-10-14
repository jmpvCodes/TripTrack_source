package com.example.triptrack;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class SignUpActivity extends AppCompatActivity {

    TextView otherUsersText, welcomeLabel, headLabel;
    ImageView signUpImageView;
    TextInputLayout nameText,surnameText, passwordText;
    MaterialButton SignUpButton;
    TextInputEditText nameInputText, surnameInputText, emailEditText, passwordEditText, confirmPasswordEditText;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        otherUsersText.setOnClickListener(v -> transitionBack());

        SignUpButton.setOnClickListener(v -> {
            validate();

        });
        mAuth = FirebaseAuth.getInstance();
    }

    private void register(String email, String password){

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Obtén el usuario recién creado
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            // Crea un UserProfileChangeRequest para establecer el nombre de perfil del usuario
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nameInputText.getText().toString() + " " + surnameInputText.getText().toString())
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
                            userData.put("Email: ",emailEditText.getText().toString());
                            db.collection("users").document(user.getUid()).set(userData);
                        }

                        Intent intent = new Intent (SignUpActivity.this, LoginActivity.class);
                        Toast.makeText(SignUpActivity.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();
                    }

                    else{
                        Toast.makeText(SignUpActivity.this, "Fallo al registrarse", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void validate () {

        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();
        String confirmpassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Correo inválido!");
            return;
        }

        else{
            emailEditText.setError(null);
        }

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

        if (!confirmpassword.equals(password)){
            confirmPasswordEditText.setError("Las contraseñas no coinciden. Compruébalo de nuevo!");
        }

        else{
            confirmPasswordEditText.setError(null);
            register(email,password);
        }
    }

    @Override
    public void onBackPressed() {
        transitionBack();
    }

    private void transitionBack() {

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);

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
