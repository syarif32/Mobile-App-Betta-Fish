package com.example.cupang.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderDate, tvCustomerName, tvCustomerEmail, tvCustomerTelp;
    private TextView tvSubtotal, tvOngkir, tvTotalBayar, tvKurirService, tvAlamatKirim;
    private TextView tvStatusOrder;
    private ImageView imageViewBuktiBayar;
    private Button btnApprove, btnReject;
    private RecyclerView recyclerViewOrderProducts;
    private OrderProductItemAdminAdapter productAdapter;

    private AdminApiService adminApiService;
    private int orderId;
    private OrderDetailAdminResponse.OrderInfo currentOrderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        Toolbar toolbar = findViewById(R.id.toolbarPaymentDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Pembayaran");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();

        adminApiService = AdminApiClient.getClient().create(AdminApiService.class);

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            Toast.makeText(this, "ID Order tidak valid.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnApprove.setOnClickListener(v -> confirmVerification("approve"));
        btnReject.setOnClickListener(v -> confirmVerification("reject"));
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tv_detail_order_id);
        tvOrderDate = findViewById(R.id.tv_detail_order_date);
        tvCustomerName = findViewById(R.id.tv_detail_customer_name);
        tvCustomerEmail = findViewById(R.id.tv_detail_customer_email);
        tvCustomerTelp = findViewById(R.id.tv_detail_customer_telp);
        tvSubtotal = findViewById(R.id.tv_detail_subtotal);
        tvOngkir = findViewById(R.id.tv_detail_ongkir);
        tvTotalBayar = findViewById(R.id.tv_detail_total_bayar);
        tvKurirService = findViewById(R.id.tv_detail_kurir_service);
        tvAlamatKirim = findViewById(R.id.tv_detail_alamat_kirim);
        tvStatusOrder = findViewById(R.id.tv_detail_status_order);
        imageViewBuktiBayar = findViewById(R.id.imageView_bukti_bayar_detail);
        btnApprove = findViewById(R.id.btn_approve_payment);
        btnReject = findViewById(R.id.btn_reject_payment);

        recyclerViewOrderProducts = findViewById(R.id.recyclerView_order_products);
        recyclerViewOrderProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new OrderProductItemAdminAdapter(this, new ArrayList<>());
        recyclerViewOrderProducts.setAdapter(productAdapter);
    }

    private void loadOrderDetails(int idOrder) {
        adminApiService.getOrderDetailsForAdmin(idOrder).enqueue(new Callback<OrderDetailAdminResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailAdminResponse> call, @NonNull Response<OrderDetailAdminResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    currentOrderInfo = response.body().getData().getOrderInfo();
                    List<OrderProductItemAdminModel> products = response.body().getData().getProducts();
                    OrderDetailAdminResponse.PelangganInfo pelanggan = response.body().getData().getPelangganInfo();

                    if (currentOrderInfo != null) {
                        tvOrderId.setText("Order ID: #" + currentOrderInfo.getIdOrder());
                        tvOrderDate.setText(currentOrderInfo.getTglOrder());

                        if (pelanggan != null) {
                            tvCustomerName.setText("Nama: " + (pelanggan.getNama() != null ? pelanggan.getNama() : "-"));
                            tvCustomerEmail.setText("Email: " + (pelanggan.getEmail() != null ? pelanggan.getEmail() : "-"));
                            tvCustomerTelp.setText("Telp: " + (pelanggan.getTelp() != null ? pelanggan.getTelp() : "-"));
                        } else {
                            tvCustomerName.setText("Nama: N/A");
                            tvCustomerEmail.setText("Email: N/A");
                            tvCustomerTelp.setText("Telp: N/A");
                        }

                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
                        tvSubtotal.setText("Subtotal: " + formatRupiah.format(currentOrderInfo.getSubtotal()));
                        tvOngkir.setText("Ongkir: " + formatRupiah.format(currentOrderInfo.getOngkir()));
                        tvTotalBayar.setText("Total Bayar: " + formatRupiah.format(currentOrderInfo.getTotalBayar()));
                        tvKurirService.setText("Kurir: " + currentOrderInfo.getKurir() + " - " + currentOrderInfo.getService());

                        String alamatLengkap = currentOrderInfo.getAlamatKirim();
                        if (currentOrderInfo.getKota() != null && !currentOrderInfo.getKota().isEmpty()) {
                            alamatLengkap += ", " + currentOrderInfo.getKota();
                        }
                        if (currentOrderInfo.getProvinsi() != null && !currentOrderInfo.getProvinsi().isEmpty()) {
                            alamatLengkap += ", " + currentOrderInfo.getProvinsi();
                        }
                        if (currentOrderInfo.getKodepos() != null && !currentOrderInfo.getKodepos().isEmpty()) {
                            alamatLengkap += " " + currentOrderInfo.getKodepos();
                        }
                        tvAlamatKirim.setText("Alamat: " + alamatLengkap);
                        tvStatusOrder.setText("Status Order: " + currentOrderInfo.getFormattedStatus());

                        // KOREKSI PATH GAMBAR BUKTI BAYAR DI SINI
                        // Harus mengonversi HttpUrl ke String terlebih dahulu
                        String adminBaseUrlString = AdminApiClient.getClient().baseUrl().toString(); // Konversi HttpUrl ke String
                        String baseUrlRoot = adminBaseUrlString.replace("admin/", ""); // Menghilangkan "admin/" dari URL
                        String imageUrl = baseUrlRoot + "bukti_bayar/" + currentOrderInfo.getBuktiBayar(); // Menambahkan "bukti_bayar/"
                        Log.d("PaymentDetail", "Loading bukti bayar from: " + imageUrl);

                        Glide.with(PaymentDetailActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.logo)
                                .error(R.drawable.logo)
                                .into(imageViewBuktiBayar);


                        // Tampilkan tombol approve/reject hanya jika statusnya Menunggu Konfirmasi (status 1)
                        if (currentOrderInfo.getStatus().equals("1")) {
                            btnApprove.setVisibility(View.VISIBLE);
                            btnReject.setVisibility(View.VISIBLE);
                        } else {
                            btnApprove.setVisibility(View.GONE);
                            btnReject.setVisibility(View.GONE);
                        }
                    }

                    if (products != null) {
                        productAdapter.setProducts(products);
                    }

                } else {
                    Toast.makeText(PaymentDetailActivity.this, "Gagal memuat detail order: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("PaymentDetail", "Error response: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailAdminResponse> call, @NonNull Throwable t) {
                Toast.makeText(PaymentDetailActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PaymentDetail", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void confirmVerification(String action) {
        String title = action.equals("approve") ? "Setujui Pembayaran" : "Tolak Pembayaran";
        String message = action.equals("approve") ? "Apakah Anda yakin ingin menyetujui pembayaran order ini?" : "Apakah Anda yakin ingin menolak pembayaran order ini? Order akan dibatalkan.";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(action.equals("approve") ? "Setujui" : "Tolak", (dialog, which) -> verifyPayment(action))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void verifyPayment(String action) {
        if (currentOrderInfo == null) {
            Toast.makeText(this, "Data order tidak tersedia.", Toast.LENGTH_SHORT).show();
            return;
        }

        adminApiService.verifyPayment(currentOrderInfo.getIdOrder(), action).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    Toast.makeText(PaymentDetailActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = "Operasi gagal.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (!response.isSuccessful()) {
                        errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    }
                    Toast.makeText(PaymentDetailActivity.this, "Gagal verifikasi: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("PaymentDetail", "Verification error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Toast.makeText(PaymentDetailActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PaymentDetail", "Verification connection error: " + t.getMessage(), t);
            }
        });
    }
}