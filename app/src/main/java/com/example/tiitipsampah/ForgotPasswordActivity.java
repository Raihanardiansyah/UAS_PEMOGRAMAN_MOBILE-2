package com.example.tiitipsampah; // Sesuaikan package kamu

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail, etNewPassword;
    private Button btnResetPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Fitur Edge-to-Edge
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi View
        etForgotEmail = findViewById(R.id.etForgotEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Aksi Tombol Reset Password
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = etForgotEmail.getText().toString().trim();
                String newPassInput = etNewPassword.getText().toString().trim();

                if (emailInput.isEmpty() || newPassInput.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Semua kolom wajib diisi, Cuy!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassInput.length() < 5) {
                    Toast.makeText(ForgotPasswordActivity.this, "Password baru minimal 5 karakter!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ambil data SharedPreferences yang sudah ada
                SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
                String registeredEmail = sharedPref.getString("registered_email", "");

                // Cek apakah email yang dimasukkan sama dengan yang terdaftar
                // Kita juga bolehkan akun tester "raihan@gmail.com" biar gampang demo
                if (emailInput.equals(registeredEmail) || emailInput.equals("raihan@gmail.com")) {

                    // Update password di database lokal
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("registered_email", emailInput); // Pastikan email terkunci
                    editor.putString("registered_password", newPassInput); // Timpa dengan password baru
                    editor.apply();

                    Toast.makeText(ForgotPasswordActivity.this, "Password berhasil diperbarui, Cuy!", Toast.LENGTH_LONG).show();

                    // Lempar balik ke halaman login
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Email tidak terdaftar di aplikasi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aksi teks "Back to Login"
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}