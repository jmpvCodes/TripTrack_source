package com.example.triptrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.List;

/**
 * Esta clase es un adaptador que se utiliza para mostrar una lista de documentos.
 * El adaptador se utiliza en la actividad DocumentActivity para mostrar una lista de documentos.
 */
public class DocumentAdapter extends ArrayAdapter<Document> {

    /**
     * Constructor de la clase DocumentAdapter.
     *
     * @param context   el contexto en el que se utiliza el adaptador
     * @param documents la lista de documentos que se mostrarán
     */
    public DocumentAdapter(Context context, List<Document> documents) {

        super(context, 0, documents);

    }

    /**
     * Método que devuelve la vista que se muestra en la posición especificada de la lista.
     *
     * @param position    la posición de la vista que se va a devolver
     * @param convertView la vista que se va a convertir
     * @param parent      el ViewGroup en el que se va a inflar la vista
     * @return la vista que se muestra en la posición especificada de la lista
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Document document = getItem(position);

        // Comprobar si la vista se está reutilizando, de lo contrario, inflar la vista
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
        }

        /*
            * Obtener las vistas que se van a actualizar
            * En este caso, las vistas son el nombre del documento, el icono de eliminar y el icono de editar
            * El nombre del documento se establece en el TextView documentNameTextView
         */
        TextView documentNameTextView = convertView.findViewById(R.id.document_name_text_view);
        documentNameTextView.setText(document.getName());
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);
        ImageView editIcon = convertView.findViewById(R.id.edit_icon);

        // Establecer un clic largo en la vista para mostrar una ventana de diálogo de opciones de eliminación
        deleteIcon.setOnClickListener(v -> {
            // Mostrar una ventana de diálogo de confirmación
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar documento")
                    .setMessage("¿Desea eliminar este documento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Obtener el documento correspondiente
                        Document document1 = getItem(position);
                        // Eliminar el archivo del almacenamiento interno
                        File file = new File(document1.getPath());
                        boolean deleted = file.delete();
                        if (deleted) {
                            // Eliminar el elemento correspondiente de tus datos
                            remove(document1);
                            // Notificar al adaptador que los datos han cambiado
                            notifyDataSetChanged();
                            Toast.makeText(getContext(), "Documento eliminado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al eliminar el documento", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Establecer un clic largo en la vista para mostrar una ventana de diálogo para editar el nombre del archivo
        editIcon.setOnClickListener(view -> {
            // Obtener el documento correspondiente
            Document document12 = getItem(position);
            // Mostrar una ventana de diálogo para editar el nombre del archivo
            final EditText input = new EditText(getContext());
            // Obtener el nombre del archivo sin la extensión
            String documentName = document12.getName();
            int lastDotIndex = documentName.lastIndexOf(".");
            if (lastDotIndex != -1) {
                documentName = documentName.substring(0, lastDotIndex);
            }
            // Establecer el nombre del archivo en el EditText
            input.setText(documentName);
            new AlertDialog.Builder(getContext())
                    .setTitle("Editar nombre del archivo")
                    .setView(input)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        // Obtener el nuevo nombre del archivo
                        String newName = input.getText().toString() + ".jpg";
                        // Renombrar el archivo en el almacenamiento interno
                        File oldFile = new File(document12.getPath());
                        File newFile = new File(oldFile.getParent(), newName);
                        boolean renamed = oldFile.renameTo(newFile);
                        if (renamed) {
                            // Actualizar el documento en tus datos
                            document12.setName(newName);
                            document12.setPath(newFile.getAbsolutePath());
                            // Notificar al adaptador que los datos han cambiado
                            notifyDataSetChanged();
                            Toast.makeText(getContext(), "El documento ha sido editado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Error al editar el documento", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        return convertView;
    }
}

