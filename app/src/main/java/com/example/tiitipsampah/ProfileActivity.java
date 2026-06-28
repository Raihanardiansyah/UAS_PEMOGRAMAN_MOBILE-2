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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBackProfile, ivProfilePicture;
    private TextView tvProfileName;
    private TextView tvSignOut;
    private SharedPreferences sharedPref;
    private boolean isLoggingOut = false; // FLAG SAKTI: Biar onResume gak mencatat session pas logout

    private static final int PICK_IMAGE_REQUEST = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ivBackProfile = findViewById(R.id.ivBackProfile);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvSignOut = findViewById(R.id.tvSignOut);

        sharedPref = getGetSharedPreferences();

        View mainView = findViewById(R.id.main_profile);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 1. MUAT NAMA DARI PREFERENCES
        String savedName = sharedPref.getString("registered_username", "Raihan");
        tvProfileName.setText("Hi, " + savedName);

        // 2. MUAT FOTO PROFIL DARI PREFERENCES
        String encodedImage = sharedPref.getString("profile_image_base64", "");
        if (!encodedImage.isEmpty() && ivProfilePicture != null) {
            Bitmap bitmap = decodeBase64ToBitmap(encodedImage);
            if (bitmap != null) {
                ivProfilePicture.setImageBitmap(bitmap);
            }
        }

        if (ivBackProfile != null) {
            ivBackProfile.setOnClickListener(v -> finish());
        }

        if (ivProfilePicture != null) {
            ivProfilePicture.setOnClickListener(v -> openGallery());
        }

        if (tvProfileName != null) {
            tvProfileName.setOnClickListener(v -> tampilkanDialogEditNama());
        }

        // ====================================================
        // FIX LOGOUT TOTAL: LANGSUNG KE LOGINACTIVITY TANPA BALIK KE UTAMA
        // ====================================================
        if (tvSignOut != null) {
            tvSignOut.setOnClickListener(v -> {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Sign Out")
                        .setMessage("Apakah lu yakin mau keluar dari aplikasi GreenBin, Cuy? 😟")
                        .setPositiveButton("Ya, Keluar", (dialog, which) -> {

                            // KUNCI UTAMA: Set flag true agar onResume diabaikan total!
                            isLoggingOut = true;

                            // 1. SAPU BERSIH SEMUA PREFERENCES LOGIN & SESSION HALAMAN
                            sharedPref.edit()
                                    .clear() // Menggunakan clear() biar semua data akun lama musnah total
                                    .apply();

                            // 2. STRATEGI ANTI-BACK DAN SINKRONISASI: Lempar mutlak ke LoginActivity
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

                            // Hancurkan seluruh tumpukan halaman (termasuk MainActivity di background)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            Toast.makeText(ProfileActivity.this, "Berhasil Keluar Akun! 👋", Toast.LENGTH_SHORT).show();
                            finish(); // Hancurkan ProfileActivity
                        })
                        .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }
    }

    private SharedPreferences getGetSharedPreferences() {
        return getSharedPreferences("GreenBinPref", MODE_PRIVATE);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Foto Profil"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap resizedBitmap = getResizedBitmap(bitmap, 400);

                if (ivProfilePicture != null) {
                    ivProfilePicture.setImageBitmap(resizedBitmap);
                    String encodedImage = encodeBitmapToBase64(resizedBitmap);
                    sharedPref.edit().putString("profile_image_base64", encodedImage).apply();
                    Toast.makeText(this, "Foto profil berhasil disimpan permanen! 📸💾", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tampilkanDialogEditNama() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ubah Nama Pengguna");

        final EditText inputNama = new EditText(this);
        inputNama.setHint("Masukkan nama baru kamu");
        String currentName = sharedPref.getString("registered_username", "Raihan");
        inputNama.setText(currentName);
        inputNama.setSelection(inputNama.getText().length());
        builder.setView(inputNama);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String namaBaru = inputNama.getText().toString().trim();
            if (!namaBaru.isEmpty()) {
                sharedPref.edit().putString("registered_username", namaBaru).apply();
                tvProfileName.setText("Hi, " + namaBaru);
                Toast.makeText(ProfileActivity.this, "Nama berhasil diubah! 💾", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Bitmap decodeBase64ToBitmap(String input) {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    // ====================================================
    // FIX AMAN: Jangan catat session kalau user lagi logout!
    // ====================================================
    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoggingOut && sharedPref != null) {
            sharedPref.edit().putString("last_opened_activity", "profile").apply();
        }
    }
}