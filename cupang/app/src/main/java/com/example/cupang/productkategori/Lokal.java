package com.example.cupang.productkategori;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cupang.R;
// Pastikan import ini sudah benar dan merujuk ke kelas Product di package ini
// HAPUS BARIS INI JIKA ADA: import com.example.cupang.ui.products.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Lokal extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private SearchView searchView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokal);

        // Inisialisasi view
        productRecyclerView = findViewById(R.id.productRecyclerView);
        searchView = findViewById(R.id.searchView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(productList, this);
        productRecyclerView.setAdapter(productAdapter);
        fetchProductData();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return true;
            }
        });
    }

    private void fetchProductData() {
        ApiService apiService = ServerApi.getApiService();
        // Call List<com.example.cupang.productkategori.Product>
        Call<List<Product>> call = apiService.getProductsByKategori("CL");

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}