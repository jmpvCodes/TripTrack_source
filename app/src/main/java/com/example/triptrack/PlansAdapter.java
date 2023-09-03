package com.example.triptrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class PlansAdapter extends ArrayAdapter<Plans> {
    private final TravelAgendaActivity activity;

    public PlansAdapter(TravelAgendaActivity activity, ArrayList<Plans> plans) {
        super(activity, 0, plans);
        this.activity = activity;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Plans plan = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_plan, parent, false);
        }

        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView editIcon = convertView.findViewById(R.id.edit_icon);
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);

        timeTextView.setText(plan.getTime());
        locationTextView.setText(plan.getLocation());
        descriptionTextView.setText(plan.getDescription());

        editIcon.setOnClickListener(view -> {});

        deleteIcon.setOnClickListener(view -> {
            // Obtener el plan asociado con este icono
            Plans plan1 = (Plans) view.getTag();

            // Mostrar un cuadro de diálogo de confirmación para eliminar el plan
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Eliminar plan");
            builder.setMessage("¿Estás seguro de que deseas eliminar este plan?");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                // Llamar al método deletePlan() para eliminar el plan
                activity.deletePlan(plan1);
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        return convertView;
    }

}
