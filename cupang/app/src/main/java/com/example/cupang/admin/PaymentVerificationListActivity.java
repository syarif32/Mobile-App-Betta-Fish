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

public class PaymentVerificationListActivity extends AppCompatActivity implements PaymentVerificationAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PaymentVerificationAdapter adapter;
    private List<PaymentVerificationModel> paymentList;
    private AdminApiService adminApiService;
    private LinearLayout emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_verification_list); // Buat layout ini

        Toolbar toolbar = findViewById(R.id.toolbarPaymentVerification); // Toolbar di activity_payment_verification_list.xml
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Verifikasi Pembayaran");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.recyclerViewPendingPayments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateLayout = findViewById(R.id.emptyStateLayoutPayment);

        paymentList = new ArrayList<>();
        adapter = new PaymentVerificationAdapter(this, paymentList, this);
        recyclerView.setAdapter(adapter);

        adminApiService = AdminApiClient.getClient().create(AdminApiService.class);

        loadPendingPayments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingPayments(); // Muat ulang daftar saat Activity kembali
    }

    private void loadPendingPayments() {
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE); // Tampilkan empty state saat loading

        adminApiService.getPendingPayments().enqueue(new Callback<PaymentVerificationListResponse>() {
            @Override
            public void onResponse(@NonNull Call<PaymentVerificationListResponse> call, @NonNull Response<PaymentVerificationListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    List<PaymentVerificationModel> fetchedPayments = response.body().getData();
                    if (fetchedPayments != null && !fetchedPayments.isEmpty()) {
                        paymentList.clear();
                        paymentList.addAll(fetchedPayments);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(PaymentVerificationListActivity.this, "Tidak ada pembayaran menunggu verifikasi.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(PaymentVerificationListActivity.this, "Gagal memuat pembayaran: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("PaymentVerifyList", "Error response: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentVerificationListResponse> call, @NonNull Throwable t) {
                recyclerView.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                Toast.makeText(PaymentVerificationListActivity.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PaymentVerifyList", "Connection error: " + t.getMessage(), t);
            }
        });
    }

    @Override
    public void onViewProofClick(PaymentVerificationModel payment) {
        Intent intent = new Intent(PaymentVerificationListActivity.this, PaymentDetailActivity.class);
        intent.putExtra("ORDER_ID", payment.getIdOrder()); // Kirim ID Order ke detail
        startActivity(intent);
    }
}