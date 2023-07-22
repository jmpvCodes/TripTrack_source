package com.example.triptrack;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
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
import java.util.ArrayList;

public class MapamundiActivity extends AppCompatActivity {

    private MapView mapView;
    private ArrayList<OverlayItem> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapamundi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // Obtener el ID del item seleccionado
                        int itemId = item.getItemId();

                        // Realizar acciones basadas en el item seleccionado
                        switch (itemId) {
                            case R.id.bottom_nav_my_trips:
                                // Acción para la pestaña "Inicio"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent homeIntent = new Intent(MapamundiActivity.this, MainActivity.class);
                                startActivity(homeIntent);
                                return true;
                            case R.id.bottom_nav_settings:
                                // Acción para la pestaña "Perfil"
                                // Ejemplo: iniciar la actividad correspondiente
                                Intent profileIntent = new Intent(MapamundiActivity.this, ConfigurationActivity.class);
                                startActivity(profileIntent);
                                return true;
                        }

                        return false;
                    }
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

        // Agregar marcadores de ejemplo
        addMarker(new GeoPoint(40.7128, -74.0060), "Nueva York");
        addMarker(new GeoPoint(51.5074, -0.1278), "Londres");
        addMarker(new GeoPoint(-33.8688, 151.2093), "Sídney");

        // Mostrar los marcadores en el mapa
        showMarkers();
    }

    // Método para agregar un marcador a la lista
    private void addMarker(GeoPoint position, String title) {
        OverlayItem marker = new OverlayItem(title, null, position);
        markers.add(marker);
    }

    // Método para mostrar los marcadores en el mapa
    private void showMarkers() {
        ItemizedIconOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(this, markers, null);
        mapView.getOverlays().add(overlay);
        mapView.invalidate(); // Actualizar el mapa
    }
}
