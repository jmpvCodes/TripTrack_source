package com.example.triptrack;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Esta clase representa la pantalla de cálculo de gastos. 
 * Permite al usuario ver el presupuesto del viaje y añadir facturas, tickets y otros gastos.
 * Los gastos se pueden añadir manualmente o importar desde el almacenamiento interno de la aplicación.
 */
public class ExpenseCalculationActivity extends AppCompatActivity {

    /**
     * Método que se llama cuando se crea la actividad.
     * @param savedInstanceState estado de la instancia guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_calculation);

       // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());// Generar la barra de herramientas

        String tripId = getIntent().getStringExtra("tripId"); // Obtener el ID del viaje

        // Configurar el TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Presupuesto")); // Añadir una pestaña para el presupuesto
        tabLayout.addTab(tabLayout.newTab().setText("Facturas")); // Añadir una pestaña para las facturas

        ViewPager viewPager = findViewById(R.id.view_pager); // Obtener referencia al ViewPager

        // Crear un adaptador para el ViewPager
        // El adaptador se encarga de mostrar los fragmentos en el ViewPager
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            /**
             * Método que se llama cuando se crea un fragmento.
             * @param position posición del fragmento
             * @return fragmento creado
             */
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) { // Si la posición es 0, mostrar el fragmento del presupuesto
                    BudgetTabFragment budgetTabFragment = new BudgetTabFragment();
                    Bundle args = new Bundle();
                    args.putString("tripId", tripId);
                    budgetTabFragment.setArguments(args);
                    return budgetTabFragment;
                } else { // Si la posición es 1, mostrar el fragmento de las facturas
                    return new InvoiceTabFragment();
                }
            }

            /**
             * Método que se llama para obtener el número de fragmentos.
             * @return número de fragmentos
             */
            @Override
            public int getCount() {
                return 2;
            }

            /**
             * Método que se llama para obtener el título de un fragmento.
             * @param position posición del fragmento
             * @return título del fragmento
             */
            @NotNull
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "Presupuesto";
                } else {
                    return "Facturas y tickets";
                }
            }
        };
        viewPager.setAdapter(adapter); // Establecer el adaptador para el ViewPager

        // Conectar el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }
    }