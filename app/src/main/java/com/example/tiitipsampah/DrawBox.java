package com.example.tiitipsampah;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.google.mlkit.vision.objects.DetectedObject;
import java.util.ArrayList;
import java.util.List;

public class DrawBox extends View {

    private Paint paint = new Paint();
    private List<DetectedObject> objects = new ArrayList<>();
    private int previewWidth = 1, previewHeight = 1;

    public DrawBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.parseColor("#00E676")); // Hijau AI
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f); // Ketebalan garis kotak
    }

    public void setResults(List<DetectedObject> objects, int width, int height) {
        this.objects = objects;
        this.previewWidth = width;
        this.previewHeight = height;
        invalidate(); // Paksa gambar ulang layar (refresh)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Hitung skala rasio agar koordinat AI pas dengan ukuran layar HP
        float scaleX = (float) getWidth() / (float) previewHeight; // ML Kit rotasi 90 derajat bawaannya
        float scaleY = (float) getHeight() / (float) previewWidth;

        for (DetectedObject obj : objects) {
            Rect rect = obj.getBoundingBox();

            // Konversi koordinat kotak AI ke koordinat layar HP
            float left = rect.left * scaleX;
            float top = rect.top * scaleY;
            float right = rect.right * scaleX;
            float bottom = rect.bottom * scaleY;

            // Gambar kotak hijaunya di layar, Cuy!
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }
}