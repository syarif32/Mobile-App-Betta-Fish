package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;

public class ProductDetailResponse extends BaseResponse {
    @SerializedName("data")
    private ProductDetailAdminModel data;

    public ProductDetailAdminModel getData() {
        return data;
    }
}