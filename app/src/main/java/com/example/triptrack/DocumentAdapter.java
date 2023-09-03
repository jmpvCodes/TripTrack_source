package com.example.triptrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.List;

public class DocumentAdapter extends ArrayAdapter<Document> {
    public DocumentAdapter(Context context, List<Document> documents) {

        super(context, 0, documents);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Document document = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
        }

        TextView documentNameTextView = convertView.findViewById(R.id.document_name_text_view);
        documentNameTextView.setText(document.getName());
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);
        ImageView editIcon = convertView.findViewById(R.id.edit_icon);
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
                        String newName = input.getText().toString();
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

