package com.example.tiitipsampah; // Sesuaikan dengan nama package kamu jika berbeda

import android.content.Intent;
import android.content.SharedPreferences; // Import baru untuk database lokal
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    // Kita pakai etUsername sesuai dengan ID di XML mockup terbaru
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Fitur Edge-to-Edge biar aman dari sistem cut-out layar
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi Komponen UI (Sudah disamakan dengan ID di XML baru)
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Aksi tombol Daftar
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                // Validasi input kosong
                if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Semua kolom wajib diisi, Cuy!", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 5) { // Kita samakan minimal 5 karakter sesuai logika login kamu kemarin
                    Toast.makeText(RegisterActivity.this, "Password minimal 5 karakter!", Toast.LENGTH_SHORT).show();
                } else {

                    // ====================================================
                    // PROSES JURUS NYIMPEN DATA LOKAL (SharedPreferences)
                    // ====================================================
                    SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    // Kita simpan datanya ke dalam file internal HP
                    editor.putString("registered_username", username);
                    editor.putString("registered_email", email);
                    editor.putString("registered_password", pass);
                    editor.apply(); // Data sah tersimpan!

                    Toast.makeText(RegisterActivity.this, "Akun berhasil disimpan di HP!", Toast.LENGTH_LONG).show();

                    // Balik ke LoginActivity setelah sukses daftar
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Tutup halaman register biar tidak menumpuk di stack
                }
            }
        });
    }
}