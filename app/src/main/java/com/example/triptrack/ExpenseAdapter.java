package com.example.triptrack;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta clase es un adaptador que se utiliza para mostrar una lista de documentos.
 * El adaptador se utiliza en la actividad DocumentActivity para mostrar una lista de documentos.
 */
public class ExpenseAdapter extends ArrayAdapter<Expense> {

    private OnDataChangeListener onDataChangeListener;
    private final File filesDir, tripDirectory;
    private final List<Expense> expenses;


    /**
     * Constructor de la clase ExpenseAdapter.
     * @param context contexto de la aplicación
     * @param expenses lista de gastos
     * @param tripId ID del viaje
     */
    public ExpenseAdapter(Context context, List<Expense> expenses, String tripId) {
        super(context, 0);
        this.filesDir = context.getFilesDir();
        this.expenses = expenses;

        // Se usa filesDir para crear tripDirectory
        File presupuestoDirectory = new File(filesDir, "Presupuesto");
        this.tripDirectory = new File(presupuestoDirectory, tripId);
    }
    /**
     * Establece el presupuesto del viaje.
     */
    public void setBudget() {
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Expense expense = expenses.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list, parent, false);
            ImageView editIcon = convertView.findViewById(R.id.edit_icon);
            editIcon.setVisibility(View.GONE);
        }

        TextView documentNameTextView = convertView.findViewById(R.id.document_name_text_view);
        documentNameTextView.setText(expense.getConcept() + ": " + String.format(Locale.US, "%.2f", expense.getAmount()));
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);

        deleteIcon.setOnClickListener(v -> {

            // Eliminar el gasto del adaptador
            remove(expense);

            // Notificar al adaptador que los datos han cambiado
            notifyDataSetChanged();

            // Obtener los datos del gasto
            String id = expense.getUid();

            // Leer todos los gastos del archivo
            List<String> expenses = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(new File(tripDirectory, "registro_gastos.txt")))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    expenses.add(line);
                    Log.d(TAG, "linea: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Buscar el gasto con el ID correspondiente y eliminarlo de la lista
            Iterator<String> iterator = expenses.iterator();
            while (iterator.hasNext()) {
                String expenseLine = iterator.next();
                if (expenseLine.startsWith(id + ";")) {
                    iterator.remove();
                    break;
                }
            }
            // Llamar a onExpenseDeleted() para notificar que el gasto ha sido eliminado
            if(onDataChangeListener != null){
                onDataChangeListener.onExpenseDeleted(expense.getAmount());
            }

            // Reescribir la lista en el archivo
            try (FileWriter writer = new FileWriter(new File(tripDirectory, "registro_gastos.txt"), false)) {  // false para sobrescribir el archivo
                for (String expenseLine : expenses) {
                    writer.append(expenseLine).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Gasto eliminado correctamente", Toast.LENGTH_SHORT).show();
        });


        return convertView;
    }

    /**
     * Interfaz que se utiliza para notificar que un gasto ha sido eliminado.
     */
    public interface OnDataChangeListener{

        /**
         * Método que se llama cuando un gasto ha sido eliminado.
         * @param amount cantidad del gasto eliminado
         */
        void onExpenseDeleted(double amount);
    }

    /**
     * Método que establece el OnDataChangeListener.
     * @param onDataChangeListener OnDataChangeListener
     */
    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        this.onDataChangeListener = onDataChangeListener;
    }


}

