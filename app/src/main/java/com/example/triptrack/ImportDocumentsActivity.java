package com.example.triptrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Esta clase representa la pantalla de importación de documentos.
 * Permite al usuario importar documentos de su dispositivo y guardarlos en el almacenamiento interno de la aplicación.
 * Los documentos se muestran en una lista y se pueden abrir en otras aplicaciones.
 */

public class ImportDocumentsActivity extends AppCompatActivity {

    // Declaración de variables
    private RadioGroup documentTypeRadioGroup; // Grupo de botones de radio para seleccionar el tipo de documento
    private static final int READ_REQUEST_CODE = 42; // Código de solicitud para la selección de documentos
    private String selectedMimeType; // Tipo MIME seleccionado
    private DocumentAdapter documentAdapter; // Adaptador para la lista de documentos

    /**
     * Método que se llama cuando se crea la actividad.
     * @param savedInstanceState estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_documents);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());; // Generar la barra de herramientas

        documentTypeRadioGroup = findViewById(R.id.document_type_radio_group);


        // Obtener una referencia al botón de importación y establecer un listener para él
        Button importButton = findViewById(R.id.import_button);
        importButton.setOnClickListener(v -> performFileSearch());

        // Obtener una referencia a la lista de documentos y crear un adaptador para ella
        ListView documentListView = findViewById(R.id.document_list_view);
        documentAdapter = new DocumentAdapter(this, new ArrayList<>());
        documentListView.setAdapter(documentAdapter);

        String tripId = getIntent().getStringExtra("tripId"); // Obtener el ID del viaje


        // Leer los archivos del almacenamiento interno y agregarlos al adaptador
        File directory = new File(getFilesDir(), "viajes/" + tripId);

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                Document document = new Document(file.getName(), file.getAbsolutePath());
                documentAdapter.add(document);
            }
            documentAdapter.notifyDataSetChanged();
        }

        /*
         * Establecer un listener para la lista de documentos.
         * Cuando se hace clic en un elemento de la lista, se abre el archivo correspondiente en otra aplicación.
         */
        documentListView.setOnItemClickListener((parent, view, position, id) -> {
            Document document = documentAdapter.getItem(position);
            File file = new File(document.getPath());
            Uri uri = FileProvider.getUriForFile(ImportDocumentsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeType(file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });
    }
        
    /**
     * Obtiene el tipo MIME de un archivo. 
     * @see <a href="https://developer.android.com/training/secure-file-sharing/retrieve-info">Recuperar información de un archivo</a>
     * @param file archivo del que se va a obtener el tipo MIME
     * @return el tipo MIME del archivo
     */
        private String getMimeType (File file){
            String mimeType = null;
            String fileName = file.getName();
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex != -1) {
                String extension = fileName.substring(lastDotIndex + 1);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return mimeType;
        }


        /**
         * Crea un intento de selección de documentos y lo envía a la actividad apropiada.
         * El resultado de la actividad se recibe en onActivityResult.
         */
        private void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // Crear un intento de selección de documentos
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Establecer la categoría del intento

        int checkedRadioButtonId = documentTypeRadioGroup.getCheckedRadioButtonId();   // Obtener el ID del botón de radio seleccionado

        // Establecer el tipo MIME del intento en función del botón de radio seleccionado
        switch (checkedRadioButtonId) {
            case R.id.pdf_radio_button:
                selectedMimeType = "application/pdf";
                break;
            case R.id.word_radio_button:
                selectedMimeType = "application/msword";
                break;
            case R.id.excel_radio_button:
                selectedMimeType = "application/vnd.ms-excel";
                break;
            case R.id.image_radio_button:
                selectedMimeType = "image/*";
                break;
            case R.id.video_radio_button:
                selectedMimeType = "video/*";
                break;
            default:
                selectedMimeType = "*/*";
                break;
        }
        intent.setType(selectedMimeType);

        startActivityForResult(intent, READ_REQUEST_CODE); 
    }

    /**
     * Se llama cuando se recibe el resultado de una actividad. El resultado se recibe en forma de intento. 
     * Se comprueba si el resultado es de la actividad de selección de documentos y se obtiene el URI del archivo seleccionado.
     * @param requestCode codigo de solicitud de la actividad
     * @param resultCode codigo de resultado de la actividad
     * @param resultData resultado de la actividad
     * @see <a href="https://developer.android.com/training/basics/intents/result">Cómo obtener un resultado de una actividad</a>
     */
    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // Comprobar si el resultado es de la actividad de selección de documentos
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();

                // Comprobar si el tipo MIME del archivo seleccionado coincide con el tipo MIME seleccionado
                ContentResolver contentResolver = getContentResolver();
                String type = contentResolver.getType(uri);
                if (type != null && !type.startsWith(selectedMimeType.replace("*", ""))) {
                    Toast.makeText(this, "El archivo seleccionado no coincide con el tipo de archivo seleccionado", Toast.LENGTH_LONG).show();
                    return;
                }


                // Obtener el nombre del archivo seleccionado
                String fileName = null;
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    cursor.close();
                }

                // Guardar el archivo en el almacenamiento interno
                if (fileName != null) {
                    
                    String tripId = getIntent().getStringExtra("tripId");
                    File directory = new File(getFilesDir(), "viajes/" + tripId); // Crear un directorio para el viaje si no existe
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File file = new File(directory, fileName);
                    try {

                        // Copiar el archivo seleccionado en el almacenamiento interno
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();

                        // Agregar el archivo guardado al adaptador
                        Document document = new Document(fileName, file.getAbsolutePath());
                        documentAdapter.add(document);
                        documentAdapter.notifyDataSetChanged();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }
}

