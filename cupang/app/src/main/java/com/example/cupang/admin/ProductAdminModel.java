package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class ProductAdminModel {
    @SerializedName("kode")
    private String kode;
    @SerializedName("merk")
    private String merk;
    @SerializedName("kategori")
    private String kategori;
    @SerializedName("hargajual")
    private double hargajual;
    @SerializedName("stok")
    private int stok;
    @SerializedName("foto")
    private String foto; // Nama file foto

    // Konstruktor (opsional, tapi bagus untuk pengujian)
    public ProductAdminModel(String kode, String merk, String kategori, double hargajual, int stok, String foto) {
        this.kode = kode;
        this.merk = merk;
        this.kategori = kategori;
        this.hargajual = hargajual;
        this.stok = stok;
        this.foto = foto;
    }

    // Getter methods
    public String getKode() {
        return kode;
    }

    public String getMerk() {
        return merk;
    }

    public String getKategori() {
        return kategori;
    }

    public double getHargajual() {
        return hargajual;
    }

    public int getStok() {
        return stok;
    }

    public String getFoto() {
        return foto;
    }
}