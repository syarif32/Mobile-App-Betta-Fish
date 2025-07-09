package com.example.cupang.history;

import com.google.gson.annotations.SerializedName;

public class ProductDetailModel {
    @SerializedName("kode_product")
    private String kodeProduct;
    @SerializedName("merk") // Nama produk dari tbl_product
    private String namaProduk;
    @SerializedName("harga_satuan")
    private double hargaSatuan;
    @SerializedName("qty")
    private int qty;
    @SerializedName("foto") // Foto produk dari tbl_product
    private String fotoProduk;

    public String getKodeProduct() {
        return kodeProduct;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public double getHargaSatuan() {
        return hargaSatuan;
    }

    public int getQty() {
        return qty;
    }

    public String getFotoProduk() {
        return fotoProduk;
    }
}