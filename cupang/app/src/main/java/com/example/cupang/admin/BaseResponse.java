package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    @SerializedName("status")
    private String status; // Misalnya: "sukses" atau "gagal"
    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}