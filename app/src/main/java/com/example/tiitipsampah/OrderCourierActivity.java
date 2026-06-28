package com.example.tiitipsampah;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random; // Import untuk fungsi random supir

public class OrderCourierActivity extends AppCompatActivity {

    private LinearLayout layoutSummary, layoutSearchingCourier, containerItems;
    private ImageView ivOrderCapturedImage;
    private Button btnOrderScanLagi, btnOrderJemput;
    private TextView tvOrderTotalPoin;

    public static Bitmap bitmapSampahTransfer;
    private String ringkasanTeksSampah = "";
    private int totalPoinDidapat = 0;

    // DATA BASE NAMA SUPIR DAN PLAT NOMOR
    private final String[] DAFTAR_KURIR = {
            "Bang Eja (B 4133 UPB)",
            "Kang Raihan (B 2026 KRI)",
            "Pak Totok (B 9912 AI)",
            "Mang Asep (B 3345 SPH)",
            "Mas Jono (B 5821 GBN)"
    };

    @Override
    protected void onCreate(Bundle BundleSavedInstance) {
        super.onCreate(BundleSavedInstance);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_courier);

        layoutSummary = findViewById(R.id.layoutSummary);
        layoutSearchingCourier = findViewById(R.id.layoutSearchingCourier);
        ivOrderCapturedImage = findViewById(R.id.ivOrderCapturedImage);
        btnOrderScanLagi = findViewById(R.id.btnOrderScanLagi);
        btnOrderJemput = findViewById(R.id.btnOrderJemput);
        tvOrderTotalPoin = findViewById(R.id.tvOrderTotalPoin);
        containerItems = findViewById(R.id.llTrashContainer);

        View mainView = findViewById(R.id.main_order_courier);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        if (bitmapSampahTransfer != null && ivOrderCapturedImage != null) {
            ivOrderCapturedImage.setImageBitmap(bitmapSampahTransfer);
        }

        ArrayList<String> dataSampahAI = getIntent().getStringArrayListExtra("DATA_SAMPAH_AI");
        StringBuilder sb = new StringBuilder();

        if (dataSampahAI != null && !dataSampahAI.isEmpty() && containerItems != null) {
            containerItems.removeAllViews();

            for (int i = 0; i < dataSampahAI.size(); i++) {
                String item = dataSampahAI.get(i);
                int poinItem = 25;
                int bgColorIcon = Color.parseColor("#C8E6C9");

                if (item.toLowerCase().contains("bottle") || item.toLowerCase().contains("plastic")) {
                    poinItem = 50;
                    bgColorIcon = Color.parseColor("#B3E5FC");
                } else if (item.toLowerCase().contains("food") || item.toLowerCase().contains("plant")) {
                    poinItem = 30;
                    bgColorIcon = Color.parseColor("#FFE0B2");
                }

                totalPoinDidapat += poinItem;

                if (i > 0) sb.append(", ");
                sb.append(item);

                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, 0, 0, 36);
                rowLayout.setLayoutParams(rowParams);

                ImageView ivIcon = new ImageView(this);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(130, 130);
                ivIcon.setLayoutParams(iconParams);
                ivIcon.setImageResource(android.R.drawable.ic_menu_report_image);
                ivIcon.setBackgroundColor(bgColorIcon);
                ivIcon.setPadding(20, 20, 20, 20);

                LinearLayout textLayout = new LinearLayout(this);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textLayoutParams.setMargins(40, 0, 0, 0);
                textLayout.setLayoutParams(textLayoutParams);

                TextView tvNama = new TextView(this);
                tvNama.setText("1 " + item);
                tvNama.setTextColor(Color.parseColor("#333333"));
                tvNama.setTextSize(18);
                tvNama.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView tvPoin = new TextView(this);
                tvPoin.setText("( +" + poinItem + " Point )");
                tvPoin.setTextColor(Color.parseColor("#00E676"));
                tvPoin.setTextSize(15);
                tvPoin.setTypeface(null, android.graphics.Typeface.BOLD);

                textLayout.addView(tvNama);
                textLayout.addView(tvPoin);
                rowLayout.addView(ivIcon);
                rowLayout.addView(textLayout);
                containerItems.addView(rowLayout);
            }
            ringkasanTeksSampah = sb.toString();
        }

        if (tvOrderTotalPoin != null) {
            tvOrderTotalPoin.setText("+" + totalPoinDidapat);
        }

        btnOrderScanLagi.setOnClickListener(v -> finish());

        // ====================================================
        // LOGIKA AMBIL SUPIR RANDOM PAS KLIK JEMPUT & INTEGRASI DASHBOARD
        // ====================================================
        btnOrderJemput.setOnClickListener(v -> {
            layoutSummary.setVisibility(View.GONE);
            layoutSearchingCourier.setVisibility(View.VISIBLE);

            // Pilih supir acak menggunakan objek Random
            Random random = new Random();
            int indexSupirTerpilih = random.nextInt(DAFTAR_KURIR.length);
            String supirTerpilih = DAFTAR_KURIR[indexSupirTerpilih];

            // Tentukan string nama file gambar di drawable berdasarkan kurir aktif
            String fotoKurirTerpilih = "ic_menu_gallery";
            if (indexSupirTerpilih == 0) fotoKurirTerpilih = "kurir_eja";
            else if (indexSupirTerpilih == 1) fotoKurirTerpilih = "kurir_raihan";
            else if (indexSupirTerpilih == 2) fotoKurirTerpilih = "kurir_totok";
            else if (indexSupirTerpilih == 3) fotoKurirTerpilih = "kurir_asep";
            else if (indexSupirTerpilih == 4) fotoKurirTerpilih = "kurir_jono";

            // KUNCI DATA KE PREFERENCES BIAR BISA DIBACA DASHBOARD UTAMA SECARA DIANAMIS
            SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("kurir_aktif", supirTerpilih);
            editor.putString("foto_kurir_aktif", fotoKurirTerpilih);
            editor.apply();

            new Handler().postDelayed(() -> {
                // Toast memanggil nama kurir hasil acakan AI secara spesifik!
                Toast.makeText(OrderCourierActivity.this, "Kurir Ditemukan! " + supirTerpilih + " sedang meluncur ke lokasi, Cuy! 🏃💨", Toast.LENGTH_LONG).show();

                // SIMPAN RIWAYAT JEMPUT SAMPAH KE HISTORY REALTIME
                simpanKeHistorySampah("Setor: " + ringkasanTeksSampah, "Reward: +" + totalPoinDidapat + " XP");

                bitmapSampahTransfer = null;
                finish();
            }, 3000);
        });
    }

    private void simpanKeHistorySampah(String judul, String reward) {
        SharedPreferences sharedPref = getSharedPreferences("GreenBinPref", MODE_PRIVATE);
        Gson gson = new Gson();

        String jsonStr = sharedPref.getString("list_history_sampah", "");
        ArrayList<HistoryModel> listHistory;

        if (jsonStr.isEmpty()) {
            listHistory = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            listHistory = gson.fromJson(jsonStr, type);
        }

        String tanggalHariIni = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        listHistory.add(0, new HistoryModel(judul, reward, tanggalHariIni));

        sharedPref.edit().putString("list_history_sampah", gson.toJson(listHistory)).commit();
    }
}