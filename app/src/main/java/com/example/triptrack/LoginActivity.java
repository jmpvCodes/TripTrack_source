package com.example.triptrack;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    TextView otherUsersText, welcomeLabel, headLabel, forgotPassText;
    ImageView logoImageView;
    TextInputLayout nameText, passwordText;
    MaterialButton SignUpButton;
    TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    public static final int RC_SIGN_IN = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        mAuth = FirebaseAuth.getInstance();

        otherUsersText.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);

            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<View, String>(logoImageView, "logoImageTrans");
            pairs[1] = new Pair<View, String>(welcomeLabel, "welcomeTrans");
            pairs[2] = new Pair<View, String>(headLabel, "headTextTrans");
            pairs[3] = new Pair<View, String>(nameText, "nameTextTrans");
            pairs[4] = new Pair<View, String>(passwordText, "passwordTextTrans");
            pairs[5] = new Pair<View, String>(SignUpButton, "signUpButtonTrans");
            pairs[6] = new Pair<View, String>(otherUsersText, "otherUsersTrans");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, options.toBundle());

            } else {

                startActivity(intent);
                finish();
            }
        });
        SignUpButton.setOnClickListener(v -> validate());
        forgotPassText.setOnClickListener(v -> {
            String emailAddress = emailEditText.getText().toString().trim();

            mAuth.fetchSignInMethodsForEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            emailEditText.setError("¡No se ha encontrado un correo electrónico registrado!");
                            Toast.makeText(LoginActivity.this, "¡No se ha encontrado un correo electrónico registrado!", Toast.LENGTH_LONG).show();

                        } else {
                            mAuth.sendPasswordResetEmail(emailAddress)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Email enviado", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                        }
                    });
        });

    }

    private void validate(){

        String email = Objects.requireNonNull(emailEditText.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString().trim();

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

        startSession(email,password);

    }

    private void startSession(String email, String password){

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        writePrefs();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    else{
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Intentalo de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void writePrefs(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            SharedPreferences prefs = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Nombre: ", user.getDisplayName());
            editor.putString("Apellidos: ", user.getDisplayName());
            editor.putString("Email: ", user.getEmail());
            editor.apply();
        }
    }



}
