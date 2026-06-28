package com.example.tiitipsampah;

public class HistoryModel {
    private String judul;
    private String subJudul;
    private String tanggal;

    // Constructor untuk inisialisasi data history
    public HistoryModel(String judul, String subJudul, String tanggal) {
        this.judul = judul;
        this.subJudul = subJudul;
        this.tanggal = tanggal;
    }

    // Getter untuk mengambil data judul riwayat
    public String getJudul() {
        return judul;
    }

    // Getter untuk mengambil data sub-judul (poin/reward)
    public String getSubJudul() {
        return subJudul;
    }

    // Getter untuk mengambil tanggal aktivitas
    public String getTanggal() {
        return tanggal;
    }
}