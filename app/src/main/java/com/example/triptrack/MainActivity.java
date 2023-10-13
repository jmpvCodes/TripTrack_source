package com.example.triptrack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private TextView list_trips_text;

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
        CardView warningCard = findViewById(R.id.warning_no_trips);
        warningCard.setVisibility(View.GONE);

        list_trips_text = findViewById(R.id.list_trips_text);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

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

    // Configurar el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configurar el NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.sign_out);
        SpannableString s = new SpannableString(menuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
        menuItem.setTitle(s);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Cerrar el DrawerLayout al seleccionar una opción
            DrawerLayout drawer = findViewById(R.id.drawer_layout); // Utilizar el ID correcto del DrawerLayout
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    switch (itemId) {
                        case R.id.bottom_nav_world:
                            // Acción para la pestaña "Buscar"
                            // Ejemplo: iniciar la actividad correspondiente
                            Intent searchIntent = new Intent(MainActivity.this, MapamundiActivity.class);
                            startActivity(searchIntent);
                            return true;
                        case R.id.bottom_nav_profile:
                            // Acción para la pestaña "Perfil"
                            // Ejemplo: iniciar la actividad correspondiente
                            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                            return true;
                    }

                    return false;
                });


        // Configurar las Cards para crear un nuevo viaje y consultar un viaje
        CardView createTripCard = findViewById(R.id.create_trip_card);
        ImageView iconImageView = findViewById(R.id.new_trip);
        iconImageView.setImageResource(R.drawable.new_trip);

        createTripCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreatingNewTripActivity.class);
            startActivity(intent);
        });

        // Obtener los valores de los campos del Intent
        String destination = getIntent().getStringExtra("destination");
        String departureDate = getIntent().getStringExtra("departureDate");
        String returnDate = getIntent().getStringExtra("returnDate");
        String peopleCount = getIntent().getStringExtra("peopleCount");
        String price = getIntent().getStringExtra("price");
        String tripId = getIntent().getStringExtra("tripId");


        // Agregar el CardView con los datos del viaje
        if (destination != null) {
            addTripCard(destination, departureDate, returnDate, peopleCount, price, tripId);
        }
    }


    // Abrir o cerrar el drawer cuando se presiona el ícono de la appbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        editIcon.setOnClickListener(v -> {
            // Crear un AlertDialog.Builder para construir el AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Editar viaje");

            // Inflar el layout del formulario de edición del viaje
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_trip, null);
            builder.setView(dialogView);

            // Obtener los campos del formulario
            final EditText destinationEditText = dialogView.findViewById(R.id.destination_edit_text);
            final EditText departureDateEditText = dialogView.findViewById(R.id.departure_date_edit_text);
            final EditText returnDateEditText = dialogView.findViewById(R.id.return_date_edit_text);
            final EditText peopleCountEditText = dialogView.findViewById(R.id.people_count_edit_text);
            final EditText priceEditText = dialogView.findViewById(R.id.price_edit_text);

            // Mostrar los datos del viaje en los campos del formulario
            destinationEditText.setText(destination);
            departureDateEditText.setText(departureDate);
            returnDateEditText.setText(returnDate);
            peopleCountEditText.setText(peopleCount);
            priceEditText.setText(price);

            // Agregar un botón "Guardar" al AlertDialog
            builder.setPositiveButton("Guardar", (dialog, which) -> {
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
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            });
            // Agregar un botón "Cancelar" al AlertDialog
            builder.setNegativeButton("Cancelar", (dialog, which) -> {
                // Cerrar el AlertDialog sin guardar los cambios
                dialog.dismiss();
            });

            // Mostrar el AlertDialog
            builder.show();
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
                // TODO: Agregar código para completar el viaje y eliminarlo del menú principal

                // Enviar un mensaje con el valor del destino a la actividad "MapamundiActivity"
                Intent intent = new Intent("com.example.triptrack.DESTINATION_DATA");
                intent.putExtra("destination", destination);
                sendBroadcast(intent);

                // Por ejemplo, puedes cambiar el estado del viaje a "finalizado" en la base de datos de Firestore
                // y luego actualizar la interfaz de usuario para eliminar el Card del menú principal

                // Cambiar el estado del viaje a "finalizado" en la base de datos de Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    private void loadTrips() {
        // Obtener una referencia a la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Mostrar la animación de Lottie
        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.playAnimation();

        // Obtener todos los documentos de la colección "viajes" que no tienen el atributo "status" con valor "finalizado"
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(uid).collection("viajes")
                .whereNotEqualTo("status", "finalizado")
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
