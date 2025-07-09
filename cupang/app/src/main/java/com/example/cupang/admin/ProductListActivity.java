package com.example.cupang.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class ProductListActivity extends AppCompatActivity implements ProductAdminAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private ProductAdminAdapter adapter;
    private List<ProductAdminModel> productList;
    private AdminApiService adminApiService;
    private Button addProductButton;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_actifity);

        Toolbar toolbar = findViewById(R.id.toolbarAdminProduct);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manajemen Produk");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewProductAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        addProductButton = findViewById(R.id.addProductButton);

        productList = new ArrayList<>();
        adapter = new ProductAdminAdapter(this, productList, this);
        recyclerView.setAdapter(adapter);

        adminApiService = AdminApiClient.getClient().create(AdminApiService.class);

        addProductButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, AddEditProductActivity.class);
            startActivity(intent);
        });

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);

        adminApiService.getAllProducts().enqueue(new Callback<ProductListResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductListResponse> call, @NonNull Response<ProductListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    List<ProductAdminModel> fetchedProducts = response.body().getData();
                    if (fetchedProducts != null && !fetchedProducts.isEmpty()) {
                        productList.clear();
                        productList.addAll(fetchedProducts);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(ProductListActivity.this, "Tidak ada produk tersedia.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(ProductListActivity.this, "Gagal memuat produk: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("ProductListAdmin", "Error response: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductListResponse> call, @NonNull Throwable t) {
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(ProductListActivity.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductListAdmin", "Connection error: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onEditClick(ProductAdminModel product) {
        Intent intent = new Intent(ProductListActivity.this, AddEditProductActivity.class);
        intent.putExtra("PRODUCT_KODE", product.getKode()); // Kirim kode produk untuk diedit
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ProductAdminModel product) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Produk")
                .setMessage("Apakah Anda yakin ingin menghapus produk '" + product.getMerk() + "' (Kode: " + product.getKode() + ")?")
                .setPositiveButton("Hapus", (dialog, which) -> confirmDeleteProduct(product.getKode()))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void confirmDeleteProduct(String kode) {
        adminApiService.deleteProduct(kode).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    Toast.makeText(ProductListActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadProducts(); // Muat ulang daftar setelah penghapusan
                } else {
                    Toast.makeText(ProductListActivity.this, "Gagal menghapus produk: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProductListAdmin", "Delete error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Toast.makeText(ProductListActivity.this, "Gagal koneksi saat menghapus: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductListAdmin", "Delete connection error: " + t.getMessage(), t);
            }
        });
    }
}