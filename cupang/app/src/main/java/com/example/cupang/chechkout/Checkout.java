package com.example.cupang.chechkout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cupang.R;
import com.example.cupang.history.History;
import com.example.cupang.ui.dashboard.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject; // Not directly used for parsing in your current code, but kept for consistency

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;

import retrofit2.converter.gson.GsonConverterFactory; // Kept as it's used in your existing Retrofit setup

public class Checkout extends AppCompatActivity {

    private List<Product> productList;
    private List<provinsi> provinsiList; // Not directly used after initial load, but kept
    private List<kota> kotaList; // Not directly used after initial load, but kept

    private TextView txtTotalProduk, txtTotalBayar, txtOngkir, txtUsername, txtEmail;
    private Spinner spinnerProvinsi, spinnerKota, spinnerKurir;
    private EditText editAlamatLengkap; // Correctly identified
    private EditText edittelptujuan; // Added reference for existing EditText
    private EditText editKodePos; // Added reference for existing EditText
    private RadioGroup radioGroupPembayaran; // Added reference for existing RadioGroup

    private double totalProduk, ongkir;
    private HashMap<String, String> mapIdProvinsi = new HashMap<>();
    private HashMap<String, String> mapIdKota = new HashMap<>();

    private RegisterAPI registerAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Inisialisasi UI
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        txtTotalProduk = findViewById(R.id.txtTotalProduk);
        txtTotalBayar = findViewById(R.id.txtTotalBayar);
        txtOngkir = findViewById(R.id.txtOngkir);
        spinnerProvinsi = findViewById(R.id.spinnerProvinsi);
        spinnerKota = findViewById(R.id.spinnerKota);
        spinnerKurir = findViewById(R.id.spinnerKurir);
        editAlamatLengkap = findViewById(R.id.editAlamatLengkap);
        edittelptujuan = findViewById(R.id.edittelptujuan); // Initialize the phone number EditText
        editKodePos = findViewById(R.id.editKodePos); // Initialize the postal code EditText
        radioGroupPembayaran = findViewById(R.id.radioGroupPembayaran); // Initialize the RadioGroup

        Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(v -> prosesCheckout());


        // Setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://allend.site/API_Product/") //
                .addConverterFactory(GsonConverterFactory.create()) //
                .build(); //
        registerAPI = retrofit.create(RegisterAPI.class); //

        loadKeranjang(); //
        loadProvinsi(); //

        spinnerProvinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String idProv = mapIdProvinsi.get(spinnerProvinsi.getSelectedItem().toString());
                if (idProv != null) {
                    loadKota(idProv); //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerKota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Only trigger if there's a valid selection
                if (parent.getSelectedItem() != null) {
                    hitungOngkir(); //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        List<String> kurirList = Arrays.asList("tiki", "jne"); //
        spinnerKurir.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, kurirList)); //
        spinnerKurir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerKota.getSelectedItem() != null) {
                    hitungOngkir(); //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadKeranjang() {
        SharedPreferences pref = getSharedPreferences("cart_pref", MODE_PRIVATE); //
        String json = pref.getString("cart_list", "[]"); //

        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE); //
        String username = userPrefs.getString("nama", "Guest"); //
        String email = userPrefs.getString("email", "guest@example.com"); //

        txtUsername.setText("Username: " + username); //
        txtEmail.setText("Email: " + email); //

        Gson gson = new Gson(); //
        Type type = new TypeToken<List<com.example.cupang.ui.dashboard.Product>>() {}.getType(); //
        List<com.example.cupang.ui.dashboard.Product> dashboardProductList = gson.fromJson(json, type); //
        productList = convertToProducts(dashboardProductList); //

        RecyclerView recyclerView = findViewById(R.id.recyclerKeranjang); //
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //
        recyclerView.setAdapter(new CheckoutAdapter(this, productList)); //

        totalProduk = 0; //
        for (Product p : productList) { //
            totalProduk += p.getHargabeli() * p.getQuantity(); //
        }

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID")); //

        txtTotalProduk.setText("Total Produk: " + formatRupiah.format(totalProduk)); //
        txtTotalBayar.setText("Total Bayar: " + formatRupiah.format(totalProduk)); //
    }

    private void loadProvinsi() {
        registerAPI.getProvinsi().enqueue(new Callback<ProvinsiRespone>() { //
            @Override
            public void onResponse(Call<ProvinsiRespone> call, Response<ProvinsiRespone> response) {
                if (response.isSuccessful()) { //
                    List<String> namaProvinsi = new ArrayList<>(); //
                    for (provinsi p : response.body().getResults()) { //
                        namaProvinsi.add(p.getNama_provinsi()); //
                        mapIdProvinsi.put(p.getNama_provinsi(), p.getId_provinsi()); //
                    }
                    spinnerProvinsi.setAdapter(new ArrayAdapter<>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, namaProvinsi)); //
                } else {
                    Toast.makeText(Checkout.this, "Gagal parse provinsi", Toast.LENGTH_SHORT).show(); //
                }
            }

            @Override
            public void onFailure(Call<ProvinsiRespone> call, Throwable t) {
                Toast.makeText(Checkout.this, "Gagal load provinsi: " + t.getMessage(), Toast.LENGTH_SHORT).show(); //
            }
        });
    }

    private void loadKota(String idProvinsi) {
        registerAPI.getKota(idProvinsi).enqueue(new Callback<KotaResponse>() { //
            @Override
            public void onResponse(Call<KotaResponse> call, Response<KotaResponse> response) {
                if (response.isSuccessful()) { //
                    List<String> namaKota = new ArrayList<>(); //
                    mapIdKota.clear(); //
                    for (kota k : response.body().getResults()) { //
                        namaKota.add(k.getNama_kota()); //
                        mapIdKota.put(k.getNama_kota(), k.getId_kota()); //
                    }
                    spinnerKota.setAdapter(new ArrayAdapter<>(Checkout.this, android.R.layout.simple_spinner_dropdown_item, namaKota)); //
                } else {
                    Toast.makeText(Checkout.this, "Gagal parsing kota", Toast.LENGTH_SHORT).show(); //
                }
            }

            @Override
            public void onFailure(Call<KotaResponse> call, Throwable t) {
                Toast.makeText(Checkout.this, "Gagal load kota: " + t.getMessage(), Toast.LENGTH_SHORT).show(); //
            }
        });
    }

    private void hitungOngkir() {
        try {
            String kotaSelected = spinnerKota.getSelectedItem().toString(); //
            String tujuan = mapIdKota.get(kotaSelected); //

            if (tujuan == null) { //
                Toast.makeText(this, "Data kota belum tersedia", Toast.LENGTH_SHORT).show(); //
                return; //
            }

            String asal = "78"; // ID Kota Semarang
            String kurir = spinnerKurir.getSelectedItem().toString(); //
            int berat = 1000; //

            registerAPI.getOngkir(asal, tujuan, berat, kurir).enqueue(new Callback<ResponseBody>() { //
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String jsonStr = response.body().string(); //
                        JSONObject jsonObject = new JSONObject(jsonStr); //
                        JSONObject result = jsonObject.getJSONObject("rajaongkir") //
                                .getJSONArray("results").getJSONObject(0) //
                                .getJSONArray("costs").getJSONObject(0); //

                        String layanan = result.getString("service"); //
                        JSONObject cost = result.getJSONArray("cost").getJSONObject(0); //
                        int value = cost.getInt("value"); //
                        String etd = cost.getString("etd"); //

                        ongkir = value; //

                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID")); //
                        txtOngkir.setText("Layanan: " + layanan + "\nOngkir: " + formatRupiah.format(ongkir) + "\nEstimasi: " + etd + " hari"); //
                        txtTotalBayar.setText("Total Bayar: " + formatRupiah.format(totalProduk + ongkir)); //
                    } catch (Exception e) {
                        txtOngkir.setText("Gagal parsing ongkir"); //
                        Log.e("Checkout", "Error parsing ongkir: " + e.getMessage()); //
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    txtOngkir.setText("Gagal koneksi: " + t.getMessage()); //
                    Log.e("Checkout", "Error getOngkir: " + t.getMessage()); //
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(this, "Pilih kota dan kurir terlebih dahulu", Toast.LENGTH_SHORT).show(); //
            Log.e("Checkout", "NullPointerException in hitungOngkir: " + e.getMessage()); //
        }
    }

    private List<Product> convertToProducts(List<com.example.cupang.ui.dashboard.Product> dashboardProducts) {
        List<Product> products = new ArrayList<>();
        for (com.example.cupang.ui.dashboard.Product dp : dashboardProducts) {
            Product p = new Product();
            p.setKode(dp.getKode());
            p.setMerk(dp.getMerk());
            p.setHargabeli(dp.getHargabeli());
            p.setQuantity(dp.getQuantity());
            p.setFoto(dp.getFoto());
            p.setStok(0); // Default, since 'stok' is not in dashboard Product
            p.setDeskripsi(""); // Default, since 'deskripsi' is not in dashboard Product
            products.add(p);
        }
        return products;
    }

    private void prosesCheckout() {
        // Ambil data dari SharedPreferences
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE); //
        String idPelanggan = String.valueOf(userPrefs.getInt("id", -1)); //

        // Validasi data
        if (idPelanggan.isEmpty() || spinnerKota.getSelectedItem() == null || editAlamatLengkap.getText().toString().isEmpty()) { //
            Toast.makeText(this, "Lengkapi semua data pengiriman!", Toast.LENGTH_SHORT).show(); //
            return; //
        }

        // Ambil nilai dari EditText yang baru diinisialisasi
        String telpKirim = edittelptujuan.getText().toString();
        String kodePos = editKodePos.getText().toString();

        // Ambil nilai metode pembayaran
        int selectedPaymentMethodId = radioGroupPembayaran.getCheckedRadioButtonId();
        int metodeBayar = -1; // Default value if nothing is selected or unknown

        if (selectedPaymentMethodId != -1) {
            if (selectedPaymentMethodId == R.id.radioCOD) {
                metodeBayar = 0;
            } else if (selectedPaymentMethodId == R.id.radioBNI) {
                metodeBayar = 1;
            } else if (selectedPaymentMethodId == R.id.radioBCA) {
                metodeBayar = 2;
            } else if (selectedPaymentMethodId == R.id.radioBRI) {
                metodeBayar = 3;
            }
        } else {
            Toast.makeText(this, "Pilih metode pembayaran!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Siapkan payload
        Map<String, Object> data = new HashMap<>();
        data.put("id_pelanggan", idPelanggan); //
        data.put("tgl_order", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())); //
        data.put("subtotal", totalProduk); //
        data.put("ongkir", ongkir); //
        data.put("total_bayar", totalProduk + ongkir); //
        data.put("kurir", spinnerKurir.getSelectedItem().toString()); //
        data.put("service", "REG"); //
        data.put("asal", "78"); //
        data.put("tujuan", mapIdKota.get(spinnerKota.getSelectedItem().toString())); //
        data.put("alamat_kirim", editAlamatLengkap.getText().toString()); //
        data.put("telp_kirim", telpKirim); // Menggunakan nilai dari edittelptujuan
        data.put("kodepos", kodePos); // Menambahkan nilai kode pos
        data.put("metodebayar", metodeBayar); // Menambahkan metode pembayaran

        List<Map<String, Object>> produkListPayload = new ArrayList<>();
        for (Product p : productList) {
            Map<String, Object> item = new HashMap<>();
            item.put("kode_product", p.getKode()); //
            item.put("harga_satuan", p.getHargabeli()); //
            item.put("qty", p.getQuantity()); //
            produkListPayload.add(item); //
        }
        data.put("produk", produkListPayload); //

        // Konversi ke JSON
        Gson gson = new Gson(); //
        String json = gson.toJson(data); //

        Log.d("Checkout Data", "Data JSON yang akan dikirim: " + json); //

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json); //

        // Kirim request
        registerAPI.checkout(body).enqueue(new Callback<ResponseBody>() { //
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String res = response.body().string(); //
                    JsonObject obj = new JsonParser().parse(res).getAsJsonObject(); //

                    if (obj.get("status").getAsString().equals("sukses")) { //
                        // Berhasil: Kosongkan keranjang
                        getSharedPreferences("cart_pref", MODE_PRIVATE).edit() //
                                .remove("cart_list") //
                                .apply(); //

                        // Tampilkan dialog dengan dua opsi
                        AlertDialog.Builder builder = new AlertDialog.Builder(Checkout.this); //
                        builder.setTitle("Checkout Berhasil"); //

                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID")); //
                        String totalBayarFormatted = formatRupiah.format(obj.get("total_bayar").getAsDouble()); //
                        builder.setMessage("ID Order: " + obj.get("id_order").getAsString() + //
                                "\nTotal: " + totalBayarFormatted); //

                        builder.setPositiveButton("Bayar", (dialog, which) -> { //
                            Toast.makeText(Checkout.this, "Menuju ke halaman pembayaran", Toast.LENGTH_SHORT).show(); //
                            Intent intent = new Intent(Checkout.this, History.class); //
                            intent.putExtra("ORDER_ID", obj.get("id_order").getAsString()); //
                            startActivity(intent); //
                            finish(); //
                        });

                        builder.setNegativeButton("OK", (dialog, which) -> { //
                            finish(); // Tutup activity checkout
                        });

                        builder.setCancelable(false); // Mencegah dialog ditutup dengan klik di luar
                        builder.show(); //

                    } else {
                        Toast.makeText(Checkout.this, "Error: " + obj.get("message").getAsString(), Toast.LENGTH_LONG).show(); //
                    }
                } catch (Exception e) {
                    Toast.makeText(Checkout.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show(); //
                    Log.e("Checkout", "Error parsing checkout response: " + e.getMessage()); //
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Checkout.this, "Gagal terhubung ke server: " + t.getMessage(), Toast.LENGTH_LONG).show(); //
                Log.e("Checkout", "Error checkout API call: " + t.getMessage(), t); //
            }
        });
    }
}