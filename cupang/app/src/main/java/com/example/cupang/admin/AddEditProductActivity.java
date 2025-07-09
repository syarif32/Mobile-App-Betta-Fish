package com.example.cupang.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton; // Tambahkan import
import android.widget.RadioGroup; // Tambahkan import
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.cupang.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private EditText etMerk, etSatuan, etHargaBeli, etHargaPokok, etHargaJual, etStok, etDeskripsi;
    private RadioGroup rgKategori; // Deklarasi RadioGroup baru
    private RadioButton radioMitra, radioSiswa; // Deklarasi RadioButton baru

    private ImageView imageViewFoto;
    private Button btnSelectFoto, btnSaveProduct;

    private AdminApiService adminApiService;
    private String productKode = null;
    private Uri selectedImageUri;
    private String currentFotoName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        Toolbar toolbar = findViewById(R.id.toolbarAddEditProduct);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();

        adminApiService = AdminApiClient.getClient().create(AdminApiService.class);

        if (getIntent().hasExtra("PRODUCT_KODE")) {
            productKode = getIntent().getStringExtra("PRODUCT_KODE");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Produk");
            }
            loadProductDetails(productKode);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Tambah Produk");
            }
        }

        btnSelectFoto.setOnClickListener(v -> checkPermissionAndPickImage());
        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        etMerk = findViewById(R.id.et_merk);
        // Hapus: etKategori = findViewById(R.id.et_kategori);
        rgKategori = findViewById(R.id.rg_kategori);
        radioMitra = findViewById(R.id.radio_mitra);
        radioSiswa = findViewById(R.id.radio_siswa);

        etSatuan = findViewById(R.id.et_satuan);
        etHargaBeli = findViewById(R.id.et_hargabeli);
        // Hapus: etDiskonBeli = findViewById(R.id.et_diskonbeli);
        etHargaPokok = findViewById(R.id.et_hargapokok);
        etHargaJual = findViewById(R.id.et_hargajual);
        // Hapus: etDiskonJual = findViewById(R.id.et_diskonjual);
        etStok = findViewById(R.id.et_stok);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        imageViewFoto = findViewById(R.id.imageView_foto);
        btnSelectFoto = findViewById(R.id.btn_select_foto);
        btnSaveProduct = findViewById(R.id.btn_save_product);
    }

    private void loadProductDetails(String kode) {
        adminApiService.getProductDetails(kode).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetailResponse> call, @NonNull Response<ProductDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    ProductDetailAdminModel product = response.body().getData();
                    if (product != null) {
                        etMerk.setText(product.getMerk());


                        if (product.getKategori() != null) {
                            if (product.getKategori().equalsIgnoreCase("Lokal")) {
                                radioMitra.setChecked(true);
                            } else if (product.getKategori().equalsIgnoreCase("Impor")) {
                                radioSiswa.setChecked(true);
                            }
                        }

                        etSatuan.setText(product.getSatuan());
                        etHargaBeli.setText(String.valueOf(product.getHargabeli()));
                        // etDiskonBeli.setText(String.valueOf(product.getDiskonbeli()));
                        etHargaPokok.setText(String.valueOf(product.getHargapokok()));
                        etHargaJual.setText(String.valueOf(product.getHargajual()));
                        //   findViewById(R.id.et_diskonjual)
                        etStok.setText(String.valueOf(product.getStok()));
                        etDeskripsi.setText(product.getDeskripsi());

                        if (product.getFoto() != null && !product.getFoto().isEmpty()) {
                            currentFotoName = product.getFoto();
                            // BARIS INI YANG DIUBAH:
                            // Sebelumnya: String imageUrl = AdminApiClient.getClient().baseUrl() + "images/product/" + currentFotoName;
                            String imageUrl = "https://allend.site/API_Product/produk/" + currentFotoName; //
                            Log.d("AddEditProduct", "Loading image from: " + imageUrl); // Tambahkan log untuk verifikasi
                            Glide.with(AddEditProductActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)
                                    .into(imageViewFoto);
                        }
                    }
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Gagal memuat detail produk.", Toast.LENGTH_SHORT).show();
                    Log.e("AddEditProduct", "Failed to load product details: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddEditProduct", "Network error: " + t.getMessage(), t);
            }
        });
    }

    private void saveProduct() {
        String merk = etMerk.getText().toString().trim();
        String satuan = etSatuan.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // Ambil kategori dari RadioGroup
        String kategori = "";
        int selectedId = rgKategori.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            kategori = selectedRadioButton.getText().toString();
        } else {
            Toast.makeText(this, "Mohon pilih kategori produk.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi input kosong (sederhana)
        if (merk.isEmpty() || satuan.isEmpty() || etHargaJual.getText().toString().isEmpty() || etStok.getText().toString().isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data wajib (Merk, Kategori, Satuan, Harga Jual, Stok).", Toast.LENGTH_LONG).show();
            return;
        }

        double hargabeli = parseDouble(etHargaBeli.getText().toString());
        // Hapus: double diskonbeli = parseDouble(etDiskonBeli.getText().toString());
        double hargapokok = parseDouble(etHargaPokok.getText().toString());
        double hargajual = parseDouble(etHargaJual.getText().toString());
        // Hapus: double diskonjual = parseDouble(etDiskonJual.getText().toString());
        int stok = parseInt(etStok.getText().toString());

        // Buat RequestBody untuk setiap field
        RequestBody merkPart = RequestBody.create(MediaType.parse("text/plain"), merk);
        RequestBody kategoriPart = RequestBody.create(MediaType.parse("text/plain"), kategori);
        RequestBody satuanPart = RequestBody.create(MediaType.parse("text/plain"), satuan);
        RequestBody hargabeliPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hargabeli));
        // Hapus: RequestBody diskonbeliPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(diskonbeli));
        RequestBody hargapokokPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hargapokok));
        RequestBody hargajualPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(hargajual));
        // Hapus: RequestBody diskonjualPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(diskonjual));
        RequestBody stokPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stok));
        RequestBody deskripsiPart = RequestBody.create(MediaType.parse("text/plain"), deskripsi);

        MultipartBody.Part fotoPart = null;
        if (selectedImageUri != null) {
            try {
                File file = uriToFile(selectedImageUri);
                if (file != null && file.exists() && file.length() > 0) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImageUri)), file);
                    fotoPart = MultipartBody.Part.createFormData("foto", getFileName(selectedImageUri), requestFile);
                } else {
                    Toast.makeText(this, "Gagal memproses gambar. File tidak valid.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (IOException e) {
                Log.e("AddEditProduct", "Error converting URI to File: " + e.getMessage());
                Toast.makeText(this, "Error memproses gambar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Call<BaseResponse> call;
        if (productKode == null) { // Mode Tambah
            call = adminApiService.addProduct(merkPart, kategoriPart, satuanPart, hargabeliPart,
                    hargapokokPart, hargajualPart, stokPart, deskripsiPart, fotoPart);
        } else { // Mode Edit
            RequestBody kodePart = RequestBody.create(MediaType.parse("text/plain"), productKode);
            call = adminApiService.editProduct(kodePart, merkPart, kategoriPart, satuanPart, hargabeliPart,
                    hargapokokPart, hargajualPart, stokPart, deskripsiPart, fotoPart);
        }

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("sukses")) {
                    Toast.makeText(AddEditProductActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = "Operasi gagal.";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage();
                    } else if (!response.isSuccessful()) {
                        errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    }
                    Toast.makeText(AddEditProductActivity.this, "Gagal menyimpan: " + errorMessage, Toast.LENGTH_LONG).show();
                    Log.e("AddEditProduct", "Save error: " + errorMessage);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddEditProduct", "Save connection error: " + t.getMessage(), t);
            }
        });
    }

    // --- LOGIKA IZIN DAN PEMILIHAN GAMBAR (Tetap sama seperti di respons sebelumnya) ---
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
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Batal", null)
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
                        .error(R.drawable.logo)
                        .into(imageViewFoto);
                Toast.makeText(this, "Gambar berhasil dipilih.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tidak ada gambar yang dipilih.", Toast.LENGTH_SHORT).show();
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

    private File uriToFile(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        File filesDir = getApplicationContext().getFilesDir();
        String fileName = getFileName(uri);
        if (fileName == null || fileName.isEmpty()) {
            fileName = System.currentTimeMillis() + ".jpg";
        }
        File imageFile = new File(filesDir, fileName);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] bitmapdata = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(imageFile);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();

        bitmap.recycle();

        return imageFile;
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}