package com.example.tiitipsampah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 1. Membuat Splash Screen jadi Full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 2. Sembunyikan Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 3. Logika Perpindahan Halaman dengan Delay 5600ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Buka data preferences untuk cek halaman terakhir
                SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
                String lastActivity = sharedPref.getString("last_opened_activity", "main");

                if (lastActivity.equals("profile")) {
                    // KONDISI KHUSUS: Buka MainActivity di background agar back stack aman
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    // Langsung tumpuk atasnya pake ProfileActivity secara instan
                    Intent profileIntent = new Intent(SplashActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                } else {
                    // KONDISI DEFAULT: Pindah ke LocationCheckActivity dulu sesuai alur aslimu, Cuy
                    Intent intent = new Intent(SplashActivity.this, LocationCheckActivity.class);
                    startActivity(intent);
                }

                // Tutup activity splash agar tidak bisa di-back ke loading lagi
                finish();
            }
        }, 5600);
    }
}