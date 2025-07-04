package com.example.triptrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Objects;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta clase representa la pantalla principal de la aplicación.
 */
public class MainActivity extends AppCompatActivity {

    // Declaración de variables
    private DrawerLayout drawerLayout; 
    private TextView list_trips_text;

    private CardView warningCard;

    private int count = 0;

    private String uid;

    private String destination, departureDate, returnDate, peopleCount, price;

    // Obtener una referencia a la base de datos
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Método que se ejecuta cuando se crea la actividad
     * @param savedInstanceState estado de la instancia guardada
     */
    @SuppressLint({"NonConstantResourceId", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Configurar la Toolbar (AppBar)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu); // Icono para abrir el drawer
        }

        // Ocultar el título de la actividad
        warningCard = findViewById(R.id.warning_no_trips);
        warningCard.setVisibility(View.GONE);

        // Ocultar el texto "No hay viajes"
        list_trips_text = findViewById(R.id.list_trips_text);



        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Verificar si el usuario ha iniciado sesión
        if (user != null) {
            uid = user.getUid();

            verifyTrips();

            // Obtener todos los documentos de la colección "viajes" que tienen el atributo "status" con valor "finalizado"
            db.collection("users").document(uid).collection("viajes")
                    .whereEqualTo("status", "finalizado")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            count = documents.size();  // Contar los viajes finalizados

                            SharedPreferences prefs = getSharedPreferences("ViajesFinalizados", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("viajes_finalizados", count);
                            editor.apply();

                        }
                    });

        }


        // Configurar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar el NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        // Cambiar el color del texto de la opción "Cerrar sesión"
        MenuItem menuItem = menu.findItem(R.id.sign_out);
        SpannableString s = new SpannableString(menuItem.getTitle()); 
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0); 
        menuItem.setTitle(s);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Cerrar el DrawerLayout al seleccionar una opción
            DrawerLayout drawer = findViewById(R.id.drawer_layout); 
            drawer.closeDrawer(GravityCompat.START);

            // Manejar la selección de opciones del menú
            switch (item.getItemId()) {
                case R.id.nav_my_trips:
                    // Iniciar la actividad FinishedTripsActivity al seleccionar la opción "Viajes finalizados"
                    Intent intent = new Intent(MainActivity.this, FinishedTripsActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.nav_my_world:
                    // Iniciar la actividad FinishedTripsActivity al seleccionar la opción "Viajes finalizados"
                    Intent intentMapamundi = new Intent(MainActivity.this, MapamundiActivity.class);
                    startActivity(intentMapamundi);
                    return true;
                case R.id.nav_profile:
                    // Iniciar la actividad FinishedTripsActivity al seleccionar la opción "Viajes finalizados"
                    Intent intentSettings = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intentSettings);
                    return true;

                case R.id.create_incidents:
                    // Iniciar la actividad FinishedTripsActivity al seleccionar la opción "Viajes finalizados"
                    Intent intentIncidents = new Intent(MainActivity.this, SendIncidentsActivity.class);
                    startActivity(intentIncidents);
                    return true;
                case R.id.sign_out:
                    // Cerrar la sesión del usuario
                    FirebaseAuth.getInstance().signOut();
                    // Iniciar la actividad ProfileActivity después de cerrar la sesión
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                    Toast.makeText(MainActivity.this, "Ha cerrado sesión correctamente", Toast.LENGTH_LONG).show();

                    return true;



            }

            return false;
        });

        String tripId = getIntent().getStringExtra("tripId");


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_my_trips);
        bottomNavigationView.setOnItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    if (itemId == R.id.bottom_nav_my_trips) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_world) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent searchIntent = new Intent(this, MapamundiActivity.class);
                        searchIntent.putExtra("tripId",tripId);
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
                }); // Generar la barra de navegación


        // Configurar las Cards para crear un nuevo viaje y consultar un viaje
        CardView createTripCard = findViewById(R.id.create_trip_card);
        ImageView iconImageView = findViewById(R.id.new_trip);
        iconImageView.setImageResource(R.drawable.new_trip);

        // Agregar un OnClickListener a la Card para crear un nuevo viaje
        createTripCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatingNewTripActivity.class);
            startActivity(intent);
        });

        // Obtener los valores de los campos del Intent
         destination = getIntent().getStringExtra("destination");
         departureDate = getIntent().getStringExtra("departureDate");
         returnDate = getIntent().getStringExtra("returnDate");
         peopleCount = getIntent().getStringExtra("peopleCount");
         price = getIntent().getStringExtra("price");


        // Agregar el CardView con los datos del viaje
        if (destination != null) {
            addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
        }
    }


    /**
     * Método que se ejecuta cuando se selecciona un elemento del menú de la barra de herramientas.
     * @param item elemento del menú seleccionado
     * @return true si el elemento del menú se ha seleccionado correctamente, false en caso contrario
     */    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Manejar los clicks en los elementos del menú de la barra de herramientas
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Método que crea un CardView para mostrar los datos de un viaje en la pantalla principal.  
     * @param destination destino del viaje
     * @param departureDate fecha de ida del viaje
     * @param returnDate fecha de vuelta del viaje
     * @param peopleCount número de personas que viajan
     * @param price precio del viaje
     * @param tripId ID del viaje
     * @see <a href="https://developer.android.com/guide/topics/ui/layout/cardview">CardView</a>
     */
    @SuppressLint("ResourceAsColor")
    private void addTripCard(String destination, String departureDate, String returnDate, String peopleCount, String price, String tripId) {
        // Crear un nuevo CardView
        CardView tripCard = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, margin, margin, margin);
        tripCard.setLayoutParams(layoutParams);
        tripCard.setRadius(30);
        tripCard.setCardElevation(10);
        tripCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.example_color));

        // Agregar el contenido del CardView
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.setPadding(45, 45, 45, 45);
        tripCard.addView(cardContentLayout);

        TextView destinationText = new TextView(this);
        destinationText.setText(destination);
        destinationText.setTextColor(ContextCompat.getColor(this, R.color.black));
        destinationText.setTextSize(30);
        destinationText.setPadding(0,0,0,50);
        cardContentLayout.addView(destinationText);

        // Crear un LinearLayout con orientación horizontal
        LinearLayout datesLayout = new LinearLayout(this);
        datesLayout.setOrientation(LinearLayout.HORIZONTAL);
        datesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Crear el TextView para la fecha de ida
        TextView departureDateText = new TextView(this);
        departureDateText.setText("Ida: " + departureDate);
        departureDateText.setTextColor(ContextCompat.getColor(this, R.color.black));
        departureDateText.setTextSize(18);

