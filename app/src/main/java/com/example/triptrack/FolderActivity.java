package com.example.triptrack;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class FolderActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private LottieAnimationView lottieAnimationView;

    private TextView messageTextView, messageTextView2;

    private View rootView;

    private FloatingActionButton createFolderButton;

    private Bitmap imageBitmap;

    private GridView gridView;

    private ImageAdapter adapter;




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

        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();

        messageTextView =findViewById(R.id.messageTextView);
        messageTextView2 = findViewById(R.id.messageTextView2);

        createFolderButton = findViewById(R.id.createFolderButton);


        // Obtener una referencia al GridView
        gridView = findViewById(R.id.gridView);

        // Crear un adaptador para el GridView
        adapter = new ImageAdapter(this);

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
                // Verificar si hay archivos en la lista
                    // Hay imágenes o videos en el directorio
                    lottieAnimationView.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.GONE);
                    messageTextView2.setVisibility(View.GONE);
                    createFolderButton.setVisibility(View.VISIBLE);
                    adapter.setFiles(files);
            } else {
                // No hay imágenes o videos en el directorio
                lottieAnimationView.setVisibility(View.VISIBLE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView2.setVisibility(View.VISIBLE);
                createFolderButton.setVisibility(View.GONE);
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
                                // Verificar si quedan fotos en la carpeta
                                if (files != null && files.length > 0) {
                                    // Hay imágenes o videos en el directorio
                                    lottieAnimationView.setVisibility(View.GONE);
                                    messageTextView.setVisibility(View.GONE);
                                    messageTextView2.setVisibility(View.GONE);
                                    createFolderButton.setVisibility(View.VISIBLE);
                                } else {
                                    // No hay imágenes o videos en el directorio
                                    lottieAnimationView.setVisibility(View.VISIBLE);
                                    messageTextView.setVisibility(View.VISIBLE);
                                    messageTextView2.setVisibility(View.VISIBLE);
                                    createFolderButton.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
                return true;
            });
            popupMenu.show();
            return true;
        });

        createFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones a realizar cuando se hace clic en el LinearLayout

                PopupMenu popupMenu = new PopupMenu(FolderActivity.this, v);
                popupMenu.getMenu().add("Cámara");
                popupMenu.getMenu().add("Galería");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Cámara")) {
                            // Acciones a realizar cuando se selecciona la opción "Cámara"
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        } else if (item.getTitle().equals("Galería")) {
                            // Acciones a realizar cuando se selecciona la opción "Galería"
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_IMAGE_PICK);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones a realizar cuando se hace clic en el LinearLayout

                PopupMenu popupMenu = new PopupMenu(FolderActivity.this, v);
                popupMenu.getMenu().add("Cámara");
                popupMenu.getMenu().add("Galería");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Cámara")) {
                            // Acciones a realizar cuando se selecciona la opción "Cámara"
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        } else if (item.getTitle().equals("Galería")) {
                            // Acciones a realizar cuando se selecciona la opción "Galería"
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_IMAGE_PICK);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Acciones a realizar cuando se captura una imagen con la cámara
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data"); // Actualizar el valor de imageBitmap

            // Guardar la imagen en el almacenamiento interno de la aplicación
            String tripId = getIntent().getStringExtra("tripId");
            String folderName = getIntent().getStringExtra("folderName");
            File directory = new File(FolderActivity.this.getFilesDir(), "Galería/" + tripId + "/" + folderName);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "JPEG_" + timeStamp + ".jpg";
            File file = new File(directory, fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                // Manejar excepción
            }

            // Leer los archivos de la carpeta especificada
            File[] files = directory.listFiles(file2 -> {
                // Filtrar solo los archivos de imagen o video
                String fileName2 = file.getName().toLowerCase();
                return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".mp4");
            });

            // Pasar la lista de archivos al adaptador del GridView
            adapter.setFiles(files);

            // Notificar al adaptador que los datos han cambiado
            adapter.notifyDataSetChanged();

            // Forzar a GridView a volver a dibujar todas las vistas
            gridView.invalidateViews();

            // Ocultar el LottieAnimationView y el TextView
            gridView.setVisibility(View.VISIBLE);
            LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
            lottieAnimationView.setVisibility(View.GONE);
            TextView messageTextView = findViewById(R.id.messageTextView);
            TextView messageTextView2 = findViewById(R.id.messageTextView2);
            messageTextView.setVisibility(View.GONE);
            messageTextView2.setVisibility(View.GONE);

            createFolderButton.setVisibility(View.VISIBLE);


        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            // Acciones a realizar cuando se selecciona una imagen de la galería
            Uri selectedImage = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(FolderActivity.this.getContentResolver(), selectedImage); // Actualizar el valor de imageBitmap

                // Obtener el nombre del archivo de la imagen seleccionada
                String fileName = null;
                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
                Cursor cursor = FolderActivity.this.getContentResolver().query(selectedImage, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }

                // Mostrar la imagen en el GridView
                adapter.add(imageBitmap, fileName);
                adapter.notifyDataSetChanged();

                // Ocultar el LottieAnimationView y el TextView
                LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
                lottieAnimationView.setVisibility(View.GONE);
                TextView messageTextView = findViewById(R.id.messageTextView);
                TextView messageTextView2 = findViewById(R.id.messageTextView2);
                messageTextView.setVisibility(View.GONE);
                messageTextView2.setVisibility(View.GONE);


                createFolderButton.setVisibility(View.VISIBLE);

                String tripId = getIntent().getStringExtra("tripId");
                String folderName = getIntent().getStringExtra("folderName"); // Guardar la imagen en el almacenamiento interno de la aplicación
                File directory = new File(FolderActivity.this.getFilesDir(), "Galería/" + tripId + "/" + folderName);

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, fileName);
                try {
                    InputStream inputStream = FolderActivity.this.getContentResolver().openInputStream(selectedImage);
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();

                } catch (IOException e) {
                    // Manejar excepción
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}