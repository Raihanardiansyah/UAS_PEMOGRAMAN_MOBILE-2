package com.example.tiitipsampah;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge; // Tambah import ini
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets; // Tambah import ini
import androidx.core.view.ViewCompat; // Tambah import ini
import androidx.core.view.WindowInsetsCompat; // Tambah import ini
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView ivBackHistory; // Variabel baru untuk tombol back

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Aktifkan EdgeToEdge biar sinkron
        setContentView(R.layout.activity_history);

        tabLayout = findViewById(R.id.tabLayoutHistory);
        viewPager = findViewById(R.id.viewPagerHistory);
        ivBackHistory = findViewById(R.id.ivBackHistory);

        // ====================================================
        // FIX TERLALU ATAS: NGASIH JARAK AMAN DARI STATUS BAR HP
        // ====================================================
        View mainLayout = findViewById(R.id.main_history);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // systemBars.top bakal otomatis ngitung tinggi status bar HP kamu biar ga kejedot
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // AKSI TOMBOL KEMBALI
        if (ivBackHistory != null) {
            ivBackHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Menutup halaman history dan balik ke dashboard
                }
            });
        }

        // Pasang Adapter ke ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return HistoryListFragment.newInstance(position);
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // Hubungkan Tab dengan Judul Kategori
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0) {
                    tab.setText("Penarikan Poin");
                } else {
                    tab.setText("Penjemputan Sampah");
                }
            }
        }).attach();
    }
}