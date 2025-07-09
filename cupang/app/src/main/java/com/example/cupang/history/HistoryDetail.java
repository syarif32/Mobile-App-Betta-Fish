package com.example.cupang.history;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View; // Import ini untuk View.GONE/VISIBLE
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryDetail extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private TextView detailOrderId, detailOrderDate, detailOrderStatus, detailOrderTotal;
    private TextView detailKurir, detailOngkir, detailTujuan, detailAlamat;
    private TextView detailJenisPembayaran; // *** BARIS BARU DITAMBAHKAN ***

    private RecyclerView recyclerViewProductsInOrder;
    private ProductInOrderAdapter productInOrderAdapter;
    private ImageView imageViewPaymentProof;
    private Button buttonSelectImage, buttonUploadPaymentDetails;

    private int orderId;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();

        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderDetail(orderId);
        } else {
            Toast.makeText(this, "ID Pesanan tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonSelectImage.setOnClickListener(v -> checkPermissionAndPickImage());
        buttonUploadPaymentDetails.setOnClickListener(v -> uploadPaymentProof());
    }

    private void initViews() {
        detailOrderId = findViewById(R.id.detailOrderId);
        detailOrderDate = findViewById(R.id.detailOrderDate);
        detailOrderStatus = findViewById(R.id.detailOrderStatus);
        detailOrderTotal = findViewById(R.id.detailOrderTotal);

        detailKurir = findViewById(R.id.kurir);
        detailOngkir = findViewById(R.id.ongkir);
        detailTujuan = findViewById(R.id.tujuan);
        detailAlamat = findViewById(R.id.detailAlamat);
        detailJenisPembayaran = findViewById(R.id.JenisPembayaran); // *** BARIS BARU DITAMBAHKAN ***

        recyclerViewProductsInOrder = findViewById(R.id.recyclerViewProductsInOrder);
        recyclerViewProductsInOrder.setLayoutManager(new LinearLayoutManager(this));

        imageViewPaymentProof = findViewById(R.id.imageViewPaymentProof);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonUploadPaymentDetails = findViewById(R.id.buttonUploadPaymentDetails);
    }

    private void loadOrderDetail(int idOrder) {
        APIService apiService = ApiClient.getClient().create(APIService.class);
        apiService.getOrderDetail(idOrder).enqueue(new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<OrderDetailResponse> call, @NonNull Response<OrderDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    OrderDetailResponse orderDetail = response.body();
                    displayOrderDetail(orderDetail);
                } else {
                    Toast.makeText(HistoryDetail.this, "Gagal memuat detail pesanan: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("HistoryDetail", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(HistoryDetail.this, "Gagal koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("HistoryDetailError", "Error loading order detail: " + t.getMessage(), t);
            }
        });
    }

    private void displayOrderDetail(OrderDetailResponse orderDetail) {
        detailOrderId.setText("#ORD" + orderDetail.getIdOrder());
        detailOrderDate.setText(orderDetail.getTglOrder());
        detailOrderStatus.setText(mapStatus(orderDetail.getStatusOrder()));

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        detailOrderTotal.setText(formatRupiah.format(orderDetail.getTotalBayar()));

        detailKurir.setText(orderDetail.getKurir() != null ? orderDetail.getKurir() : "-");
        detailOngkir.setText(formatRupiah.format(orderDetail.getOngkir()));
        detailTujuan.setText(orderDetail.getKurir() + " - " + (orderDetail.getService() != null ? orderDetail.getService() : ""));
        detailAlamat.setText(orderDetail.getAlamatKirim() != null ? orderDetail.getAlamatKirim() : "-");

        // *** LOGIKA UNTUK JENIS PEMBAYARAN DAN KONDISI UPLOAD BUKTI BAYAR ***
        String paymentMethodText = "Tidak Diketahui";
        switch (orderDetail.getMetodebayar()) {
            case 0:
                paymentMethodText = "COD (Cash On Delivery)";
                break;
            case 1:
                paymentMethodText = "Transfer Bank BNI";
                break;
            case 2:
                paymentMethodText = "Transfer Bank BCA";
                break;
            case 3:
                paymentMethodText = "Transfer Bank BRI";
                break;
            default:
                paymentMethodText = "Metode Pembayaran Tidak Dikenali";
                break;
        }
        detailJenisPembayaran.setText(paymentMethodText);

        if (orderDetail.getProducts() != null) {
            productInOrderAdapter = new ProductInOrderAdapter(this, orderDetail.getProducts());
            recyclerViewProductsInOrder.setAdapter(productInOrderAdapter);
        }

        if (orderDetail.getMetodebayar() == 0) {
            imageViewPaymentProof.setVisibility(View.GONE);
            buttonSelectImage.setVisibility(View.GONE);
            buttonUploadPaymentDetails.setVisibility(View.GONE);

        } else {
            imageViewPaymentProof.setVisibility(View.VISIBLE);
            buttonSelectImage.setVisibility(View.VISIBLE);
            buttonUploadPaymentDetails.setVisibility(View.VISIBLE);

            if (orderDetail.getBuktiBayar() != null && !orderDetail.getBuktiBayar().isEmpty()) {
                String imageUrl = "https://allend.site/API_Product/bukti_bayar/" + orderDetail.getBuktiBayar();
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .into(imageViewPaymentProof);
                buttonSelectImage.setEnabled(false); // Bukti bayar sudah diupload
                buttonUploadPaymentDetails.setEnabled(false); // Bukti bayar sudah diupload
            } else {
                if (orderDetail.getStatusOrder().equals("0")) {
                    buttonSelectImage.setEnabled(true);
                    buttonUploadPaymentDetails.setEnabled(false); // Upload hanya aktif setelah gambar dipilih di onActivityResult
                } else {
                    buttonSelectImage.setEnabled(false);
                    buttonUploadPaymentDetails.setEnabled(false);
                }
            }
        }
    }

    private String mapStatus(String status) {
        switch (status) {
            case "0":
                return "Menunggu Pembayaran";
            case "1":
                return "Pembayaran Diverifikasi";
            case "2":
                return "Order Berhasil";
            case "3":
                return "Dikirim";
            case "4":
                return "Selesai"; //
            case "5":
                return "Dibatalkan";
            default:
                return "Status Tidak Diketahui";
        }
    }

    // --- KODE LAINNYA YANG TIDAK BERUBAH ---

    private void checkPermissionAndPickImage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_CODE);
            } else {
                openImageChooserFromGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            } else {
                openImageChooserFromGallery();
            }
        }
    }

    private void openImageChooserFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
            } else {
                allPermissionsGranted = false;
            }

            if (allPermissionsGranted) {
                openImageChooserFromGallery();
            } else {
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (!showRationale) {
                    showPermissionDeniedDialog();
                } else {
                    Toast.makeText(this, "Izin penyimpanan ditolak. Tidak dapat memilih gambar.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Izin Diperlukan")
                .setMessage("Aplikasi memerlukan izin penyimpanan untuk memilih gambar. Mohon berikan izin di Pengaturan Aplikasi.")
                .setPositiveButton("Buka Pengaturan", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.logo)
                        .into(imageViewPaymentProof);
                buttonUploadPaymentDetails.setEnabled(true);
                Toast.makeText(this, "Gambar berhasil dipilih.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPaymentProof() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Silakan pilih gambar bukti pembayaran terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = getFileName(selectedImageUri);
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] fileBytes = getBytes(inputStream);

            if (fileBytes == null || fileBytes.length == 0) {
                Toast.makeText(this, "Gagal memproses gambar. File kosong.", Toast.LENGTH_SHORT).show();
                Log.e("UploadProof", "Processed file bytes are null or empty.");
                return;
            }

            Log.d("UploadProof", "File to upload: " + fileName + ", size: " + fileBytes.length + " bytes");

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("bukti_bayar", fileName, requestFile);
            RequestBody orderIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(orderId));

            APIService apiService = ApiClient.getClient().create(APIService.class);
            apiService.uploadPaymentProof(orderIdPart, body).enqueue(new Callback<UploadProofResponse>() {
                @Override
                public void onResponse(@NonNull Call<UploadProofResponse> call, @NonNull Response<UploadProofResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                        Toast.makeText(HistoryDetail.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        loadOrderDetail(orderId); // Muat ulang detail untuk update UI
                    } else {
                        String errorMessage = "Gagal mengunggah bukti pembayaran.";
                        if (response.body() != null && response.body().getMessage() != null) {
                            errorMessage = "Gagal mengunggah bukti pembayaran: " + response.body().getMessage();
                        } else if (!response.isSuccessful()) {
                            errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                        }
                        Toast.makeText(HistoryDetail.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("UploadProof", "Response error: " + errorMessage);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UploadProofResponse> call, @NonNull Throwable t) {
                    Toast.makeText(HistoryDetail.this, "Gagal koneksi saat mengunggah: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadProofError", "Error uploading payment proof: " + t.getMessage(), t);
                }
            });

        } catch (Exception e) {
            Log.e("UploadProofError", "Error preparing image for upload: " + e.getMessage(), e);
            Toast.makeText(this, "Error mempersiapkan gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}