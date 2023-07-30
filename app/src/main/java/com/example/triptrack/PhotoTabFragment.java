package com.example.triptrack;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private MyGridViewAdapter gridViewAdapter;
    private List<Bitmap> myImageList;
    private Bitmap imageBitmap;


    public PhotoTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoTabFragment newInstance(String param1, String param2) {
        PhotoTabFragment fragment = new PhotoTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tripId = getArguments().getString("tripId");

        GridView gridView = view.findViewById(R.id.gridview);
        gridView.setNumColumns(3); // Establecer el número de columnas a 3
        gridView.setVerticalSpacing(10);
        gridViewAdapter = new MyGridViewAdapter(getActivity(), myImageList);
        gridView.setAdapter(gridViewAdapter);

        // Cargar las imágenes desde el almacenamiento interno y mostrarlas en el GridView
        File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    gridViewAdapter.add(bitmap);
                } catch (Exception e) {
                    // Manejar excepción
                }
            }
            gridViewAdapter.notifyDataSetChanged();
        }

        // Añadir la imagen al adaptador del GridView
        if (imageBitmap != null) {
            gridViewAdapter.add(imageBitmap);
            gridViewAdapter.notifyDataSetChanged();
        }

        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acciones a realizar cuando se hace clic en el LinearLayout

                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Acciones a realizar cuando se captura una imagen con la cámara
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data"); // Actualizar el valor de imageBitmap

            // Guardar la imagen en el almacenamiento interno de la aplicación
            String tripId = getActivity().getIntent().getStringExtra("tripId");
            File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);
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

            // Mostrar la imagen en el GridView
            gridViewAdapter.add(imageBitmap);
            gridViewAdapter.notifyDataSetChanged();
        }

     else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            // Acciones a realizar cuando se selecciona una imagen de la galería
            Uri selectedImage = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage); // Actualizar el valor de imageBitmap
                // Mostrar la imagen en el GridView
                gridViewAdapter.add(imageBitmap);
                gridViewAdapter.notifyDataSetChanged();

                // Obtener el nombre del archivo de la imagen seleccionada
                String fileName = null;
                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }

                String tripId = getArguments().getString("tripId");
                // Guardar la imagen en el almacenamiento interno de la aplicación
                File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, fileName);
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImage);
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
