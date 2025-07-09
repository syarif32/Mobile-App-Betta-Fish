package com.example.cupang.chechkout;

import com.google.gson.annotations.SerializedName;

public class kota {

    @SerializedName("city_id")
    private String id_kota;

    @SerializedName("city_name")
    private String nama_kota;

    @SerializedName("type")
    private String tipe_kota;

    public String getId_kota() {
        return id_kota;
    }

    public void setId_kota(String id_kota) {
        this.id_kota = id_kota;
    }

    public String getNama_kota() {
        return nama_kota;
    }

    public void setNama_kota(String nama_kota) {
        this.nama_kota = nama_kota;
    }

    public String getTipe_kota() {
        return tipe_kota;
    }

    public void setTipe_kota(String tipe_kota) {
        this.tipe_kota = tipe_kota;
    }

    public String getFullNamaKota() {
        return tipe_kota + " " + nama_kota;
    }
}