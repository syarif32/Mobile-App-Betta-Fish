package com.example.cupang.chechkout;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KotaResponse {

    @SerializedName("rajaongkir")
    private RajaOngkir rajaongkir;

    public List<kota> getResults() {
        return rajaongkir != null ? rajaongkir.results : null;
    }

    private static class RajaOngkir {
        @SerializedName("results")
        private List<kota> results;
    }
}