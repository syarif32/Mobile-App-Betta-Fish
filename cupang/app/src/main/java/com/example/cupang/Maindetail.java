package com.example.cupang;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cupang.ui.products.ApiService;
import com.example.cupang.ui.products.Product;
import com.example.cupang.ui.products.ServerApi;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.NumberFormat; // Import ini
import java.util.Locale;     // Import ini

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maindetail extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private ImageView imageViewProduct;
    private TextView textViewProductName, textViewProductDesc, textViewProductPrice;
    private TextView textViewProductStock, textViewProductCategory, textViewProductUnit;
    private TextView textViewProductViews, txtEmail;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindetail);

        // Bind view dari layout
        imageViewProduct       = findViewById(R.id.imageViewProduct);
        textViewProductName    = findViewById(R.id.textViewProductName);
        textViewProductDesc    = findViewById(R.id.textViewProductDesc);
        textViewProductPrice   = findViewById(R.id.textViewProductPrice);
        textViewProductStock   = findViewById(R.id.textViewProductStock);
        textViewProductCategory= findViewById(R.id.textViewProductCategory);
        textViewProductUnit    = findViewById(R.id.textViewProductUnit);
        textViewProductViews   = findViewById(R.id.textViewProductViews);
        txtEmail               = findViewById(R.id.detail);
        btnBack                = findViewById(R.id.btnBack);

        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        // Ambil email dari SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userEmail = prefs.getString("email", "user@email.com");

        // Tampilkan email
        txtEmail.setText("Tertarik dengan Ikan ini " + userEmail + "? Checkout Sekarang yaa!");

        // Ambil data dari intent
        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            // Tampilkan data produk
            textViewProductName.setText(product.getMerk());
            textViewProductDesc.setText(product.getDeskripsi());

            // --- BAGIAN INI YANG DIUBAH UNTUK FORMAT RUPIAH ---
            // Dapatkan harga beli dalam double
            double hargaBeli = product.getHargabeli();

            // Buat formatter untuk Rupiah
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

            // Format harga beli ke string Rupiah
            String hargaRupiah = formatRupiah.format(hargaBeli);

            // Set teks ke TextViewProductPrice
            textViewProductPrice.setText(hargaRupiah);
            // --- AKHIR BAGIAN PERUBAHAN ---

            textViewProductStock.setText("Stok: " + product.getStok());
            textViewProductCategory.setText("Kategori: " + product.getKategori());
            textViewProductUnit.setText("Satuan: " + product.getSatuan());
            textViewProductViews.setText("Dilihat: " + product.getView() + " kali");

            // Load gambar produk
            String imageUrl = "https://allend.site/API_Product/produk/" + product.getFoto();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img)
                    .error(R.drawable.img)
                    .into(imageViewProduct);

            // Kirim request ke API untuk menambah 1 view
            ApiService apiService = ServerApi.getApiService();
            Call<ResponseBody> call = apiService.updateView(product.getKode());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // Penanganan respons (tetap sama)
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}