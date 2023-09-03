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

public class ExpenseCalculationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_calculation);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        String tripId = getIntent().getStringExtra("tripId");

// Configurar el TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Presupuesto"));
        tabLayout.addTab(tabLayout.newTab().setText("Facturas"));

        ViewPager viewPager = findViewById(R.id.view_pager);

// Crear un adaptador para el ViewPager
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    BudgetTabFragment budgetTabFragment = new BudgetTabFragment();
                    Bundle args = new Bundle();
                    args.putString("tripId", tripId);
                    budgetTabFragment.setArguments(args);
                    return budgetTabFragment;
                } else {
                    return new InvoiceTabFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

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
        viewPager.setAdapter(adapter);

// Conectar el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);
    }
    }