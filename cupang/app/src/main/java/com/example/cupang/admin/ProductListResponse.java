package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProductListResponse extends BaseResponse {
    @SerializedName("data")
    private List<ProductAdminModel> data;

    public List<ProductAdminModel> getData() {
        return data;
    }
}