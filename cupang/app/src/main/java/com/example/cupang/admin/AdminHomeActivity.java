package com.example.cupang.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.cupang.MainActivity; // Import MainActivity untuk logout
import com.example.cupang.R; // Pastikan R diimpor dengan benar

public class AdminHomeActivity extends AppCompatActivity {

    private CardView cardManageProducts;
    private CardView cardVerifyPayments;
    private CardView cardOrderHistory; // New CardView
    private Button btnLogoutAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Toolbar toolbar = findViewById(R.id.toolbarAdminHome);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        cardManageProducts = findViewById(R.id.cardManageProducts);
        cardVerifyPayments = findViewById(R.id.cardVerifyPayments);
        cardOrderHistory = findViewById(R.id.cardOrderHistory); // Initialize the new CardView
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);
    }

    private void setupClickListeners() {
        cardManageProducts.setOnClickListener(v -> {
            // Navigasi ke halaman Manajemen Produk
            startActivity(new Intent(AdminHomeActivity.this, ProductListActivity.class));
        });

        cardVerifyPayments.setOnClickListener(v -> {
            // Navigasi ke halaman Verifikasi Pembayaran
            startActivity(new Intent(AdminHomeActivity.this, PaymentVerificationListActivity.class));
        });

        cardOrderHistory.setOnClickListener(v -> { // Set click listener for the new CardView
            // Navigasi ke halaman Riwayat Order
            startActivity(new Intent(AdminHomeActivity.this, OrderHistoryActivity.class));
        });

        btnLogoutAdmin.setOnClickListener(v -> {
            // Logika logout admin
            // Karena sesi admin tidak disimpan di SharedPreferences (sesuai permintaan Anda),
            // kita hanya perlu mengarahkan kembali ke MainActivity dan mengakhiri AdminHomeActivity.
            // Tidak perlu menghapus shared preferences di sini untuk admin.

            Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus semua activity sebelumnya
            startActivity(intent);
            finish();
        });
    }

    // Optional: Override onBackPressed jika Anda ingin membatasi admin agar tidak bisa kembali ke MainActivity dari home admin
    @Override
    public void onBackPressed() {
        // Biarkan kosong agar tombol back tidak berfungsi,
        // atau tampilkan dialog konfirmasi keluar aplikasi.
        // super.onBackPressed(); // Jika ingin tombol back tetap berfungsi normal
    }
}