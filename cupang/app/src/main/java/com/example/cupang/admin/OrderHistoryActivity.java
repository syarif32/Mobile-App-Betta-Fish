package com.example.cupang.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cupang.R;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<OrderHistoryModel> orderHistoryList;
    private AdminApiService adminApiService;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbarOrderHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Riwayat Order");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.emptyStateLayoutOrderHistory);

        orderHistoryList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderHistoryList, this);
        recyclerView.setAdapter(adapter);

        adminApiService = AdminApiClient.getClient().create(AdminApiService.class);

        loadOrderHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderHistory(); // Refresh data when returning to this activity
    }

    private void loadOrderHistory() {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);

        adminApiService.getAllOrderHistory().enqueue(new Callback<OrderHistoryListResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderHistoryListResponse> call, @NonNull Response<OrderHistoryListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    List<OrderHistoryModel> fetchedOrders = response.body().getData();
                    if (fetchedOrders != null && !fetchedOrders.isEmpty()) {
                        adapter.setOrders(fetchedOrders); // Use setOrders to update and notify
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(OrderHistoryActivity.this, "Tidak ada riwayat order.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(OrderHistoryActivity.this, "Gagal memuat riwayat order: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("OrderHistoryActivity", "Error response: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderHistoryListResponse> call, @NonNull Throwable t) {
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(OrderHistoryActivity.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderHistoryActivity", "Connection error: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onViewDetailsClick(OrderHistoryModel order) {
        // Navigasi ke PaymentDetailActivity untuk melihat detail order
        Intent intent = new Intent(OrderHistoryActivity.this, PaymentDetailActivity.class);
        intent.putExtra("ORDER_ID", order.getIdOrder());
        startActivity(intent);
    }
}