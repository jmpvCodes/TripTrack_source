package com.example.triptrack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.tooltip.Tooltip;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

/**
 * Esta clase es un fragmento que se utiliza para mostrar la pestaña de presupuesto.
 */
public class BudgetTabFragment extends Fragment implements ExpenseAdapter.OnDataChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    ArrayList<Expense> expenses;

    // Lista de gastos
    private TextView budgetDisplay;
    private EditText budgetEditText, expenseAmountEditText, expenseConceptEditText;
    private Button budget_button, addExpenseButton;
    private String budgetText, uuid, tripId;
    private ListView expensesListView;
    private ExpenseAdapter expenseAdapter;
    private Expense expense;
    private Context context = this.getContext();
    private File directory, tripDirectory, file, expensesFile;
    private double budget = 0.0; // Presupuesto inicial
    private ImageView reset_icon;

    /**
     * Constructor vacío requerido por la clase Fragment
     */
    public BudgetTabFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget_tab, container, false);

        // Inicializar vistas
        budget_button = rootView.findViewById(R.id.budget_button);
        budgetDisplay = rootView.findViewById(R.id.budget_display);
        budgetEditText = rootView.findViewById(R.id.budget_edittext);
        expenseAmountEditText = rootView.findViewById(R.id.expense_amount_edittext);
        expenseConceptEditText = rootView.findViewById(R.id.expense_concept_edittext);
        addExpenseButton = rootView.findViewById(R.id.add_expense_button);
        expensesListView = rootView.findViewById(R.id.expenses_listview);
        reset_icon = rootView.findViewById(R.id.reset_icon);
        expenses = new ArrayList<>();

        readBudget();

        readExpenses();

        // Configurar el evento click del botón "Establecer presupuesto"
        budgetEditText.setOnEditorActionListener((v, actionId, event) -> {
            setBudget();
            return true;
        });

        // Configurar el evento click del botón "Añadir presupuesto"
        budget_button.setOnClickListener(v -> {

            // Comprobar si el campo budget_edittext está vacío
            if (budgetEditText.getText().toString().trim().isEmpty()) {
                budget_button.setVisibility(View.VISIBLE);
                budgetEditText.setVisibility(View.VISIBLE);
                expenseAmountEditText.setVisibility(View.GONE);
                expenseConceptEditText.setVisibility(View.GONE);
                addExpenseButton.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Por favor, introduce un presupuesto.", Toast.LENGTH_SHORT).show();
            }

            // Comprobar si amountText es un número
            else if (budgetEditText.getText().toString().matches("[a-zA-Z]+")) {
                budget_button.setVisibility(View.VISIBLE);
                budgetEditText.setVisibility(View.VISIBLE);
                expenseAmountEditText.setVisibility(View.GONE);
                expenseConceptEditText.setVisibility(View.GONE);
                addExpenseButton.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Por favor, introduce un número válido para la cantidad.", Toast.LENGTH_SHORT).show();
            }

            else {
                budget_button.setVisibility(View.GONE);
                budgetEditText.setVisibility(View.GONE);
                expensesListView.setVisibility(View.GONE);
                expenseAmountEditText.setVisibility(View.VISIBLE);
                expenseConceptEditText.setVisibility(View.VISIBLE);
                addExpenseButton.setVisibility(View.VISIBLE);
                reset_icon.setVisibility(View.VISIBLE);
                setBudget();
            }

        });

        // Configurar el evento click del botón "Añadir gasto"
        addExpenseButton.setOnClickListener(v -> addExpense());

        reset_icon.setOnClickListener(v -> {
            // Mostrar una ventana de diálogo de confirmación
            new AlertDialog.Builder(requireContext())
                    .setTitle("Resetear presupuesto")
                    .setMessage("¿Desea resetear el presupuesto?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        // Eliminar el archivo del almacenamiento interno
                        budget = 0.0;  // Actualizar el presupuesto
                        // Eliminar el elemento correspondiente de tus datos
                        expenseAdapter.clear();
                        // Notificar al adaptador que los datos han cambiado
                        expenseAdapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "Presupuesto reseteado correctamente", Toast.LENGTH_SHORT).show();
                        budgetDisplay.setText(getString(R.string.budget_text, String.format(Locale.US, "%.2f", budget)));
                        // Guardar el nuevo valor del presupuesto en el archivo de texto "budget.txt"
                        assert getArguments() != null;
                        directory = new File(requireActivity().getFilesDir(), "Presupuesto");
                        tripDirectory = new File(String.valueOf(directory), tripId);
                        File file1 = new File(tripDirectory, "budget.txt");
                        if (file1.exists()){
                            file1.delete();
                        }
                        File file2 = new File(tripDirectory, "registro_gastos.txt");
                        if (file2.exists()) {
                            file2.delete();
                        }

                        // Hacer que el campo para establecer el presupuesto y el botón correspondiente vuelvan a aparecer
                        budget_button.setVisibility(View.VISIBLE);
                        budgetEditText.setVisibility(View.VISIBLE);
                        budgetEditText.setText("");  // Añadir esta línea para limpiar el campo budgetEditText
                        reset_icon.setVisibility(View.GONE);
                        expenseAmountEditText.setVisibility(View.GONE);
                        expenseConceptEditText.setVisibility(View.GONE);
                        addExpenseButton.setVisibility(View.GONE);
                        expensesListView.setVisibility(View.GONE);
                        budgetDisplay.setVisibility(View.GONE);

                        verifyAmount();

                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return rootView;
    }

    private void verifyAmount() {
         context = getActivity();
         boolean budgetFileExists = file.exists();

         // Mostrar un mensaje de advertencia si el campo budget_edittext está vacío y no existe un archivo de texto con el valor del presupuesto guardado
        if (budgetEditText.getText().toString().isEmpty() && !budgetFileExists) {
            expenseAmountEditText.setVisibility(View.GONE);
            reset_icon.setVisibility(View.GONE);
            expenseConceptEditText.setVisibility(View.GONE);
            addExpenseButton.setVisibility(View.GONE);
            assert context != null;
            Tooltip tooltip = new Tooltip.Builder(budgetEditText)
                    .setText("Establezca aquí el presupuesto que espera gastar durante el viaje")
                    .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.verde_claro))
                    .setTextColor(ContextCompat.getColor(context, R.color.white))

                    .setGravity(Gravity.BOTTOM)
                    .setCornerRadius(8f)
                    .setDismissOnClick(true)
                    .show();

            // Programar la eliminación del mensaje después de 5 segundos
            new Handler(Looper.getMainLooper()).postDelayed(tooltip::dismiss, 4000);
        }
        else if (budget == 0) {
                Toast.makeText(getContext(), "¡Has llegado al presupuesto! ", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Añade un nuevo presupuesto para continuar", Toast.LENGTH_SHORT).show();
            budget_button.setVisibility(View.VISIBLE);
                budgetEditText.setVisibility(View.VISIBLE);
                budgetDisplay.setVisibility(View.GONE);
                reset_icon.setVisibility(View.GONE);
                expenseAmountEditText.setVisibility(View.GONE);
                expenseConceptEditText.setVisibility(View.GONE);
                expensesListView.setVisibility(View.GONE);
                addExpenseButton.setVisibility(View.GONE);
            // Eliminar el archivo del almacenamiento interno
            budget = 0.0;  // Actualizar el presupuesto
            // Eliminar el elemento correspondiente de tus datos
            expenseAdapter.clear();
            // Notificar al adaptador que los datos han cambiado
            expenseAdapter.notifyDataSetChanged();
            File file1 = new File(tripDirectory, "budget.txt");
            if (file1.exists()){
                file1.delete();
            }
            File file2 = new File(tripDirectory, "registro_gastos.txt");
            if (file2.exists()) {
                file2.delete();
            }
            }
        else if (budget < 0) {
                Toast.makeText(getContext(), "¡Te has pasado del presupuesto!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Añade un nuevo presupuesto", Toast.LENGTH_SHORT).show();
            budget_button.setVisibility(View.VISIBLE);
                budgetEditText.setVisibility(View.VISIBLE);
                budgetDisplay.setVisibility(View.GONE);
                reset_icon.setVisibility(View.GONE);
                expensesListView.setVisibility(View.GONE);
                expenseAmountEditText.setVisibility(View.GONE);
                expenseConceptEditText.setVisibility(View.GONE);
                addExpenseButton.setVisibility(View.GONE);
            // Eliminar el archivo del almacenamiento interno
            budget = 0.0;  // Actualizar el presupuesto
            // Eliminar el elemento correspondiente de tus datos
            expenseAdapter.clear();
            // Notificar al adaptador que los datos han cambiado
            expenseAdapter.notifyDataSetChanged();
            File file1 = new File(tripDirectory, "budget.txt");
            if (file1.exists()){
                file1.delete();
            }
            File file2 = new File(tripDirectory, "registro_gastos.txt");
            if (file2.exists()) {
                file2.delete();
            }
            }


    }
    private void readBudget(){
        // Leer el valor del presupuesto guardado en el archivo de texto
        assert getArguments() != null;
        tripId = getArguments().getString("tripId");
        directory = new File(requireActivity().getFilesDir(), "Presupuesto")  ;
        tripDirectory = new File(directory, tripId);
        expenseAdapter = new ExpenseAdapter(getContext(), expenses, tripId);
        expensesListView.setAdapter(expenseAdapter);
        expenseAdapter.setOnDataChangeListener(this);
        file = new File(tripDirectory, "budget.txt");
        boolean budgetFileExists = file.exists();
        if (budgetFileExists) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[(int) file.length()];
                inputStream.read(buffer);
                String budgetText = new String(buffer);
                budgetDisplay.setText(getString(R.string.budget_text, budgetText));
                budgetDisplay.setVisibility(View.VISIBLE);

                // Inicializar la variable budget con el valor leído del archivo de texto
                budget = Double.parseDouble(budgetText);

                // Ejecutar el bloque de código que se ejecuta cuando se pulsa sobre el botón "Añadir presupuesto"
                budget_button.setVisibility(View.GONE);
                budgetEditText.setVisibility(View.GONE);
                expenseAmountEditText.setVisibility(View.VISIBLE);
                expenseConceptEditText.setVisibility(View.VISIBLE);
                addExpenseButton.setVisibility(View.VISIBLE);
                reset_icon.setVisibility(View.VISIBLE);  // Añadir esta línea para mostrar el icono de reinicio
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Tooltip tooltip = new Tooltip.Builder(budgetEditText)
                    .setText("Establezca aquí el presupuesto que espera gastar durante el viaje")
                    .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.verde_claro))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .setGravity(Gravity.BOTTOM)
                    .setCornerRadius(8f)
                    .setDismissOnClick(true)
                    .show();

            // Programar la eliminación del mensaje después de 5 segundos
            new Handler(Looper.getMainLooper()).postDelayed(tooltip::dismiss, 4000);
            budget_button.setVisibility(View.VISIBLE);
            budgetEditText.setVisibility(View.VISIBLE);
            expenseAmountEditText.setVisibility(View.GONE);
            expenseConceptEditText.setVisibility(View.GONE);
            addExpenseButton.setVisibility(View.GONE);
            reset_icon.setVisibility(View.GONE);
        }
    }
    private void setBudget() {

        budgetText = budgetEditText.getText().toString();

        assert getArguments() != null;
        String tripId = getArguments().getString("tripId");

        if (!budgetText.isEmpty()) {
            budget = Double.parseDouble(budgetText);
            expenseAdapter.setBudget();
            updateBudgetDisplay();
            // Hacer que budgetDisplay vuelva a ser visible
            budgetDisplay.setVisibility(View.VISIBLE);

            // Guardar el valor del presupuesto en un archivo de texto
            File directory = new File(requireActivity().getFilesDir(), "Presupuesto");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File tripDirectory = new File(directory, tripId);
            if (!tripDirectory.exists()) {
                tripDirectory.mkdir();
            }
            File file = new File(tripDirectory, "budget.txt");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(budgetText.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateBudgetDisplay() {
        // Gastos totales
        double totalExpenses = 0.0;
        for (int i = 0; i < expenseAdapter.getCount(); i++) {
            totalExpenses += expenseAdapter.getItem(i).getAmount();
        }
        budgetDisplay.setText(String.format(Locale.US, "%.2f", budget));
    }
    private void addExpense() {

        String amountText = expenseAmountEditText.getText().toString();
        String conceptText = expenseConceptEditText.getText().toString();

        // Comprobar si los campos están vacíos
        if (amountText.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, introduce una cantidad.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conceptText.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, introduce un concepto.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprobar si amountText es un número
        if (!amountText.matches("\\d+(\\.\\d+)?")) {
            Toast.makeText(getContext(), "Por favor, introduce un número válido para la cantidad.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprobar si conceptText es una cadena
        if (conceptText.matches("\\d+")) {
            Toast.makeText(getContext(), "Por favor, introduce un conepto válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        double expenseAmount = Double.parseDouble(amountText);

        expensesListView.setVisibility(View.VISIBLE);
        // Añade el gasto a la lista existente
        // Generar un UUID
        uuid = UUID.randomUUID().toString();
        expense = new Expense(uuid, expenseAmount, conceptText);
        expenses.add(expense);
        expenseAdapter.add(expense);

        // Notifica al adaptador que los datos han cambiado
        expenseAdapter.notifyDataSetChanged();

        // Actualizar el total de gastos y el presupuesto restante

        Log.d(TAG, "prebudget: " + budget);
        budget -= expenseAmount;
        updateBudgetDisplay();
        Log.d(TAG, "postbudget: " + budget);

        // Limpiar los campos de entrada de gastos
        expenseAmountEditText.setText("");
        expenseConceptEditText.setText("");

        // Guardar la lista de gastos en un archivo de texto
        saveExpenseList(amountText,conceptText);

        // Guardar el nuevo valor del presupuesto en el archivo de texto "budget.txt"
        assert getArguments() != null;
        String tripId = getArguments().getString("tripId");
        File directory = new File(requireActivity().getFilesDir(), "Presupuesto");
        File tripDirectory = new File(directory, tripId);
        File file = new File(tripDirectory, "budget.txt");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(String.valueOf(budget).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        verifyAmount();

    }
    private void readExpenses(){
        // Leer la lista de gastos guardada en el archivo de texto
        assert getArguments() != null;
        tripId = getArguments().getString("tripId");
        directory = new File(requireActivity().getFilesDir(), "Presupuesto")  ;
        tripDirectory = new File(directory, tripId);
        expensesFile = new File(tripDirectory, "registro_gastos.txt");

        if (expensesFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(expensesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    String uuid = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    String concept = parts[2];
                    expense = new Expense(uuid, amount, concept);
                    expenses.add(expense);
                    expenseAdapter.add(expense);

                    // Actualizar el adaptador de la vista ListView
                    expenseAdapter.notifyDataSetChanged();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveExpenseList(String amountText, String conceptText){
        assert getArguments() != null;
        String tripId = getArguments().getString("tripId");
        File directory = new File(requireActivity().getFilesDir(), "Presupuesto");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File tripDirectory = new File(directory, tripId);
        if (!tripDirectory.exists()) {
            tripDirectory.mkdir();
        }
        File file = new File(tripDirectory, "registro_gastos.txt");
        try (FileWriter writer = new FileWriter(file, true)) {
            // Añadir el UUID, la cantidad y el concepto al archivo
            writer.append(uuid).append(";").append(amountText).append(";").append(conceptText).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onExpenseDeleted(double amount) {

            budget += amount;
            budgetText = String.valueOf(budget);
            updateBudgetDisplay();
            verifyAmount();
            // Guardar el valor del presupuesto en un archivo de texto
            File directory = new File(requireActivity().getFilesDir(), "Presupuesto");
            if (!directory.exists()) {
                directory.mkdir();
            }
            File tripDirectory = new File(directory, tripId);
            if (!tripDirectory.exists()) {
                tripDirectory.mkdir();
            }
            File file = new File(tripDirectory, "budget.txt");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(budgetText.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}
