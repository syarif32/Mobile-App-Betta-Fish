package com.example.cupang.chechkout;

import com.google.gson.annotations.SerializedName;

public class provinsi {

    @SerializedName("province_id")
    private String id_provinsi;

    @SerializedName("province")
    private String nama_provinsi;

    public String getId_provinsi() {
        return id_provinsi;
    }

    public void setId_provinsi(String id_provinsi) {
        this.id_provinsi = id_provinsi;
    }

    public String getNama_provinsi() {
        return nama_provinsi;
    }

    public void setNama_provinsi(String nama_provinsi) {
        this.nama_provinsi = nama_provinsi;
    }
}