package com.example.tiitipsampah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView tvHelloMain;
    private ImageView ivChatIcon;
    private ImageView ivKurirDashboard;
    private ImageView ivProfileMain; // FIX: Diaktifkan untuk foto profil lingkaran di header
    private View btnTabRedeem;
    private View btnTabHistory;
    private FrameLayout btnTabProfile;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inisialisasi View
        tvHelloMain = findViewById(R.id.tvHelloMain);
        ivChatIcon = findViewById(R.id.ivChatIcon);
        btnTabProfile = findViewById(R.id.btnTabProfile);
        ivKurirDashboard = findViewById(R.id.ivKurirDashboard);
        ivProfileMain = findViewById(R.id.ivProfileMain); // FIX: Di-bind ke ID XML yang baru

        // Menjaga jarak dari tombol bawaan navigasi HP
        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // AKSI JIKA USER KLIK FOTO PROFIL KECIL DI DASHBOARD UNTUK GANTI
        if (ivProfileMain != null) {
            ivProfileMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        }

        if (ivChatIcon != null) {
            ivChatIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "Fitur Chat segera hadir, Cuy!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        LinearLayout bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            btnTabRedeem = bottomNav.getChildAt(1);
            if (btnTabRedeem != null) {
                btnTabRedeem.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, RedeemActivity.class);
                    startActivity(intent);
                });
            }

            View btnTabScan = bottomNav.getChildAt(2);
            if (btnTabScan != null) {
                btnTabScan.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    startActivity(intent);
                });
            }

            btnTabHistory = bottomNav.getChildAt(3);
            if (btnTabHistory != null) {
                btnTabHistory.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                });
            }
        }

        if (btnTabProfile != null) {
            btnTabProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    // Fungsi untuk membuka galeri HP
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), PICK_IMAGE_REQUEST);
    }

    // Menangani hasil ganti foto profil langsung dari dashboard utama
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                // Perkecil ukuran bitmap biar pas masuk SharedPreferences aman gak bikin lemot
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, (int)(400 * ((float)bitmap.getHeight()/bitmap.getWidth())), true);

                if (ivProfileMain != null) {
                    ivProfileMain.setImageBitmap(resizedBitmap);
                }

                // SIMPAN PERMANEN FORMAT BASE64 STRING
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
                String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
                sharedPref.edit().putString("profile_image_base64", encodedImage).apply();

                Toast.makeText(this, "Foto profil berhasil diperbarui permanen! 📸💾", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ====================================================
    // FIX REAL-TIME & PERMANEN: LOAD DATA KETIKA BACK KE DASHBOARD
    // ====================================================
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);

        // 1. MUAT NAMA USER TERBARU SECARA REAL-TIME
        String registeredUsername = sharedPref.getString("registered_username", "");
        if (registeredUsername == null || registeredUsername.isEmpty()) {
            tvHelloMain.setText("Hello, Raihan!☘️");
        } else {
            tvHelloMain.setText("Hello, " + registeredUsername + "!☘️");
        }

        // 2. MUAT FOTO PROFIL PERMANEN DARI BASE64 STRINGS
        String encodedImage = sharedPref.getString("profile_image_base64", "");
        if (!encodedImage.isEmpty() && ivProfileMain != null) {
            byte[] decodedByte = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
            if (bitmap != null) {
                ivProfileMain.setImageBitmap(bitmap);
            }
        }

        // 3. MUAT INFO & GAMBAR SUPIR TERPILIH DARI HALAMAN SCAN SAMPAH
        String kurirAktif = sharedPref.getString("kurir_aktif", "Belum ada penjemputan");
        String fotoKurir = sharedPref.getString("foto_kurir_aktif", "ic_menu_gallery");
        TextView tvKurirDashboard = findViewById(R.id.tvInfoKurirUtama);

        if (tvKurirDashboard != null) {
            if (kurirAktif.equals("Belum ada penjemputan")) {
                tvKurirDashboard.setText("Status: " + kurirAktif + " 💤");
                if (ivKurirDashboard != null) {
                    ivKurirDashboard.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                tvKurirDashboard.setText("Kurir Menuju Lokasi:\n" + kurirAktif + " 🏃💨\nEstimasi: 5 Menit lagi.");

                if (ivKurirDashboard != null) {
                    int resId = getResources().getIdentifier(fotoKurir, "drawable", getPackageName());
                    if (resId != 0) {
                        ivKurirDashboard.setImageResource(resId);
                    } else {
                        ivKurirDashboard.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                }
            }
        }

        // ====================================================
        // FIX SESSION PERSISTENCE: CATAT SEBAGAI HALAMAN AKTIF
        // ====================================================
        sharedPref.edit().putString("last_opened_activity", "main").apply();
    }
}