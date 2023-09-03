package com.example.triptrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Objects;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        String tripId = getIntent().getStringExtra("tripId");
        String folderName = getIntent().getStringExtra("folderName");

        TextView titleFolder = findViewById(R.id.titleFolder);
        titleFolder.setText(folderName);

        // Obtener una referencia al GridView
        GridView gridView = findViewById(R.id.gridView);

        // Crear un adaptador para el GridView
        ImageAdapter adapter = new ImageAdapter(this);

        // Leer los archivos de la carpeta especificada
        File directory = new File(getFilesDir(), "Galería/" + tripId + "/" + folderName);
        Log.d("FolderActivity", "Directorio: " + directory.getAbsolutePath());
        if (directory.exists()) {
            File[] files = directory.listFiles(file -> {
                // Filtrar solo los archivos de imagen o video
                String fileName = file.getName().toLowerCase();
                return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".mp4");
            });

            // Pasar la lista de archivos al adaptador del GridView
            if (files != null && files.length > 0) {
                adapter.setFiles(files);
            } else {
                Toast.makeText(this, "No se encontraron imágenes o videos en la carpeta", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "La carpeta especificada no existe", Toast.LENGTH_LONG).show();

        }

        // Establecer el adaptador en el GridView
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // Obtener el archivo en la posición especificada
            File file = (File) adapter.getItem(position);

            // Crear un Intent para abrir el archivo
            Uri uri = FileProvider.getUriForFile(FolderActivity.this, "com.example.triptrack.provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Crear el menú contextual
            PopupMenu popupMenu = new PopupMenu(FolderActivity.this, view);
            Menu menu = popupMenu.getMenu();
            menu.add("Borrar");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Borrar")) {
                    // Mostrar un diálogo de confirmación al usuario
                    new AlertDialog.Builder(FolderActivity.this)
                            .setTitle("Eliminar momento")
                            .setMessage("¿Estás seguro de que deseas eliminar este momento?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                // Eliminar la foto de la carpeta
                                File file = (File) adapter.getItem(position);
                                file.delete();

                                // Actualizar el adaptador del GridView
                                File[] files = directory.listFiles(f -> {
                                    // Filtrar solo los archivos de imagen o video
                                    String fileName = f.getName().toLowerCase();
                                    return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".mp4");
                                });
                                adapter.setFiles(files);
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            });
            popupMenu.show();
            return true;
        });




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    if (itemId == R.id.nav_my_trips) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent mainIntent = new Intent(FolderActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_world) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent searchIntent = new Intent(FolderActivity.this, MapamundiActivity.class);
                        startActivity(searchIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_settings) {
                        // Acción para la pestaña "Perfil"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent profileIntent = new Intent(FolderActivity.this, ConfigurationActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }

                    return false;
                });

    }
}