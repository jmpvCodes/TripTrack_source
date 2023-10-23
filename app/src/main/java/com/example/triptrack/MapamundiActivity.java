package com.example.triptrack;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.json.JSONException;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.*;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta clase representa la actividad Mapamundi.
 * Esta actividad muestra un mapa del mundo y permite al usuario agregar marcadores en el mapa.
 */
public class MapamundiActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Obtener el ID del usuario actual
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ItemizedIconOverlay[] markerOverlay;

    private MapView mapView;
    private ArrayList<OverlayItem> markers;

    private RequestQueue requestQueue;

private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapamundi);
        Configuration.getInstance().getOsmdroidTileCache().delete();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Configurar el ítem seleccionado en BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_world);
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

        // Crear una sola cola de solicitudes al inicio de tu actividad
        requestQueue = Volley.newRequestQueue(this);

        mapView = findViewById(R.id.mapView);
        markers = new ArrayList<>();



// Obtener la referencia a la colección de usuarios
        CollectionReference usersRef = db.collection("users");

        // Obtener la referencia al usuario actual (aquí necesitarás el ID del usuario actual)
        assert user != null;
        String userId = user.getUid(); // "userId" es el ID del usuario actual
        DocumentReference userRef = usersRef.document(userId);

        // Obtener la referencia a la colección de marcadores del usuario
        CollectionReference markersRef = userRef.collection("markers");

        // Leer los marcadores de Firestore al iniciar la actividad
        markersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    double lat = document.getDouble("latitude");
                    double lon = document.getDouble("longitude");
                    CustomOverlayItem marker = new CustomOverlayItem("Lugar", "Description", new GeoPoint(lat, lon));
                    marker.setId(document.getId()); // Suponiendo que el ID del documento es el identificador único del marcador
                    markers.add(marker);
                }

                // Configurar el User Agent
                Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
                // Configurar el proveedor de mapas y la fuente de azulejos
                mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
                // Establecer el zoom inicial y la ubicación en el mapa (opcional)
                mapView.getController().setZoom(5); // Zoom inicial
                mapView.getController().setCenter(new GeoPoint(40.41, -3.69)); // Ubicación inicial de Madrid

                // Crear un nuevo ItemizedIconOverlay con todos los marcadores actuales
                markerOverlay = new ItemizedIconOverlay[]{new ItemizedIconOverlay<>(MapamundiActivity.this, markers, gestureListener)};

                // Añadir el ItemizedIconOverlay al MapView
                mapView.getOverlays().add(markerOverlay[0]);

                // Actualizar el mapa después de cargar los marcadores
                mapView.invalidate();
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });


        mapView.getOverlays().add(new Overlay() {
            @Override
            public boolean onDoubleTap(final MotionEvent e, final MapView mapView) {

                // Convertir las coordenadas del toque a una posición geográfica
                GeoPoint point = (GeoPoint) mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());

                // Crear un nuevo marcador en esa posición y añadirlo a la lista de marcadores
                CustomOverlayItem newMarker = new CustomOverlayItem("Lugar", "Description", point);
                markers.add(newMarker);

                // Obtener la referencia a la colección de usuarios
                CollectionReference usersRef = db.collection("users");

                // Obtener la referencia al usuario actual (aquí necesitarás el ID del usuario actual)
                String userId = user.getUid(); // "userId" es el ID del usuario actual
                DocumentReference userRef = usersRef.document(userId);

                Log.d(TAG, "userId = " + userId);

                // Obtener la referencia a la colección de marcadores del usuario
                CollectionReference markersRef = userRef.collection("markers");

                // Guardar el nuevo marcador en Firestore
                GeoPoint markerPoint = (GeoPoint) newMarker.getPoint();
                Map<String, Object> latLng = new HashMap<>();
                latLng.put("latitude", markerPoint.getLatitude());
                latLng.put("longitude", markerPoint.getLongitude());

                // Generar un identificador único para el marcador
                String markerId = UUID.randomUUID().toString();

                // Guardar el identificador único en el marcador para su uso posterior
                newMarker.setId(markerId);

                // Usar el identificador único en lugar del índice para guardar el marcador en Firestore
                markersRef.document(markerId).set(latLng);

                // Eliminar el ItemizedIconOverlay anterior del mapa
                mapView.getOverlays().remove(markerOverlay[0]);

                // Crear un nuevo ItemizedIconOverlay con los marcadores actualizados y reemplazar el antiguo en el mapa
                markerOverlay[0] = new ItemizedIconOverlay<>(MapamundiActivity.this, markers, gestureListener);

                // Añadir el ItemizedIconOverlay al MapView
                mapView.getOverlays().add(markerOverlay[0]);

                // Actualizar el mapa
                mapView.invalidate();

                return true;
            }

        @Override
                public void draw(Canvas canvas, MapView mapView, boolean shadow) {

                }

                @Override
                public void onResume() {
                    super.onResume();
                    mapView.onResume(); // Asegúrate de llamar a onResume() en el MapView
                }

                @Override
                public void onPause() {
                    super.onPause();
                    mapView.onPause(); // Asegúrate de llamar a onPause() en el MapView
                }

            });

        }
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> gestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
        @Override
        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
            // Obtener las coordenadas del marcador
            GeoPoint point = (GeoPoint) item.getPoint();

            // Crear una nueva solicitud a la API de Geocodificación de OpenStreetMap
            String url = "https://nominatim.openstreetmap.org/reverse?lat=" + point.getLatitude() + "&lon=" + point.getLongitude() + "&format=json";

            // Realizar la solicitud (esto es solo un ejemplo, deberías hacerlo en un hilo separado para no bloquear el hilo principal)
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            // Obtener el nombre del lugar a partir de la respuesta
                            String placeName = response.getString("display_name");

                            // Mostrar los datos del lugar
                            Toast.makeText(MapamundiActivity.this, item.getTitle() + ": " + placeName, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }, Throwable::printStackTrace);

            // Añadir la solicitud a la cola de solicitudes
            requestQueue.add(request);

            return true;
        }

        @Override
        public boolean onItemLongPress(final int index, final OverlayItem item) {

            // Obtener la referencia a la colección de usuarios
            CollectionReference usersRef = db.collection("users");

            // Obtener la referencia al usuario actual (aquí necesitarás el ID del usuario actual)
            String userId = user.getUid(); // "userId" es el ID del usuario actual
            DocumentReference userRef = usersRef.document(userId);

            // Obtener la referencia a la colección de marcadores del usuario
            CollectionReference markersRef = userRef.collection("markers");

            // Obtener el identificador único del marcador
            String markerId = ((CustomOverlayItem) item).getId();

            Log.d(TAG, "markerId = " + markerId);

            // Eliminar el marcador de Firestore usando el identificador único
            markersRef.document(markerId).delete();

            // Eliminar el marcador del mapa usando el identificador único
            for (int i = 0; i < markers.size(); i++) {
                if (((CustomOverlayItem) markers.get(i)).getId().equals(markerId)) {
                    markers.remove(i);
                    break;
                }
            }

            // Eliminar el ItemizedIconOverlay anterior del mapa
            mapView.getOverlays().remove(markerOverlay[0]);

            // Crear un nuevo ItemizedIconOverlay con los marcadores actualizados y reemplazar el antiguo en el mapa
            markerOverlay[0] = new ItemizedIconOverlay<>(MapamundiActivity.this, markers, this);

            // Añadir el ItemizedIconOverlay al MapView
            mapView.getOverlays().add(markerOverlay[0]);

            // Actualizar el mapa
            mapView.invalidate();

            return true;
        }

    };

    @Override
    public void onBackPressed() {

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);

        // Configurar el ítem seleccionado en BottomNavigationView
        // Cambia esto al ID del ítem correspondiente a la actividad a la que estás volviendo
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_my_trips);
    }


}
