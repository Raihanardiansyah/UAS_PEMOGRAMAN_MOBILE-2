package com.example.tiitipsampah;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// IMPORT UNTUK AI GOOGLE ML KIT
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 101;

    private PreviewView viewFinder;
    private DrawBox viewDrawBox;
    private ImageView ivCapturedResult, ivBackScan;
    private TextView tvScanStatus, tvDetailPoin;
    private Button btnShutter, btnRetake, btnCallCourier;
    private LinearLayout layoutActionButtons;

    private ProcessCameraProvider cameraProvider;
    private ObjectDetector objectDetector;
    private ExecutorService cameraExecutor;

    private boolean isFrozen = false;
    private List<DetectedObject> terakhirTerdeteksi;
    private ArrayList<String> listHasilScanFinal = new ArrayList<>();
    private Bitmap temporaryBitmap = null; // Menyimpan bitmap tepat saat dijepret!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        viewFinder = findViewById(R.id.viewFinder);
        viewDrawBox = findViewById(R.id.viewDrawBox);
        ivCapturedResult = findViewById(R.id.ivCapturedResult);
        ivBackScan = findViewById(R.id.ivBackScan);
        tvScanStatus = findViewById(R.id.tvScanStatus);
        tvDetailPoin = findViewById(R.id.tvDetailPoin);
        btnShutter = findViewById(R.id.btnShutter);
        btnRetake = findViewById(R.id.btnRetake);
        btnCallCourier = findViewById(R.id.btnCallCourier);
        layoutActionButtons = findViewById(R.id.layoutActionButtons);

        cameraExecutor = Executors.newSingleThreadExecutor();

        View mainView = findViewById(R.id.main_scan);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        if (ivBackScan != null) {
            ivBackScan.setOnClickListener(v -> finish());
        }

        ObjectDetectorOptions options = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build();
        objectDetector = ObjectDetection.getClient(options);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCameraX();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        // AKSI JEPRET SAMPAH
        btnShutter.setOnClickListener(v -> {
            // FIX GAMBAR: Ambil bitmap saat kamera masih aktif (sebelum isFrozen = true)
            temporaryBitmap = viewFinder.getBitmap();
            isFrozen = true;

            if (temporaryBitmap != null) {
                ivCapturedResult.setImageBitmap(temporaryBitmap);
                ivCapturedResult.setVisibility(View.VISIBLE);
                OrderCourierActivity.bitmapSampahTransfer = temporaryBitmap; // Langsung kunci ke static transfer
            }

            prosesTeksHasilFinal(terakhirTerdeteksi);

            viewDrawBox.setVisibility(View.GONE);
            btnShutter.setVisibility(View.GONE);
            layoutActionButtons.setVisibility(View.VISIBLE);
        });

        // AKSI FOTO ULANG
        btnRetake.setOnClickListener(v -> {
            isFrozen = false;
            temporaryBitmap = null;
            OrderCourierActivity.bitmapSampahTransfer = null;
            ivCapturedResult.setVisibility(View.GONE);
            viewDrawBox.setVisibility(View.VISIBLE);
            btnShutter.setVisibility(View.VISIBLE);
            layoutActionButtons.setVisibility(View.GONE);

            tvScanStatus.setText("Mendeteksi Objek...");
            tvDetailPoin.setText("Arahkan kamera ke arah sampah, kotak hijau otomatis mengikuti objek!");
        });

        // TOMBOL PANGGIL KURIR
        btnCallCourier.setOnClickListener(v -> {
            Intent intent = new Intent(ScanActivity.this, OrderCourierActivity.class);
            intent.putStringArrayListExtra("DATA_SAMPAH_AI", listHasilScanFinal);
            startActivity(intent);
        });
    }

    private void startCameraX() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    if (isFrozen) {
                        imageProxy.close();
                        return;
                    }

                    @SuppressWarnings("UnsafeOptInUsageError")
                    android.media.Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                        objectDetector.process(image)
                                .addOnSuccessListener(detectedObjects -> {
                                    terakhirTerdeteksi = detectedObjects;
                                    viewDrawBox.setResults(detectedObjects, imageProxy.getWidth(), imageProxy.getHeight());
                                })
                                .addOnFailureListener(e -> e.printStackTrace())
                                .addOnCompleteListener(task -> imageProxy.close());
                    } else {
                        imageProxy.close();
                    }
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void prosesTeksHasilFinal(List<DetectedObject> listObjek) {
        listHasilScanFinal.clear();

        if (listObjek == null || listObjek.isEmpty()) {
            tvScanStatus.setText("AI Result: Objek Tidak Dikenali ❓");
            tvDetailPoin.setText("Kategori: Umum • Tetap dapat +10 Poin GreenBin");
            listHasilScanFinal.add("Barang Campuran");
            return;
        }

        StringBuilder kumpulanNamaObjek = new StringBuilder();
        int totalPoin = 0;
        int jumlahItem = 0;

        for (DetectedObject obj : listObjek) {
            if (!obj.getLabels().isEmpty()) {
                String labelNama = obj.getLabels().get(0).getText();
                listHasilScanFinal.add(labelNama);

                if (jumlahItem > 0) kumpulanNamaObjek.append(", ");
                kumpulanNamaObjek.append(labelNama);

                if (labelNama.toLowerCase().contains("bottle") || labelNama.toLowerCase().contains("plastic")) {
                    totalPoin += 50;
                } else if (labelNama.toLowerCase().contains("food") || labelNama.toLowerCase().contains("plant")) {
                    totalPoin += 30;
                } else {
                    totalPoin += 25;
                }
                jumlahItem++;
            }
        }

        if (jumlahItem == 0) {
            tvScanStatus.setText("AI Result: Barang Campuran/Plastik 📝");
            tvDetailPoin.setText("Kategori: Daur Ulang • Reward: +30 XP Poin");
            listHasilScanFinal.add("Barang Campuran");
        } else {
            tvScanStatus.setText("AI Result: " + kumpulanNamaObjek.toString() + " 📝");
            tvDetailPoin.setText("Terdeteksi " + jumlahItem + " Item • Total Reward: +" + totalPoin + " XP Poin!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Izin kamera ditolak!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}