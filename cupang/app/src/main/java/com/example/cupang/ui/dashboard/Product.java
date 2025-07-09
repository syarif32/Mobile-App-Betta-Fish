package com.example.cupang.ui.dashboard;

import java.io.Serializable;

public class Product implements Serializable {
    private String kode;
    private String merk;
    private double hargabeli;
    private int stok;
    private String foto;
    private String deskripsi;
    private int quantity = 1;
    private String email, nama, alamat, kota, provinsi, kodepos, telp;
    private String kategori;
    private String satuan;
    private int view;

    // Getter dan Setter
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }

    public String getMerk() { return merk; }
    public void setMerk(String merk) { this.merk = merk; }

    public double getHargabeli() { return hargabeli; }
    public void setHargabeli(double hargabeli) { this.hargabeli = hargabeli; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getKota() { return kota; }
    public void setKota(String kota) { this.kota = kota; }

    public String getProvinsi() { return provinsi; }
    public void setProvinsi(String provinsi) { this.provinsi = provinsi; }

    public String getKodepos() { return kodepos; }
    public void setKodepos(String kodepos) { this.kodepos = kodepos; }

    public String getTelp() { return telp; }
    public void setTelp(String telp) { this.telp = telp; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }

    public int getView() { return view; }
    public void setView(int view) { this.view = view; }
}