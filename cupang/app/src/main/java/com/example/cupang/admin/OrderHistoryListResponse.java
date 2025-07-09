package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderHistoryListResponse extends BaseResponse {
    @SerializedName("data")
    private List<OrderHistoryModel> data;

    public List<OrderHistoryModel> getData() {
        return data;
    }
}