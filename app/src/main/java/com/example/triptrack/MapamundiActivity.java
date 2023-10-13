package com.example.triptrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapamundiActivity extends AppCompatActivity {

    private MapView mapView;
    private ArrayList<OverlayItem> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapamundi);
        Configuration.getInstance().getOsmdroidTileCache().delete();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

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


        // Configuración del proveedor de mapas de osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Obtener referencia a la vista MapView del layout
        mapView = findViewById(R.id.mapView);

        // Configurar el proveedor de mapas y la fuente de azulejos
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // Establecer el zoom inicial y la ubicación en el mapa (opcional)
        mapView.getController().setZoom(3); // Zoom inicial
        mapView.getController().setCenter(new GeoPoint(0, 0)); // Ubicación inicial (coordenadas en latitud y longitud)

        // Inicializar la lista de marcadores
        markers = new ArrayList<>();

        IntentFilter filter = new IntentFilter("com.example.triptrack.DESTINATION_DATA");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Recuperar el valor del destino del intent
                String destination = intent.getStringExtra("destination");

                if (destination != null) {
                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocationName(destination, 1);
                        Log.d("MapamundiActivity", "Destination: " + destination);
                        Log.d("MapamundiActivity", "Addresses: " + addresses);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        GeoPoint geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
                        addMarker(geoPoint, destination);
                        showMarkers();


                }
                }            }
        };
        registerReceiver(receiver, filter);

    }

    // Método para agregar un marcador a la lista
    private void addMarker(GeoPoint position, String title) {
        OverlayItem marker = new OverlayItem(title, null, position);
        markers.add(marker);
        Log.d("MapamundiActivity", "Marker added: " + title + ", position: " + position);

    }

    // Método para mostrar los marcadores en el mapa
    private void showMarkers() {
        ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(this, markers, null);
        mapView.getOverlays().add(overlay);
        mapView.invalidate(); // Actualizar el mapa
    }
}
