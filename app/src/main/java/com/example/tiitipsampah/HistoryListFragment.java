package com.example.tiitipsampah;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class HistoryListFragment extends Fragment {

    private int type = 0; // 0 = Poin, 1 = Sampah

    public static HistoryListFragment newInstance(int type) {
        HistoryListFragment fragment = new HistoryListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        LinearLayout layoutPoin = view.findViewById(R.id.layoutPoinHistory);
        LinearLayout layoutSampah = view.findViewById(R.id.layoutSampahHistory);

        if (layoutPoin != null && layoutSampah != null) {
            if (type == 0) {
                layoutPoin.setVisibility(View.VISIBLE);
                layoutSampah.setVisibility(View.GONE);
                muatDataHistoryRealtime(layoutPoin, "list_history_poin");
            } else {
                layoutPoin.setVisibility(View.GONE);
                layoutSampah.setVisibility(View.VISIBLE);
                muatDataHistoryRealtime(layoutSampah, "list_history_sampah");
            }
        }

        return view;
    }

    private void muatDataHistoryRealtime(LinearLayout containerLayout, String keyPrefs) {
        if (getContext() == null) return;

        containerLayout.removeAllViews(); // Bersihkan view dummy lama
        containerLayout.setPadding(16, 32, 16, 32);

        SharedPreferences sharedPref = getContext().getSharedPreferences("GreenBinPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonStr = sharedPref.getString(keyPrefs, "");

        ArrayList<HistoryModel> listData;
        if (jsonStr.isEmpty()) {
            listData = new ArrayList<>();
        } else {
            Type typeToken = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            listData = gson.fromJson(jsonStr, typeToken);
        }

        // KONDISI JIKA DATA MASIH KOSONG
        if (listData.isEmpty()) {
            TextView tvKosong = new TextView(getContext());
            tvKosong.setText("Belum ada riwayat aktivitas nih, Cuy! 🏜️");
            tvKosong.setTextSize(16);
            tvKosong.setTextColor(Color.GRAY);
            tvKosong.setGravity(Gravity.CENTER);
            containerLayout.addView(tvKosong);
            return;
        }

        // GENERATE LIST SECARA REAL-TIME
        for (HistoryModel data : listData) {
            LinearLayout boxItem = new LinearLayout(getContext());
            boxItem.setOrientation(LinearLayout.VERTICAL);
            boxItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
            boxItem.setPadding(32, 24, 32, 24);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 24);
            boxItem.setLayoutParams(params);

            // Judul Item
            TextView tvJudul = new TextView(getContext());
            tvJudul.setText(data.getJudul());
            tvJudul.setTextSize(16);
            tvJudul.setTextColor(Color.parseColor("#333333"));
            tvJudul.setTypeface(null, Typeface.BOLD);

            // SubJudul / Poin Nilai
            TextView tvSub = new TextView(getContext());
            tvSub.setText(data.getSubJudul());
            tvSub.setTextSize(14);
            tvSub.setTextColor(type == 0 ? Color.parseColor("#D32F2F") : Color.parseColor("#00C853"));
            tvSub.setTypeface(null, Typeface.BOLD);
            tvSub.setPadding(0, 4, 0, 4);

            // Tanggal Transaksi
            TextView tvTgl = new TextView(getContext());
            tvTgl.setText(data.getTanggal());
            tvTgl.setTextSize(12);
            tvTgl.setTextColor(Color.GRAY);

            boxItem.addView(tvJudul);
            boxItem.addView(tvSub);
            boxItem.addView(tvTgl);

            containerLayout.addView(boxItem);
        }
    }
}