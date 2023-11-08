package com.example.triptrack;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Esta clase representa el fragmento de la pestaña de la factura.
 */
public class InvoiceTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;


    private DocumentAdapter documentAdapter;


    /**
     * Constructor vacio requerido para el fragmento de la pestaña de la factura.
     */
    public InvoiceTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: Rename and change types of parameters
            getArguments().getString(ARG_PARAM1);
            getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invoice_tab, container, false);

        String tripId = getActivity().getIntent().getStringExtra("tripId");

        // Obtener una referencia a la lista de documentos y crear un adaptador para ella
        ListView documentListView = view.findViewById(R.id.listViewTickets);
        documentAdapter = new DocumentAdapter(getContext(), new ArrayList<>());
        documentListView.setAdapter(documentAdapter);

        // Actualizar el ListView
        updateListView();

        /*
         * Establecer un listener para la lista de documentos.
         * Cuando se hace clic en un elemento de la lista, se abre el archivo correspondiente en otra aplicación.
         */

        documentListView.setOnItemClickListener((parent, v, position, id) -> {
            Document document = documentAdapter.getItem(position);
            File file = new File(document.getPath());
            Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, getMimeType(file));
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        });

        // Obtener el FloatingActionButton
        FloatingActionButton floatingActionButton;
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(v -> {
            // Acciones a realizar cuando se hace clic en el LinearLayout

            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(getContext(), floatingActionButton);
            popupMenu.getMenu().add("Cámara");
            popupMenu.getMenu().add("Galería");
            popupMenu.setOnMenuItemClickListener(item -> {
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
            });
            popupMenu.show();
        });
        // Devolver la vista inflada
        return view;
    }

    @SuppressLint("Range")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap imageBitmap = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (imageBitmap != null) {
            // Guardar la imagen en el almacenamiento interno de la aplicación
            String tripId = getActivity().getIntent().getStringExtra("tripId");
            File directory = new File(getActivity().getFilesDir(), "Tickets/" + tripId);
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

            // Actualizar el ListView
            updateListView();
        }
    }
    private String getMimeType(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (mimeType == null) {
            mimeType = "image/jpeg";  // Establecer un tipo MIME predeterminado
        }
        return mimeType;
    }

    private void updateListView() {
        // Limpiar el adaptador
        documentAdapter.clear();

        // Leer los archivos del almacenamiento interno y agregarlos al adaptador
        String tripId = getActivity().getIntent().getStringExtra("tripId");
        File directory = new File(getActivity().getFilesDir(), "Tickets/" + tripId);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                Document document = new Document(file.getName(), file.getAbsolutePath());
                documentAdapter.add(document);
            }
            documentAdapter.notifyDataSetChanged();
        }
    }

}
