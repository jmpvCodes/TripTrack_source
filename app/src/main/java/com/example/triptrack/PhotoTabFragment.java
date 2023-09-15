package com.example.triptrack;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    private String mParam2;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private MyGridViewAdapter gridViewAdapter;

    private TextView messageTextView, messageTextView2;
    private List<Bitmap> myImageList;
    private Bitmap imageBitmap;

    private View rootView;

    private FloatingActionButton createFolderButton;

    private GridView gridView;

    private LottieAnimationView lottieAnimationView;


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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_tab, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String tripId = getArguments().getString("tripId");

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();


        messageTextView = view.findViewById(R.id.messageTextView);
        messageTextView2 = view.findViewById(R.id.messageTextView2);

        createFolderButton = view.findViewById(R.id.createFolderButton);

        gridView = view.findViewById(R.id.gridview);
        gridView.setNumColumns(3); // Establecer el número de columnas a 3
        gridView.setVerticalSpacing(10);
        gridViewAdapter = new MyGridViewAdapter(getActivity(), myImageList);
        gridView.setAdapter(gridViewAdapter);



        // Cargar las imágenes desde el almacenamiento interno y mostrarlas en el GridView
        File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);

        // Crear un FileFilter para obtener solo los archivos con extensiones específicas
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                // Obtener la extensión del archivo
                String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                // Verificar si la extensión del archivo es una de las extensiones permitidas
                return extension.equals("jpg") || extension.equals("png") || extension.equals("mp4") || extension.equals("jpeg") ;
            }
        };

// Obtener la lista de archivos que cumplen con el filtro
        File[] files = directory.listFiles(filter);


