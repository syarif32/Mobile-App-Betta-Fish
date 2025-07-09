package com.example.cupang.history;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // Import ini untuk View.GONE/VISIBLE
import android.widget.LinearLayout; // Import ini untuk LinearLayout emptyStateLayout
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import ini untuk Toolbar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cupang.R;
import java.util.List;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private LinearLayout emptyStateLayout; // Deklarasi untuk empty state layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        // Mengatur tombol kembali di Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi empty state layout
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int idPelanggan = sharedPreferences.getInt("id", -1);

        if (idPelanggan != -1) {
            loadOrderHistory(idPelanggan);
        } else {
            Toast.makeText(this, "ID Pelanggan tidak ditemukan", Toast.LENGTH_SHORT).show();
            // Tampilkan empty state jika ID pelanggan tidak ditemukan
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadOrderHistory(int idPelanggan) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://allend.site/API_Product/") // Pastikan ini IP yang benar
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService api = retrofit.create(APIService.class);
        api.getOrderHistory(idPelanggan).enqueue(new Callback<OrderHistoryResponse>() {
            @Override
            public void onResponse(Call<OrderHistoryResponse> call, Response<OrderHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    List<OrderHistoryModel> orders = response.body().getData();
                    if (orders != null && !orders.isEmpty()) {
                        adapter = new HistoryAdapter(History.this, orders);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE); // Sembunyikan empty state
                    } else {
                        // Jika data kosong, tampilkan empty state
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(History.this, "Tidak ada data riwayat", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Jika respons tidak sukses atau body null, tampilkan empty state
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(History.this, "Tidak ada data riwayat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryResponse> call, Throwable t) {
                Toast.makeText(History.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HistoryError", t.getMessage(), t);
                // Jika koneksi gagal, tampilkan empty state
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    // Tambahkan onResume untuk memuat ulang data saat kembali ke halaman ini
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int idPelanggan = sharedPreferences.getInt("id", -1);
        if (idPelanggan != -1) {
            loadOrderHistory(idPelanggan);
        }
    }
}