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

/**
 * Esta clase es responsable de mostrar una pantalla de bienvenida con animaciones y luego lanzar la actividad apropiada
 * basándose en si el usuario ya ha iniciado sesión o no.
 * @author Juan Manuel Pernía Valencia
 * @version 1.0
 *
 */


public class SplashScreenActivity extends AppCompatActivity {

    /**
     * Método que se llama cuando se crea la actividad. Establece la vista de contenido en el diseño de la actividad
     * splashscreen_activity y luego llama al método generacionAnimaciones para inicializar las animaciones.
     *
     * @param savedInstanceState el estado anterior de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_activity);
        generacionAnimaciones(); //llamada al método que inicializará las animaciones de la aplicación.
    }

    /**
     * Método que inicializa las animaciones para la pantalla de bienvenida. Carga dos animaciones desde archivos XML
     * en la carpeta "Anim" y las asocia con las vistas apropiadas en el diseño. También carga una imagen usando Glide
     * y la establece como origen para un ImageView.
     *
     * Después de inicializar las animaciones e imagen, el método crea un nuevo objeto Handler y lo usa para publicar
     * una acción retrasada que lanzará la actividad apropiada después de 4 segundos. Si el usuario no ha iniciado sesión,
     * se lanza la actividad SignUpActivity. De lo contrario, se lanza LoginActivity con una animación de transición de
     * elemento compartido.
     */
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

        //Carga de la imagen usando Glide y establecimiento de la imagen como origen para un ImageView.
        Glide.with(this)
                .load(R.drawable.triptrack_gif)
                .into((ImageView) findViewById(R.id.logoImageView));

        //Creación de un hilo que pasados 4sg inicializará la siguiente Activity.
        new Handler().postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            GoogleSignIn.getLastSignedInAccount(SplashScreenActivity.this);

            //Si el usuario no ha iniciado sesión, se lanza la actividad SignUpActivity.
            if (user == null) {

                Intent intent = new Intent(SplashScreenActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();

            } else {
                //De lo contrario, se lanza LoginActivity con una animación de transición de elemento compartido.
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