// Verificar si hay archivos en la lista
        if (files != null && files.length > 0) {
            // Hay imágenes o videos en el directorio
            lottieAnimationView.setVisibility(View.GONE);
            messageTextView.setVisibility(View.GONE);
            messageTextView2.setVisibility(View.GONE);
            createFolderButton.setVisibility(View.VISIBLE);

            // Iterar sobre todos los archivos y agregar cada uno al GridView
            for (File file : files) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                gridViewAdapter.add(bitmap, file.getName());
            }

            // Notificar al adaptador que los datos han cambiado
            gridViewAdapter.notifyDataSetChanged();

            // Añadir la imagen al adaptador del GridView
            if (imageBitmap != null) {
                // Aquí debes obtener el nombre real del archivo de la imagen y pasarlo como segundo parámetro al método add(Bitmap image, String fileName)
                String fileName = "Koala_climbing_tree.jpg"; // Reemplazar con el nombre real del archivo de la imagen
                gridViewAdapter.add(imageBitmap, fileName);
                gridViewAdapter.notifyDataSetChanged();
            }
            createFolderButton.setOnClickListener(new View.OnClickListener() {
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

        } else {
            // No hay imágenes o videos en el directorio
            lottieAnimationView.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView2.setVisibility(View.VISIBLE);
            createFolderButton.setVisibility(View.GONE);
            createFolderButton.setOnClickListener(new View.OnClickListener() {
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

            lottieAnimationView.setOnClickListener(new View.OnClickListener() {
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

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Crear el menú contextual
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                // Obtener las carpetas disponibles y agregarlas al submenú
                Menu menu = popupMenu.getMenu();
                SubMenu subMenu = menu.addSubMenu("Enviar a");
                // Obtener la lista de carpetas en la carpeta tripId
                String tripId = getActivity().getIntent().getStringExtra("tripId");
                File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);
                File[] folders = directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });
                for (File folder : folders) {
                    subMenu.add(folder.getName());
                }
                // Agregar la opción "Borrar" al menú contextual
                menu.add("Borrar");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Borrar")) {
                            // Mostrar un mensaje de confirmación antes de eliminar la imagen
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Borrar imagen")
                                    .setMessage("¿Está seguro que desea eliminar esta imagen de TripTrack?")
                                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Eliminar la imagen del almacenamiento interno de la aplicación
                                            deleteImage(position);
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                        } else if (item.hasSubMenu()) {
                            // No hacer nada si se selecciona el submenú "Enviar a"
                        } else {
                            // Acciones a realizar cuando se selecciona una carpeta
                            // Por ejemplo, guardar la imagen en la carpeta seleccionada
                            saveImageToFolder(position, item.getTitle().toString());
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el nombre del archivo en la posición especificada
                String fileName = gridViewAdapter.getFileName(position);
                // Crear un Intent para abrir el archivo
                String tripId = getActivity().getIntent().getStringExtra("tripId");
                File file = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + fileName);
                Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

            // Ocultar el LottieAnimationView y el TextView
            gridView.setVisibility(View.VISIBLE);
            LottieAnimationView lottieAnimationView = rootView.findViewById(R.id.lottieAnimationView);
            lottieAnimationView.setVisibility(View.GONE);
            TextView messageTextView = rootView.findViewById(R.id.messageTextView);
            TextView messageTextView2 = rootView.findViewById(R.id.messageTextView2);
            messageTextView.setVisibility(View.GONE);
            messageTextView2.setVisibility(View.GONE);

            // Mostrar la imagen en el GridView
            gridViewAdapter.add(imageBitmap, fileName);
            gridViewAdapter.notifyDataSetChanged();


            createFolderButton.setVisibility(View.VISIBLE);


        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            // Acciones a realizar cuando se selecciona una imagen de la galería
            Uri selectedImage = data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage); // Actualizar el valor de imageBitmap

                // Obtener el nombre del archivo de la imagen seleccionada
                String fileName = null;
                String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                    cursor.close();
                }

                // Mostrar la imagen en el GridView
                gridViewAdapter.add(imageBitmap, fileName);
                gridViewAdapter.notifyDataSetChanged();

                // Ocultar el LottieAnimationView y el TextView
                LottieAnimationView lottieAnimationView = rootView.findViewById(R.id.lottieAnimationView);
                lottieAnimationView.setVisibility(View.GONE);
                TextView messageTextView = rootView.findViewById(R.id.messageTextView);
                TextView messageTextView2 = rootView.findViewById(R.id.messageTextView2);
                messageTextView.setVisibility(View.GONE);
                messageTextView2.setVisibility(View.GONE);


                createFolderButton.setVisibility(View.VISIBLE);

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

    private void saveImageToFolder(int position, String folder) {
        // Obtener el nombre del archivo en la posición especificada
        String fileName = gridViewAdapter.getFileName(position);
        // Mover la imagen a la carpeta especificada
        String tripId = getActivity().getIntent().getStringExtra("tripId");
        File sourceFile = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + fileName);
        File destinationFile = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + folder + "/" + fileName);
        sourceFile.renameTo(destinationFile);
        sourceFile.delete();
        // Eliminar la imagen del adaptador del GridView
        gridViewAdapter.remove(position);
        gridViewAdapter.notifyDataSetChanged();


        // Verificar si hay imágenes en el adaptador del GridView
        if (gridViewAdapter.getCount() == 0) {
            // No hay imágenes en el adaptador del GridView
            // Mostrar el Lottie y el TextView y ocultar el GridView
            lottieAnimationView.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView2.setVisibility(View.VISIBLE);
            createFolderButton.setVisibility(View.GONE);
            gridViewAdapter.clear();
        }
    }

    private void deleteImage(int position) {
        // Obtener el nombre del archivo en la posición especificada
        String fileName = gridViewAdapter.getFileName(position);
        // Eliminar la imagen del almacenamiento interno de la aplicación
        String tripId = getActivity().getIntent().getStringExtra("tripId");
        File file = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + fileName);
        file.delete();
        // Eliminar la imagen del adaptador del GridView
        gridViewAdapter.remove(position);
        gridViewAdapter.notifyDataSetChanged();

        // Verificar si hay imágenes en el adaptador del GridView
        if (gridViewAdapter.getCount() == 0) {
            // No hay imágenes en el adaptador del GridView
            // Mostrar el Lottie y el TextView y ocultar el GridView
            lottieAnimationView.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView2.setVisibility(View.VISIBLE);
            createFolderButton.setVisibility(View.GONE);
        }
    }

}


