package com.example.triptrack;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView otherUsersText, welcomeLabel, headLabel;
    ImageView logoImageView;
    TextInputLayout nameText, passwordText;
    MaterialButton SignUpButton;
    TextInputEditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    //

    SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
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

        signInButton = findViewById(R.id.loginGoogle);

        signInButton.setOnClickListener(v -> SignInWithGoogle());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

    }

    private void SignInWithGoogle(){

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getId());
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this,"Fallo en el inicio de sesión con Google",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){

        AuthCredential credencial = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credencial)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Fallo en iniciar la sesión. Inténtelo de nuevo.", Toast.LENGTH_LONG).show();
                    }
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
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    else{
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas. Intentalo de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });

    }



}
