package com.example.tiitipsampah;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationCheckActivity extends AppCompatActivity {

    private static final int REQ_LOKASI = 100;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvStatus, tvAlamat;
    private Button btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_check);

        // Inisialisasi View
        tvStatus = findViewById(R.id.tvStatus);
        tvAlamat = findViewById(R.id.tvAlamat);
        btnLanjut = findViewById(R.id.btnLanjut);

        // Inisialisasi Google Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Kasih jeda sebentar biar user liat animasinya
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mintaIzinLokasi();
            }
        }, 2500);

        // Logika tombol lanjut
        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gasKeLogin();
            }
        });
    }

    private void mintaIzinLokasi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOKASI);
        } else {
            ambilLokasiSekarang();
        }
    }

    private void ambilLokasiSekarang() {
        tvStatus.setText("Mendeteksi Lokasi...");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Ubah koordinat jadi nama alamat (Reverse Geocoding)
                        getAlamatDariLatLong(location.getLatitude(), location.getLongitude());
                    } else {
                        tvAlamat.setText("Gagal mendapatkan lokasi. Pastikan GPS aktif!");
                        btnLanjut.setVisibility(View.VISIBLE); // Tetap munculin tombol biar gak stuck
                    }
                }
            });
        }
    }

    private void getAlamatDariLatLong(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String alamatLengkap = addresses.get(0).getAddressLine(0);
                tvStatus.setText("Lokasi Ditemukan!");
                tvAlamat.setText(alamatLengkap);

                // Munculin tombol lanjut setelah lokasi ketemu
                btnLanjut.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            tvAlamat.setText("Lat: " + lat + ", Lon: " + lon);
            btnLanjut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOKASI) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ambilLokasiSekarang();
            } else {
                Toast.makeText(this, "Izin ditolak, fitur jemput sampah tidak maksimal", Toast.LENGTH_SHORT).show();
                gasKeLogin();
            }
        }
    }

    private void gasKeLogin() {
        Intent intent = new Intent(LocationCheckActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}