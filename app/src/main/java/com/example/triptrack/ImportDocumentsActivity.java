package com.example.triptrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import java.io.*;
import java.util.ArrayList;

public class ImportDocumentsActivity extends AppCompatActivity {

    private Button importButton;
    private RadioGroup documentTypeRadioGroup;
    private static final int READ_REQUEST_CODE = 42;
    private String selectedMimeType;

    private ListView documentListView;
    private DocumentAdapter documentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_documents);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        documentTypeRadioGroup = findViewById(R.id.document_type_radio_group);

        importButton = findViewById(R.id.import_button);
        importButton.setOnClickListener(v -> performFileSearch());

        documentListView = findViewById(R.id.document_list_view);
        documentAdapter = new DocumentAdapter(this, new ArrayList<>());
        documentListView.setAdapter(documentAdapter);

        String tripId = getIntent().getStringExtra("tripId");


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

        documentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Document document = documentAdapter.getItem(position);
                File file = new File(document.getPath());
                Uri uri = FileProvider.getUriForFile(ImportDocumentsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, getMimeType(file));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });
    }
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



        private void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        int checkedRadioButtonId = documentTypeRadioGroup.getCheckedRadioButtonId();
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

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
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
                    File directory = new File(getFilesDir(), "viajes/" + tripId);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                    File file = new File(directory, fileName);
                    try {
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

