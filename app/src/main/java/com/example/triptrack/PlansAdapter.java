package com.example.triptrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class PlansAdapter extends ArrayAdapter<Plans> {
    private final TravelAgendaActivity activity;

    public PlansAdapter(TravelAgendaActivity activity, ArrayList<Plans> plans) {
        super(activity, 0, plans);
        this.activity = activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_plan, parent, false);
        }

        TextView timeTextView = convertView.findViewById(R.id.timeTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView editIcon = convertView.findViewById(R.id.edit_icon);
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);
        View priorityCircleView = convertView.findViewById(R.id.priorityCircleView);

        Plans plan = getItem(position);
        timeTextView.setText(plan.getTime());
        locationTextView.setText(plan.getLocation());
        descriptionTextView.setText(plan.getDescription());

        // Cambiar el color del círculo según la prioridad del plan
        if (plan.getPriority() == 1) {
            priorityCircleView.setBackgroundResource(R.drawable.red_dot);
        } else if (plan.getPriority() == 2) {
            priorityCircleView.setBackgroundResource(R.drawable.yellow_dot);
        } else if (plan.getPriority() == 3) {
            priorityCircleView.setBackgroundResource(R.drawable.green_dot);
        }

        editIcon.setOnClickListener(view -> {
            // Obtener el plan asociado con este icono
            Plans planToEdit = getItem(position);

            // Crear un cuadro de diálogo para editar el plan
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Editar plan");

            // Crear una vista personalizada para el cuadro de diálogo
            View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_edit_plan, null);
            builder.setView(dialogView);

            // Obtener las vistas de los campos de entrada
            TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
            EditText locationEditText = dialogView.findViewById(R.id.locationEditText);
            EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);
            Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                    R.array.priority_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            prioritySpinner.setAdapter(adapter);

            // Establecer los valores iniciales de los campos de entrada
            String[] timeParts = planToEdit.getTime().split(":");
            timePicker.setHour(Integer.parseInt(timeParts[0]));
            timePicker.setMinute(Integer.parseInt(timeParts[1]));
            locationEditText.setText(planToEdit.getLocation());
            descriptionEditText.setText(planToEdit.getDescription());
            prioritySpinner.setSelection(planToEdit.getPriority() - 1);

            // Agregar botones para cancelar y guardar
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.setPositiveButton("Guardar", (dialog, which) -> {
                // Obtener los nuevos valores de los campos de entrada
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String time = String.format("%02d:%02d", hour, minute);
                String location = locationEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                int priority = prioritySpinner.getSelectedItemPosition() + 1;

                // Actualizar el plan con los nuevos valores
                planToEdit.setTime(time);
                planToEdit.setLocation(location);
                planToEdit.setDescription(description);
                planToEdit.setPriority(priority);

                // Guardar los cambios y actualizar la lista de planes programados
                activity.savePlans();
                activity.updateScheduledPlansList();
            });

            // Mostrar el cuadro de diálogo
            builder.show();
        });


        deleteIcon.setOnClickListener(view -> {
            // Obtener el plan asociado con este icono
            Plans planToDelete = getItem(position);

            // Mostrar un cuadro de diálogo de confirmación para eliminar el plan
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Eliminar plan");
            builder.setMessage("¿Estás seguro de que deseas eliminar este plan?");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                // Llamar al método deletePlan() para eliminar el plan
                activity.deletePlan(planToDelete);

                // Mostrar un mensaje de Toast
                Toast.makeText(activity, "Plan eliminado correctamente", Toast.LENGTH_SHORT).show();
            });


            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        return convertView;
    }
}

