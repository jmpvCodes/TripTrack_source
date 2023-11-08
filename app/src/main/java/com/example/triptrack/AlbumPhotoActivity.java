package com.example.triptrack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Esta actividad muestra un álbum de fotos para un viaje específico.
 * La actividad tiene una barra de herramientas en la parte superior y una barra de navegación en la parte inferior.
 * En el medio, hay un TabLayout con dos pestañas: "Galeria" y "Album".
 * Cada pestaña muestra un fragmento diferente.
 */

public class AlbumPhotoActivity extends AppCompatActivity {

    /**
     * Este método se llama cuando se crea la actividad.
     * Configura la barra de herramientas, el botón de retroceso, el TabLayout, el ViewPager y la barra de navegación.
     *
     * @param savedInstanceState Si la actividad se reinicia después de una pausa, este parámetro contiene los datos más recientes.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo);

        // Configurar la barra de herramientas
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Configurar el botón de retroceso
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); 

        // Obtener el ID del viaje
        String tripId = getIntent().getStringExtra("tripId");

        // Configurar el TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Galería"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));

        // Configurar el ViewPager
        ViewPager viewPager = findViewById(R.id.view_pager);

        // Crear un FragmentPagerAdapter que muestre el fragmento correcto para cada pestaña
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    PhotoTabFragment photoTabFragment = new PhotoTabFragment();
                    Bundle args = new Bundle();
                    args.putString("tripId", tripId);
                    photoTabFragment.setArguments(args);
                    return photoTabFragment;
                } else {
                    return new AlbumTabFragment();
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
                    return "Galeria";
                } else {
                    return "Album";
                }
            }
        };

        // Configurar el ViewPager para que se actualice cuando el TabLayout cambie de pestaña
        viewPager.setAdapter(adapter);

        // Configurar el TabLayout para que se actualice cuando el ViewPager cambie de página
        tabLayout.setupWithViewPager(viewPager);

        // Generar la barra de navegación
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(
                (BottomNavigationView.OnNavigationItemSelectedListener) item -> {
                    // Obtener el ID del item seleccionado
                    int itemId = item.getItemId();

                    // Realizar acciones basadas en el item seleccionado
                    if (itemId == R.id.nav_my_trips) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_world) {
                        // Acción para la pestaña "Buscar"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent searchIntent = new Intent(this, MapamundiActivity.class);
                        startActivity(searchIntent);
                        return true;
                    } else if (itemId == R.id.bottom_nav_profile) {
                        // Acción para la pestaña "Perfil"
                        // Ejemplo: iniciar la actividad correspondiente
                        Intent profileIntent = new Intent(this, ProfileActivity.class);
                        startActivity(profileIntent);
                        return true;
                    }

                    return false;
                });

    }

}
