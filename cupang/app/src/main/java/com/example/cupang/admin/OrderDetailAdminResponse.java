package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDetailAdminResponse extends BaseResponse {
    @SerializedName("data")
    private OrderDetailData data;

    public OrderDetailData getData() {
        return data;
    }

    public static class OrderDetailData {
        @SerializedName("order")
        private OrderInfo orderInfo;
        @SerializedName("products")
        private List<OrderProductItemAdminModel> products;
        @SerializedName("pelanggan")
        private PelangganInfo pelangganInfo;

        public OrderInfo getOrderInfo() { return orderInfo; }
        public List<OrderProductItemAdminModel> getProducts() { return products; }
        public PelangganInfo getPelangganInfo() { return pelangganInfo; }
    }

    public static class OrderInfo {
        @SerializedName("id_order")
        private int idOrder;
        @SerializedName("id_pelanggan")
        private int idPelanggan;
        @SerializedName("tgl_order")
        private String tglOrder;
        @SerializedName("subtotal")
        private double subtotal;
        @SerializedName("ongkir")
        private double ongkir;
        @SerializedName("total_bayar")
        private double totalBayar;
        @SerializedName("kurir")
        private String kurir;
        @SerializedName("service")
        private String service;
        @SerializedName("alamat_kirim")
        private String alamatKirim;
        @SerializedName("telp_kirim")
        private String telpKirim;
        @SerializedName("kota")
        private String kota;
        @SerializedName("provinsi")
        private String provinsi;
        @SerializedName("lama_kirim")
        private String lamaKirim;
        @SerializedName("kodepos")
        private String kodepos;
        @SerializedName("metodebayar")
        private int metodeBayar;
        @SerializedName("bukti_bayar")
        private String buktiBayar;
        @SerializedName("status")
        private String status;

        public int getIdOrder() { return idOrder; }
        public int getIdPelanggan() { return idPelanggan; }
        public String getTglOrder() { return tglOrder; }
        public double getSubtotal() { return subtotal; }
        public double getOngkir() { return ongkir; }
        public double getTotalBayar() { return totalBayar; }
        public String getKurir() { return kurir; }
        public String getService() { return service; }
        public String getAlamatKirim() { return alamatKirim; }
        public String getTelpKirim() { return telpKirim; }
        public String getKota() { return kota; }
        public String getProvinsi() { return provinsi; }
        public String getLamaKirim() { return lamaKirim; }
        public String getKodepos() { return kodepos; }
        public int getMetodeBayar() { return metodeBayar; }
        public String getBuktiBayar() { return buktiBayar; }
        public String getStatus() { return status; }

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

    public static class PelangganInfo {
        @SerializedName("nama")
        private String nama;
        @SerializedName("email")
        private String email;
        @SerializedName("telp")
        private String telp;

        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getTelp() { return telp; }
    }
}