package com.example.triptrack;

import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

public class AlbumPhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        String tripId = getIntent().getStringExtra("tripId");


        // Configurar el TabLayout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Galería"));
        tabLayout.addTab(tabLayout.newTab().setText("Álbum"));

        ViewPager viewPager = findViewById(R.id.view_pager);

    // Crear un adaptador para el ViewPager
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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Galeria";
            } else {
                return "Álbum";
            }
        }
    };
        viewPager.setAdapter(adapter);

// Conectar el TabLayout con el ViewPager
        tabLayout.setupWithViewPager(viewPager);
}
    }

