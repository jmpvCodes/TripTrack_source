package com.example.triptrack;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.tooltip.Tooltip;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetTabFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetTabFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private final ArrayList<Expense> expenses = new ArrayList<>(); // Lista de gastos
    private ArrayAdapter<Expense> adapter; // Adaptador para la vista ListView

    private TextView budgetDisplay;
    private EditText budgetEditText;
    private EditText expenseAmountEditText;
    private EditText expenseConceptEditText;
    private Button budget_button;
    private Button addExpenseButton;
    private double budget = 0.0; // Presupuesto inicial

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
        ListView expensesListView = rootView.findViewById(R.id.expenses_listview);
        Context context = getActivity();

        // Leer el valor del presupuesto guardado en el archivo de texto
        assert getArguments() != null;
        String tripId = getArguments().getString("tripId");
        File directory = new File(requireActivity().getFilesDir(), "Presupuesto");
        File tripDirectory = new File(directory, tripId);
        File file = new File(tripDirectory, "budget.txt");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Leer la lista de gastos guardada en el archivo de texto
        File expensesFile = new File(tripDirectory, "registro_gastos.txt");
        if (expensesFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(expensesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    double amount = Double.parseDouble(parts[0]);
                    String concept = parts[1];
                    Expense expense = new Expense(amount, concept);
                    expenses.add(expense);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Mostrar un mensaje de advertencia si el campo budget_edittext está vacío y no existe un archivo de texto con el valor del presupuesto guardado
        if (budgetEditText.getText().toString().isEmpty() && !budgetFileExists) {
            expenseAmountEditText.setVisibility(View.GONE);
            expenseConceptEditText.setVisibility(View.GONE);
            addExpenseButton.setVisibility(View.GONE);
            assert context != null;
            Tooltip tooltip = new Tooltip.Builder(budgetEditText)
                    .setText("Establezca aquí el presupuesto que espera gastar durante el viaje")
                    .setBackgroundColor(ContextCompat.getColor(context, R.color.verde_claro))
                    .setTextColor(ContextCompat.getColor(context, R.color.white))

                    .setGravity(Gravity.BOTTOM)
                    .setCornerRadius(8f)
                    .setDismissOnClick(true)
                    .show();

            // Programar la eliminación del mensaje después de 5 segundos
            new Handler(Looper.getMainLooper()).postDelayed(tooltip::dismiss, 5000);
        }

        // Configurar el adaptador para la vista ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, expenses);
        expensesListView.setAdapter(adapter);

        // Configurar el evento click del botón "Establecer presupuesto"
        budgetEditText.setOnEditorActionListener((v, actionId, event) -> {
            setBudget();
            return true;
        });

        // Configurar el evento click del botón "Añadir presupuesto"
        budget_button.setOnClickListener(v -> {
            budget_button.setVisibility(View.GONE);
            budgetEditText.setVisibility(View.GONE);
            budgetDisplay.setText(getString(R.string.budget_text, budgetEditText.getText().toString()));
            budgetDisplay.setVisibility(View.VISIBLE);
            expenseAmountEditText.setVisibility(View.VISIBLE);
            expenseConceptEditText.setVisibility(View.VISIBLE);
            addExpenseButton.setVisibility(View.VISIBLE);

            setBudget();
        });

        // Configurar el evento click del botón "Añadir gasto"
        addExpenseButton.setOnClickListener(v -> addExpense());

        return rootView;
    }
    private void setBudget() {

        String budgetText = budgetEditText.getText().toString();
        assert getArguments() != null;
        String tripId = getArguments().getString("tripId");

        if (!budgetText.isEmpty()) {
            budget = Double.parseDouble(budgetText);
            updateBudgetDisplay();

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
        budgetDisplay.setText(String.format(Locale.US, "%.2f", budget - totalExpenses));
    }

    private void addExpense() {
        String amountText = expenseAmountEditText.getText().toString();
        String conceptText = expenseConceptEditText.getText().toString();

        if (!amountText.isEmpty() && !conceptText.isEmpty()) {

            double expenseAmount = Double.parseDouble(amountText);

            // Crear un nuevo objeto Expense y agregarlo a la lista de gastos
            Expense expense = new Expense(expenseAmount, conceptText);
            expenses.add(expense);

            // Actualizar el adaptador de la vista ListView
            adapter.notifyDataSetChanged();

            // Actualizar el total de gastos y el presupuesto restante

            budget -= expenseAmount;
            updateBudgetDisplay();

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
            writer.append(amountText).append(";").append(conceptText).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Clase para representar un gasto
    private static class Expense {
        double amount;
        String concept;

        public Expense(double amount, String concept) {
            this.amount = amount;
            this.concept = concept;
        }

        @NotNull
        @Override
        public String toString() {
            return concept + ": " + String.format(Locale.US, "%.2f", amount);
        }
    }

}
