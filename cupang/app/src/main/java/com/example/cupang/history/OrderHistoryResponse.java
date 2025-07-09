package com.example.cupang.history;

import java.util.List;

public class OrderHistoryResponse {
    private String status;
    private List<OrderHistoryModel> data;

    public String getStatus() {
        return status;
    }

    public List<OrderHistoryModel> getData() {
        return data;
    }
}
