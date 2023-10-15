package com.example.triptrack;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Fragmento que muestra la lista de carpetas de una galería de fotos.
 * Este fragmento se utiliza en la pestana "Album" de la actividad principal.
 */
public class AlbumTabFragment extends Fragment {

    // TODO: Renombrar los argumentos de los parámetros y elegir nombres que coincidan
    // con los parámetros de inicialización del fragmento, por ejemplo, ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    /**
     * Constructor público vacío requerido por el sistema
     */
    public AlbumTabFragment() {
        // Constructor público vacío requerido por el sistema
    }

    /**
     * Método de fábrica para crear una nueva instancia de este fragmento
     * utilizando los parámetros proporcionados.
     *
     * @param param1 Parámetro 1.
     * @param param2 Parámetro 2.
     * @return Una nueva instancia del fragmento AlbumTabFragment.
     */
    // TODO: Renombrar y cambiar los tipos y el número de parámetros según sea necesario
    public static AlbumTabFragment newInstance(String param1, String param2) {
        AlbumTabFragment fragment = new AlbumTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Cambiar los tipos de los parámetros según sea necesario
    }

    /**
     * Crea y devuelve la vista inflada para este fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño para este fragmento
        return inflater.inflate(R.layout.fragment_album_tab, container, false);
    }

    /**
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle)
     * haya creado la vista inflada para este fragmento.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener las vistas necesarias
        LottieAnimationView lottieAnimationView1 = view.findViewById(R.id.lottieAnimationView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        // Obtener la lista de carpetas en la carpeta tripId
        String tripId = requireActivity().getIntent().getStringExtra("tripId");
        File directory = new File(requireActivity().getFilesDir(), "Galería/" + tripId);
        File[] files = directory.listFiles();
        File[] folders;

        if (directory.isDirectory()) {
            folders = directory.listFiles(File::isDirectory);
        } else {
            folders = null;
        }

        List<String> folderNames = new ArrayList<>();
        if (folders != null && folders.length > 0) {
            for (File folder : folders) {
                folderNames.add(folder.getName());
            }

            lottieAnimationView1.setVisibility(View.GONE);
            messageTextView.setVisibility(View.GONE);

        } else {
            lottieAnimationView1.setVisibility(View.VISIBLE);
            lottieAnimationView1.setRepeatCount(LottieDrawable.INFINITE);
            lottieAnimationView1.playAnimation();
            messageTextView.setVisibility(View.VISIBLE);
        }

        // Mostrar la lista de carpetas en un ListView
        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, folderNames);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view12, position, id) -> {
            // Crear el menú contextual
            PopupMenu popupMenu = new PopupMenu(getContext(), view12);
            Menu menu = popupMenu.getMenu();
            menu.add("Borrar");
            menu.add("Renombrar");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Borrar")) {
                    String folderName = folderNames.get(position);
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Eliminar carpeta")
                            .setMessage("¿Estás seguro de que deseas eliminar esta carpeta?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                // Eliminar la carpeta del almacenamiento interno de la aplicación
                                File folder = new File(directory, folderName);
                                deleteRecursive(folder);

                                // Actualizar la lista de carpetas en el ListView
                                folderNames.remove(folderName);
                                adapter.notifyDataSetChanged();

                                // Forzar la actualización de la vista
                                if (folderNames.isEmpty()) {
                                    lottieAnimationView1.setVisibility(View.VISIBLE);
                                    lottieAnimationView1.setRepeatCount(LottieDrawable.INFINITE);
                                    lottieAnimationView1.playAnimation();
                                    messageTextView.setVisibility(View.VISIBLE);
                                } else {
                                    lottieAnimationView1.setVisibility(View.GONE);
                                    messageTextView.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();



                }

                if (item.getTitle().equals("Renombrar")) {
                    String folderName = folderNames.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Renombrar carpeta");

                    // Configurar el campo de texto de entrada
                    final EditText input = new EditText(requireContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(folderName);
                    builder.setView(input);

                    // Configurar los botones
                    builder.setPositiveButton("Aceptar", (dialog, which) -> {
                        String newFolderName = input.getText().toString();

                        // Renombrar la carpeta en el almacenamiento interno de la aplicación
                        File oldFolder = new File(directory, folderName);
                        File newFolder = new File(directory, newFolderName);
                        boolean success = oldFolder.renameTo(newFolder);

                        if (success) {
                            // Actualizar la lista de carpetas en el ListView
                            folderNames.set(position, newFolderName);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Error al renombrar la carpeta", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

                    builder.show();
                }

                return true;

            });
            popupMenu.show();
            return true;
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
                // Obtener el nombre de la carpeta seleccionada
                String folderName = (String) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), FolderActivity.class);
                intent.putExtra("tripId", tripId);
                intent.putExtra("folderName", folderName);
                startActivity(intent);

            });

            // Mostrar un menú contextual cuando se hace clic en el botón "Crear nueva carpeta"
            FloatingActionButton createFolderButton = view.findViewById(R.id.createFolderButton);
        createFolderButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setTitle("Crear nueva carpeta");

            // Configurar el campo de entrada de texto para el nombre de la carpeta
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Configurar los botones "Aceptar" y "Cancelar"
            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                // Acciones a realizar cuando se hace clic en "Aceptar"
                String folderName = input.getText().toString();

                // Crear una nueva carpeta
                File directory1 = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + folderName);
                if (!directory1.exists()) {
                    directory1.mkdirs();
                }

                // Actualizar la lista de carpetas
                folderNames.add(folderName);
                adapter.notifyDataSetChanged();

                // Ocultar la animación de Lottie y el texto si existen carpetas
                if (!folderNames.isEmpty()) {
                    lottieAnimationView1.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.GONE);
                }
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            builder.show();
        });

    }

    // Método auxiliar para eliminar una carpeta y su contenido de forma recursiva
    private void deleteRecursive(File fileOrDirectory) {
        Log.d("deleteRecursive", "Deleting: " + fileOrDirectory.getAbsolutePath());
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursive(child);
                }
            } else {
                Log.e("deleteRecursive", "Failed to list files in directory: " + fileOrDirectory.getAbsolutePath());
            }
        }
        boolean deleted = fileOrDirectory.delete();
        if (!deleted) {
            Log.e("deleteRecursive", "Failed to delete: " + fileOrDirectory.getAbsolutePath());
        }
    }

}