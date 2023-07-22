package com.example.triptrack;

import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.tooltip.Tooltip;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    private ListView expensesListView;

    private double budget = 0.0; // Presupuesto inicial
    private double totalExpenses = 0.0; // Gastos totales

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


        // Mostrar un mensaje de advertencia si el campo budget_edittext está vacío
        if (budgetEditText.getText().toString().isEmpty()) {
            expenseAmountEditText.setVisibility(View.GONE);
            expenseConceptEditText.setVisibility(View.GONE);
            addExpenseButton.setVisibility(View.GONE);
            Tooltip tooltip = new Tooltip.Builder(budgetEditText)
                    .setText("Establezca aquí el presupuesto que espera gastar durante el viaje")
                    .setBackgroundColor(getResources().getColor(R.color.verde_claro))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setGravity(Gravity.BOTTOM)
                    .setCornerRadius(8f)
                    .setDismissOnClick(true)
                    .show();

            // Programar la eliminación del mensaje después de 5 segundos
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tooltip.dismiss();
                }
            }, 5000);
        }

        // Configurar el adaptador para la vista ListView
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, expenses);
        expensesListView.setAdapter(adapter);

        // Configurar el evento click del botón "Establecer presupuesto"
        budgetEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                setBudget();
                return true;
            }
        });

        // Configurar el evento click del botón "Añadir presupuesto"
        budget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budget_button.setVisibility(View.GONE);
                budgetEditText.setVisibility(View.GONE);
                budgetDisplay.setText(budgetEditText.getText().toString() + "€");
                budgetDisplay.setVisibility(View.VISIBLE);
                expenseAmountEditText.setVisibility(View.VISIBLE);
                expenseConceptEditText.setVisibility(View.VISIBLE);
                addExpenseButton.setVisibility(View.VISIBLE);

                setBudget();
            }
        });

        // Configurar el evento click del botón "Añadir gasto"
        addExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        return rootView;
    }

    private void setBudget() {
        String budgetText = budgetEditText.getText().toString();
        if (!budgetText.isEmpty()) {
            budget = Double.parseDouble(budgetText);
            updateBudgetDisplay();
            budgetEditText.setText("");
        }
    }

    private void updateBudgetDisplay() {
        budgetDisplay.setText(String.format("%.2f", budget - totalExpenses));
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
            totalExpenses += expenseAmount;
            updateBudgetDisplay();

            // Limpiar los campos de entrada de gastos
            expenseAmountEditText.setText("");
            expenseConceptEditText.setText("");
        }
    }

    // Clase para representar un gasto
    private class Expense {
        double amount;
        String concept;

        public Expense(double amount, String concept) {
            this.amount = amount;
            this.concept = concept;
        }

        @Override
        public String toString() {
            return concept + ": " + String.format("%.2f", amount);
        }
    }
}