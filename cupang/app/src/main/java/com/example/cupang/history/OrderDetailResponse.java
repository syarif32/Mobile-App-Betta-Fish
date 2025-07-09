package com.example.cupang.history;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDetailResponse {
    private String status;
    private String message; // Opsional, jika Anda ingin menambahkan pesan dari server

    @SerializedName("id_order")
    private int idOrder;
    @SerializedName("tgl_order")
    private String tglOrder;
    @SerializedName("total_bayar")
    private double totalBayar;
    @SerializedName("status_order") // Ubah nama field untuk menghindari konflik dengan "status" di atas
    private String statusOrder;
    @SerializedName("bukti_bayar")
    private String buktiBayar; // Untuk menampilkan bukti bayar yang sudah ada

    // Informasi alamat pengiriman
    @SerializedName("alamat_kirim")
    private String alamatKirim;
    @SerializedName("telp_kirim")
    private String telpKirim;
    @SerializedName("kurir")
    private String kurir;
    @SerializedName("service")
    private String service;
    @SerializedName("ongkir")
    private double ongkir;
    @SerializedName("metodebayar") // MENAMBAHKAN FIELD INI
    private int metodebayar;

    @SerializedName("products") // Nama array untuk daftar produk
    private List<ProductDetailModel> products;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public String getTglOrder() {
        return tglOrder;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public String getBuktiBayar() {
        return buktiBayar;
    }

    public String getAlamatKirim() {
        return alamatKirim;
    }

    public String getTelpKirim() {
        return telpKirim;
    }

    public String getKurir() {
        return kurir;
    }

    public String getService() {
        return service;
    }

    public double getOngkir() {
        return ongkir;
    }

    public int getMetodebayar() { // MENAMBAHKAN GETTER INI
        return metodebayar;
    }

    public List<ProductDetailModel> getProducts() {
        return products;
    }
}