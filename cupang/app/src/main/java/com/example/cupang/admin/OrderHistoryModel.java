package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class OrderHistoryModel {
    @SerializedName("id_order")
    private int idOrder;
    @SerializedName("id_pelanggan")
    private int idPelanggan;
    @SerializedName("nama_pelanggan")
    private String namaPelanggan;
    @SerializedName("tgl_order")
    private String tglOrder;
    @SerializedName("total_bayar")
    private double totalBayar;
    @SerializedName("bukti_bayar")
    private String buktiBayar;
    @SerializedName("status")
    private String status; // Status numerik dari DB
    @SerializedName("alamat_kirim")
    private String alamatKirim;
    @SerializedName("kurir")
    private String kurir;
    @SerializedName("service")
    private String service;
    @SerializedName("subtotal")
    private double subtotal;
    @SerializedName("ongkir")
    private double ongkir;

    // Getters
    public int getIdOrder() { return idOrder; }
    public int getIdPelanggan() { return idPelanggan; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public String getTglOrder() { return tglOrder; }
    public double getTotalBayar() { return totalBayar; }
    public String getBuktiBayar() { return buktiBayar; }
    public String getStatus() { return status; }
    public String getAlamatKirim() { return alamatKirim; }
    public String getKurir() { return kurir; }
    public String getService() { return service; }
    public double getSubtotal() { return subtotal; }
    public double getOngkir() { return ongkir; }

    // Helper method to get formatted status
    public String getFormattedStatus() {
        switch (this.status) {
            case "0": return "Menunggu Pembayaran";
            case "1": return "Sudah Dibayar (Menunggu Konfirmasi)";
            case "2": return "Order Berhasil";
            case "3": return "Dalam Pengiriman";
            case "4": return "Selesai";
            case "5": return "Dibatalkan";
            default: return "Status Tidak Diketahui";
        }
    }
}