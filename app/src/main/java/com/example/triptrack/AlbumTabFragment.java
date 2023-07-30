package com.example.triptrack;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlbumTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumTabFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener la lista de carpetas en la carpeta tripId
        String tripId = getActivity().getIntent().getStringExtra("tripId");
        File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId);
        File[] folders = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        List<String> folderNames = new ArrayList<>();
        for (File folder : folders) {
            folderNames.add(folder.getName());
        }

        // Mostrar la lista de carpetas en un ListView
        ListView listView = view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, folderNames);
        listView.setAdapter(adapter);


        // Reproducir la animación de Lottie
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();


        // Mostrar un menú contextual cuando se hace clic en el botón "Crear nueva carpeta"
        Button createFolderButton = view.findViewById(R.id.createFolderButton);
        createFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Crear nueva carpeta");

                // Configurar el campo de entrada de texto para el nombre de la carpeta
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Configurar los botones "Aceptar" y "Cancelar"
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acciones a realizar cuando se hace clic en "Aceptar"
                        String folderName = input.getText().toString();
                        String tripId = getActivity().getIntent().getStringExtra("tripId");

                        // Crear una nueva carpeta
                        File directory = new File(getActivity().getFilesDir(), "Galería/" + tripId + "/" + folderName);
                        if (!directory.exists()) {
                            directory.mkdirs();
                        }

                        // Ocultar la animación de Lottie y el texto
                        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
                        lottieAnimationView.setVisibility(View.GONE);
                        TextView messageTextView = view.findViewById(R.id.messageTextView);
                        messageTextView.setVisibility(View.GONE);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

}