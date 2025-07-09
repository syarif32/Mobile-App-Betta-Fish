package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class PaymentVerificationModel {
    @SerializedName("id_order")
    private int idOrder;
    @SerializedName("id_pelanggan")
    private int idPelanggan; // ID Pelanggan yang melakukan order
    @SerializedName("tgl_order")
    private String tglOrder;
    @SerializedName("total_bayar")
    private double totalBayar;
    @SerializedName("bukti_bayar")
    private String buktiBayar; // Nama file bukti bayar
    @SerializedName("status")
    private String status; // Status numerik dari DB

    // Detail tambahan untuk ditampilkan di list
    @SerializedName("alamat_kirim")
    private String alamatKirim;
    @SerializedName("kurir")
    private String kurir;
    @SerializedName("service")
    private String service;

    // Getter methods
    public int getIdOrder() { return idOrder; }
    public int getIdPelanggan() { return idPelanggan; }
    public String getTglOrder() { return tglOrder; }
    public double getTotalBayar() { return totalBayar; }
    public String getBuktiBayar() { return buktiBayar; }
    public String getStatus() { return status; }
    public String getAlamatKirim() { return alamatKirim; }
    public String getKurir() { return kurir; }
    public String getService() { return service; }

    // Metode helper untuk memetakan status (opsional, bisa juga di adapter)
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