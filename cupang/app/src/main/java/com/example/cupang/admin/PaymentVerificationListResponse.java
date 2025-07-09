package com.example.cupang.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PaymentVerificationListResponse extends BaseResponse {
    @SerializedName("data")
    private List<PaymentVerificationModel> data;

    public List<PaymentVerificationModel> getData() {
        return data;
    }
}