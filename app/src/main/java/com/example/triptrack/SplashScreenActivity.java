package com.example.triptrack;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);
        generacionAnimaciones(); //llamada al método que inicializará las animaciones de la aplicación.
    }

    private void generacionAnimaciones() {

        //Vinculamos cada una de las animaciones con los movimientos que harán en sus respectivos ficheros XML, dentro de la carpeta "Anim".
        Animation animacion1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        //Vinculamos cada uno de los elementos de la Splash Screen que realizarán la animación con su id del XML
        TextView deTextView = findViewById(R.id.deTextView);
        TextView juanmanuelTextView = findViewById(R.id.juanmanuelTextView);
        ImageView logoImageView = findViewById(R.id.logoImageView);

        //Estos elementos se vinculan con la animación que corresponda
        deTextView.setAnimation(animacion2);
        juanmanuelTextView.setAnimation(animacion2);
        logoImageView.setAnimation(animacion1);

        Glide.with(this)
                .load(R.drawable.triptrack_gif)
                .into((ImageView) findViewById(R.id.logoImageView));


        //Creación de un hilo que pasados 4sg inicializará la siguiente Activity.
        new Handler().postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignIn.getLastSignedInAccount(SplashScreenActivity.this);

            if (user == null) {

                Intent intent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);

            /*Se crea un Array de pares para crear una transición que se solape con la siguiente activity.
            Donde el primer elemento es el objeto que se coge de la splash screen y el segundo es donde se coloca
            en la siguiente activity.
            */
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logoImageView, "logoImageTrans");
                pairs[1] = new Pair<View, String>(juanmanuelTextView, "textTrans");

                //Se realiza una verifiación de la versión ya que versiones anteriores a Lollipop no podría realizar
                //dicho solapamiento.

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this, pairs);
                    startActivity(intent, options.toBundle());

                } else {
                    startActivity(intent);
                    finish();

                }
            }

        }, 4000);
    }
}
