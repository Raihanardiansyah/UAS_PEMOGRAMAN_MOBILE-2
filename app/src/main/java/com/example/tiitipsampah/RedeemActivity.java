package com.example.tiitipsampah;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RedeemActivity extends AppCompatActivity {

    private ImageView ivBackRedeem;
    private TextView tvCurrentPoin;
    private CardView btnRedeemGopay, btnRedeemOvo, btnRedeemDana, btnRedeemToken;
    private int userPoin = 1250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_redeem);

        ivBackRedeem = findViewById(R.id.ivBackRedeem);
        tvCurrentPoin = findViewById(R.id.tvCurrentPoin);
        btnRedeemGopay = findViewById(R.id.btnRedeemGopay);
        btnRedeemOvo = findViewById(R.id.btnRedeemOvo);
        btnRedeemDana = findViewById(R.id.btnRedeemDana);
        btnRedeemToken = findViewById(R.id.btnRedeemToken);

        View mainView = findViewById(R.id.main_redeem);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        if (ivBackRedeem != null) {
            ivBackRedeem.setOnClickListener(v -> finish());
        }

        btnRedeemGopay.setOnClickListener(v -> prosesPenukaran(500, "Saldo GOPAY Rp 50.000"));
        btnRedeemOvo.setOnClickListener(v -> prosesPenukaran(500, "Saldo OVO Rp 50.000"));
        btnRedeemDana.setOnClickListener(v -> prosesPenukaran(1000, "Saldo DANA Rp 100.000"));
        btnRedeemToken.setOnClickListener(v -> prosesPenukaran(200, "Voucher Token Listrik"));
    }

    private void prosesPenukaran(int biayaPoin, String namaHadiah) {
        if (userPoin >= biayaPoin) {
            userPoin -= biayaPoin;
            tvCurrentPoin.setText(userPoin + " XP");
            Toast.makeText(this, "Sukses Menukarkan " + namaHadiah + "!", Toast.LENGTH_LONG).show();

            // SIMPAN KE HISTORY REALTIME
            simpanKeHistoryPoin(namaHadiah, "-" + biayaPoin + " XP");
        } else {
            Toast.makeText(this, "Maaf, Poin kamu tidak mencukupi, Cuy!", Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanKeHistoryPoin(String judul, String poin) {
        SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
        Gson gson = new Gson();

        // Ambil list lama
        String jsonStr = sharedPref.getString("list_history_poin", "");
        ArrayList<HistoryModel> listHistory;

        if (jsonStr.isEmpty()) {
            listHistory = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            listHistory = gson.fromJson(jsonStr, type);
        }

        // Ambil tanggal hari ini otomatis
        String tanggalHariIni = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());

        // Tambah data baru ke paling atas list
        listHistory.add(0, new HistoryModel(judul, poin, tanggalHariIni));

        // Simpan kembali
        sharedPref.edit().putString("list_history_poin", gson.toJson(listHistory)).apply();
    }
}