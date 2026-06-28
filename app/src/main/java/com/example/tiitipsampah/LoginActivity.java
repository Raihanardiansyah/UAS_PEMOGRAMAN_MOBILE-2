package com.example.tiitipsampah;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ====================================================
        // CEK SESI LOGIN (FITUR KEEP LOGIN)
        // ====================================================
        SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            // Jika sudah login sebelumnya, langsung lempar ke MainActivity tanpa lewat halaman login
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Tutup LoginActivity biar gak bisa di-back
            return; // Stop proses onCreate ke bawah
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Setting Edge-to-Edge biar gak nabrak status bar HP
        View mainView = findViewById(R.id.main_login);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Aksi Tombol Login
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etEmail == null || etPassword == null) return;

                    String email = etEmail.getText().toString().trim();
                    String pass = etPassword.getText().toString().trim();

                    if (email.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Isi dulu dong email & pass-nya!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String registeredEmail = sharedPref.getString("registered_email", "");
                    String registeredPassword = sharedPref.getString("registered_password", "");

                    // Logika pencocokan data
                    if ((email.equals("raihan@gmail.com") && pass.equals("12345")) ||
                            (email.equals(registeredEmail) && pass.equals(registeredPassword))) {

                        Toast.makeText(LoginActivity.this, "Login Berhasil, Cuy!", Toast.LENGTH_SHORT).show();

                        // SIMPAN STATUS LOGIN BIAR GAUSAH LOGIN LAGI
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Email/Password salah atau belum terdaftar!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Aksi Teks "Register"
        if (tvToRegister != null) {
            tvToRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Aksi Teks "Forgot Password?"
        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}