// Crear el TextView para la fecha de vuelta
        TextView returnDateText = new TextView(this);
        returnDateText.setText("Vuelta: " + returnDate);
        returnDateText.setPadding(30,0,0,0);
        returnDateText.setTextColor(ContextCompat.getColor(this, R.color.black));
        returnDateText.setTextSize(18);

        // Agregar los TextViews al LinearLayout
        datesLayout.addView(departureDateText);
        datesLayout.addView(returnDateText);

        // Agregar el LinearLayout al CardView
        cardContentLayout.addView(datesLayout);

        TextView peopleCountText = new TextView(this);
        peopleCountText.setText(peopleCount + " Personas");
        peopleCountText.setTextColor(ContextCompat.getColor(this, R.color.black));
        peopleCountText.setTextSize(18);
        cardContentLayout.addView(peopleCountText);

        TextView priceText = new TextView(this);
        priceText.setText(price + " €");
        priceText.setTextColor(ContextCompat.getColor(this, R.color.black));
        priceText.setTextSize(18);
        cardContentLayout.addView(priceText);

        // Agregar el CardView al contenedor
        LinearLayout tripsContainer = findViewById(R.id.trips_container);
        tripsContainer.addView(tripCard);

        // Crear un LinearLayout con orientación horizontal
        LinearLayout iconsLayout = new LinearLayout(this);
        iconsLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        iconsLayout.setGravity(Gravity.END);


        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());


        // Crear el ImageView para el icono de editar
        ImageView editIcon = new ImageView(this);
        editIcon.setImageResource(R.drawable.edit_icon);
        editIcon.setBackgroundColor(Color.TRANSPARENT);
        editIcon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        editIcon.setPadding(10, 10, 10, 10);
        iconsLayout.addView(editIcon);

        // Agregar un OnClickListener al icono de editar
        editIcon.setOnClickListener(new View.OnClickListener(){
            /**
             * @param view
             */
            @Override
            public void onClick(View view) {
                // Crear un AlertDialog.Builder para construir el AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Editar viaje");

                // Inflar el layout del formulario de edición del viaje
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_edit_trip, null);
                builder.setView(dialogView);

                // Obtener los campos del formulario
                 EditText destinationEditText = dialogView.findViewById(R.id.destination_edit_text);
                 EditText departureDateEditText = dialogView.findViewById(R.id.departure_date_edit_text);
                 EditText returnDateEditText = dialogView.findViewById(R.id.return_date_edit_text);
                 EditText peopleCountEditText = dialogView.findViewById(R.id.people_count_edit_text);
                 EditText priceEditText = dialogView.findViewById(R.id.price_edit_text);



                // Mostrar los datos del viaje en los campos del formulario
                destinationEditText.setText(destination);
                departureDateEditText.setText(departureDate);
                returnDateEditText.setText(returnDate);
                peopleCountEditText.setText(peopleCount);
                priceEditText.setText(price);


                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Obtener los valores de los campos del formulario
                        String newDestination = destinationEditText.getText().toString();
                        String newDepartureDate = departureDateEditText.getText().toString();
                        String newReturnDate = returnDateEditText.getText().toString();
                        String newPeopleCount = peopleCountEditText.getText().toString();
                        String newPrice = priceEditText.getText().toString();


                        // Verificar si alguno de los campos está vacío
                        boolean hasEmptyFields = false;
                        if (newDestination.isEmpty()) {
                            destinationEditText.setError("Este campo es obligatorio");
                            hasEmptyFields = true;
                        }
                        if (newDepartureDate.isEmpty()) {
                            departureDateEditText.setError("Este campo es obligatorio");
                            hasEmptyFields = true;
                        }
                        if (newReturnDate.isEmpty()) {
                            returnDateEditText.setError("Este campo es obligatorio");
                            hasEmptyFields = true;
                        }
                        if (newPeopleCount.isEmpty()) {
                            peopleCountEditText.setError("Este campo es obligatorio");
                            hasEmptyFields = true;
                        } else if (!newPeopleCount.matches("\\d+")) {
                            peopleCountEditText.setError("Ingrese un valor numérico válido");
                            hasEmptyFields = true;
                        }

                        if (newPrice.isEmpty()) {
                            priceEditText.setError("Este campo es obligatorio");
                            hasEmptyFields = true;
                        } else if (!newPrice.matches("\\d+")) {
                            priceEditText.setError("Ingrese un valor numérico válido");
                            hasEmptyFields = true;
                        }

                        if (hasEmptyFields) {
                            // Mostrar un mensaje al usuario
                            Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Actualizar los datos del viaje en la base de datos de Firebase
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        db.collection("users").document(uid).collection("viajes").document(tripId)
                                .update(
                                        "destination", newDestination,
                                        "departureDate", newDepartureDate,
                                        "returnDate", newReturnDate,
                                        "peopleCount", newPeopleCount,
                                        "price", newPrice
                                )
                                .addOnSuccessListener(aVoid -> {
                                    // Los datos del viaje se actualizaron correctamente en la base de datos

                                    // Actualizar la visualización del viaje en la pantalla principal
                                    destinationText.setText(newDestination);
                                    departureDateText.setText("Ida: " + newDepartureDate);
                                    returnDateText.setText("Vuelta: " + newReturnDate);
                                    peopleCountText.setText(newPeopleCount + " Personas");
                                    priceText.setText(newPrice + " €");

                                })
                                .addOnFailureListener(e -> {
                                    // Ocurrió un error al intentar actualizar los datos del viaje en la base de datos
                                    Toast.makeText(MainActivity.this, "Error al actualizar el viaje", Toast.LENGTH_SHORT).show();
                                });

                    }
                });

                    // Agregar un botón "Cancelar" al AlertDialog
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    /**
                     * @param dialogInterface
                     * @param i
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cerrar el AlertDialog sin guardar los cambios
                        dialogInterface.dismiss();

                    }
                });

                // Mostrar el AlertDialog
                builder.show();
            }
        });

        // Crear el ImageView para el icono de completar
        ImageView doneIcon = new ImageView(this);
        doneIcon.setImageResource(R.drawable.done_icon);
        doneIcon.setBackgroundColor(Color.TRANSPARENT);
        doneIcon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        doneIcon.setPadding(10, 10, 10, 10);
        iconsLayout.addView(doneIcon);
        // Agregar un OnClickListener al icono de completar
        doneIcon.setOnClickListener(v -> {
            // Crear un AlertDialog para preguntar al usuario si ha finalizado el viaje
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("¿Ha finalizado este viaje?");
            builder.setMessage("Si pulsa sobre \"Sí\" el viaje quedará guardado en \"Viajes finalizados\"");
            builder.setPositiveButton("Sí", (dialog, which) -> {

                // Enviar un mensaje con el valor del destino a la actividad "MapamundiActivity"
                Intent intent = new Intent("com.example.triptrack.DESTINATION_DATA");
                intent.putExtra("destination", destination);
                sendBroadcast(intent);
                Log.d(TAG, "count: " + count);
                count = count + 1;  // Contar los viajes finalizados

                SharedPreferences prefs = getSharedPreferences("ViajesFinalizados", Context.MODE_PRIVATE);
                int exp = prefs.getInt("exp", 0);


                if (count < 4) {
                    exp = exp + 100;
                    Toast.makeText(MainActivity.this, "Has ganado 100 puntos de experiencia por tu viaje", Toast.LENGTH_SHORT).show();
                }
                else if (count == 4) {

                    exp = exp + 5000;
                    Toast.makeText(MainActivity.this, "¡Has llegado al nivel 2! Has ganado 5000 puntos de experiencia", Toast.LENGTH_SHORT).show();
                    exp = exp + 500;
                    Toast.makeText(MainActivity.this, "Has ganado 500 puntos de experiencia por tu viaje", Toast.LENGTH_SHORT).show();

                }
                else if (count == 5) {
                    exp = exp + 500;
                    Toast.makeText(MainActivity.this, "Has ganado 500 puntos de experiencia por tu viaje", Toast.LENGTH_SHORT).show();
                }


                else if (count == 6) {

                    exp = exp + 10000;
                    Toast.makeText(MainActivity.this, "¡Has llegado al nivel 3! Has ganado 10000 puntos de experiencia", Toast.LENGTH_SHORT).show();
                    exp = exp + 750;
                    Toast.makeText(MainActivity.this, "Has ganado 750 puntos de experiencia por tu viaje", Toast.LENGTH_SHORT).show();

                }
                else if (count == 7) {
                    exp = exp + 750;
                    Toast.makeText(MainActivity.this, "Has ganado 750 puntos de experiencia", Toast.LENGTH_SHORT).show();
                }

                else if (count == 8) {

                    exp = exp + 20000;
                    Toast.makeText(MainActivity.this, "¡Has llegado al nivel 4! Has ganado 20000 puntos de experiencia", Toast.LENGTH_SHORT).show();
                    exp = exp + 1000;
                    Toast.makeText(MainActivity.this, "Has ganado 1000 puntos de experiencia por tu viaje", Toast.LENGTH_SHORT).show();

                }

                else {
                    exp = exp + 1000;
                    Toast.makeText(MainActivity.this, "Has ganado 1000 puntos de experiencia", Toast.LENGTH_SHORT).show();
                }

                prefs = getSharedPreferences("ViajesFinalizados", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("viajes_finalizados", count);
                editor.putInt("exp", exp);
                editor.apply();

                // Cambiar el estado del viaje a "finalizado" en la base de datos de Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DocumentReference tripRef = db.collection("users").document(uid).collection("viajes").document(tripId);

                tripRef.update("status", "finalizado")
                        .addOnSuccessListener(aVoid -> {
                            // Eliminar el CardView correspondiente al viaje de la pantalla principal
                            @SuppressLint("CutPasteId") LinearLayout tripsContainer12 = findViewById(R.id.trips_container);
                            tripsContainer12.removeView(tripCard);

                            // Enviar un mensaje con los datos del viaje a la actividad "FinishedTripsActivity"
                            Intent intent2 = new Intent("com.example.triptrack.TRIP_DATA");
                            intent2.putExtra("destination", destination);
                            intent2.putExtra("departureDate", departureDate);
                            intent2.putExtra("returnDate", returnDate);
                            intent2.putExtra("peopleCount", peopleCount);
                            intent2.putExtra("price", price);
                            intent2.putExtra("tripId",tripId);
                            sendBroadcast(intent2);


                            Toast.makeText(MainActivity.this, "Cargado en \"Viajes finalizados\"", Toast.LENGTH_SHORT).show();
                        });

                verifyTrips();
            });

            builder.setNegativeButton("No", null);
            builder.show();
        });


        // Crear el ImageView para el icono de eliminar
        ImageView deleteIcon = new ImageView(this);
        deleteIcon.setImageResource(R.drawable.delete_icon);
        deleteIcon.setBackgroundColor(Color.TRANSPARENT);
        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        deleteIcon.setPadding(10, 10, 10, 10);
        iconsLayout.addView(deleteIcon);

        // Agregar un OnClickListener al icono de eliminar
        deleteIcon.setOnClickListener(v -> {
            // Mostrar un AlertDialog para confirmar la eliminación del viaje
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Eliminar viaje")
                    .setMessage("¿Está seguro de eliminar este viaje?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        // Eliminar el viaje de la base de datos de Firebase
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        db.collection("users").document(uid).collection("viajes").document(tripId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {

                                    // Eliminar el CardView correspondiente al viaje de la pantalla principal
                                    @SuppressLint("CutPasteId") LinearLayout tripsContainer1 = findViewById(R.id.trips_container);
                                    tripsContainer1.removeView(tripCard);
                                })
                                .addOnFailureListener(e -> {
                                    // Ocurrió un error al intentar eliminar el viaje de la base de datos
                                    Toast.makeText(MainActivity.this, "Error al eliminar el viaje", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });



// Agregar el LinearLayout al CardView
        cardContentLayout.addView(iconsLayout);
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        tripCard.setOnClickListener(view -> {
            // Aquí puedes agregar el código para abrir la nueva Activity
            Intent intent = new Intent(MainActivity.this, InfoTripActivity.class);
            // Puedes pasar información a la nueva Activity usando putExtra
            intent.putExtra("destination", destination);
            intent.putExtra("departureDate", departureDate);
            intent.putExtra("returnDate", returnDate);
            intent.putExtra("peopleCount", peopleCount);
            intent.putExtra("price", price);
            intent.putExtra("tripId",tripId);
            startActivity(intent);
        });

    }

    private void verifyTrips() {
        // Obtener todos los documentos de la colección "viajes" que no tienen el atributo "status" con valor "finalizado"
        db.collection("users").document(uid).collection("viajes")
                .whereNotEqualTo("status", "finalizado")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            // Hay viajes en la colección "viajes" que no tienen el atributo "status" con valor "finalizado"
                            warningCard.setVisibility(View.GONE);
                            list_trips_text.setVisibility(View.VISIBLE);
                            loadTrips();
                        } else {
                            // No hay viajes en la colección "viajes" que no tengan el atributo "status" con valor "finalizado"
                            warningCard.setVisibility(View.VISIBLE);
                            LottieAnimationView animationView = findViewById(R.id.animation_view);
                            animationView.cancelAnimation();
                            animationView.setVisibility(View.GONE);
                        }
                    }  // Error al obtener los datos de la colección "viajes"
                });

    }

    /**
     * Es un método que carga los viajes del usuario actual en la pantalla principal. 
     * Se obtienen todos los documentos de la colección "viajes" que no tienen el atributo "status" con valor "finalizado".
     * Si no hay viajes, se muestra un mensaje al usuario.
     */
    private void loadTrips() {
        // Obtener una referencia a la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Mostrar la animación de Lottie
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();

        // Obtener todos los documentos de la colección "viajes" que no tienen el atributo "status" con valor "finalizado"
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db.collection("users").document(uid).collection("viajes")
                
                .whereNotEqualTo("status", "finalizado") // Filtrar los viajes que no tienen el atributo "status" con valor "finalizado"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiar el contenedor de viajes
                        LinearLayout tripsContainer = findViewById(R.id.trips_container);
                        tripsContainer.removeAllViews();
                        // Detener la animación de Lottie
                        animationView.cancelAnimation();
                        animationView.setVisibility(View.GONE);

                        // Recorrer todos los documentos y agregar un CardView para cada uno
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String destination = document.getString("destination");
                            String departureDate = document.getString("departureDate");
                            String returnDate = document.getString("returnDate");
                            String peopleCount = document.getString("peopleCount");
                            String price = document.getString("price");
                            String tripId = document.getString("tripId");

                            addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
                        }
                    } else {
                        // Ocurrió un error al obtener los viajes
                        Toast.makeText(MainActivity.this, "Error al cargar los viajes", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
