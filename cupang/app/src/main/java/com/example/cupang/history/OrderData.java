package com.example.cupang.history;

import java.util.List;

public class OrderData {
    private int id_order;
    private String tgl_order;
    private double total_bayar;
    private String status;
    private String produk;

    public int getId_order() {
        return id_order;
    }

    public String getTgl_order() {
        return tgl_order;
    }

    public double getTotal_bayar() {
        return total_bayar;
    }

    public String getStatus() {
        return status;
    }

    public String getProduk() {
        return produk;
    }
}
