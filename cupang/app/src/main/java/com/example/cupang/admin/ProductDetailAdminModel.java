package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class ProductDetailAdminModel {
    @SerializedName("kode")
    private String kode;
    @SerializedName("merk")
    private String merk;
    @SerializedName("kategori")
    private String kategori;
    @SerializedName("satuan")
    private String satuan;
    // Hapus properti diskonbeli dan diskonjual
    // @SerializedName("hargabeli")
    // private double hargabeli;
    // @SerializedName("diskonbeli")
    // private double diskonbeli;
    @SerializedName("hargapokok")
    private double hargapokok;
    @SerializedName("hargajual")
    private double hargajual;
    // @SerializedName("diskonjual")
    // private double diskonjual;
    @SerializedName("stok")
    private int stok;
    @SerializedName("foto")
    private String foto; // Nama file foto
    @SerializedName("deskripsi")
    private String deskripsi;
    @SerializedName("view")
    private int view;
    private double hargabeli;

    public ProductDetailAdminModel() {
    }

    // Perbarui konstruktor, hapus parameter diskon
    public ProductDetailAdminModel(String kode, String merk, String kategori, String satuan,
                                   double hargabeli, double hargapokok,
                                   double hargajual, int stok,
                                   String foto, String deskripsi, int view) {
        this.kode = kode;
        this.merk = merk;
        this.kategori = kategori;
        this.satuan = satuan;
        this.hargabeli = hargabeli;
        // this.diskonbeli = diskonbeli; // Hapus
        this.hargapokok = hargapokok;
        this.hargajual = hargajual;
        // this.diskonjual = diskonjual; // Hapus
        this.stok = stok;
        this.foto = foto;
        this.deskripsi = deskripsi;
        this.view = view;
    }

    // Getter methods
    public String getKode() { return kode; }
    public String getMerk() { return merk; }
    public String getKategori() { return kategori; }
    public String getSatuan() { return satuan; }
    public double getHargabeli() { return hargabeli; }
    // Hapus getter diskonbeli: public double getDiskonbeli() { return diskonbeli; }
    public double getHargapokok() { return hargapokok; }
    public double getHargajual() { return hargajual; }
    // Hapus getter diskonjual: public double getDiskonjual() { return diskonjual; }
    public int getStok() { return stok; }
    public String getFoto() { return foto; }
    public String getDeskripsi() { return deskripsi; }
    public int getView() { return view; }

    // Setter methods (perbarui sesuai perubahan)
    public void setKode(String kode) { this.kode = kode; }
    public void setMerk(String merk) { this.merk = merk; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public void setHargabeli(double hargabeli) { this.hargabeli = hargabeli; }
    // Hapus setter diskonbeli: public void setDiskonbeli(double diskonbeli) { this.diskonbeli = diskonbeli; }
    public void setHargapokok(double hargapokok) { this.hargapokok = hargapokok; }
    public void setHargajual(double hargajual) { this.hargajual = hargajual; }
    // Hapus setter diskonjual: public void setDiskonjual(double diskonjual) { this.diskonjual = diskonjual; }
    public void setStok(int stok) { this.stok = stok; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setView(int view) { this.view = view; }
}