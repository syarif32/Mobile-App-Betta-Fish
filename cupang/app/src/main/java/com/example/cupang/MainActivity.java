package com.example.cupang;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cupang.admin.AdminHomeActivity;
import com.example.cupang.admin.ProductListActivity; // Import halaman admin
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etUserNam, etPasswor;
    private Button btnLogin;
    private TextView tv;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("isLoggedIn", false) && !ADMIN_EMAIL.equals(sharedPreferences.getString("email", ""))) {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return;
        }
        setContentView(R.layout.activity_main);
        etUserNam = findViewById(R.id.etUsername);
        etPasswor = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tv = findViewById(R.id.tvLogin);
        tv.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainRegister.class));
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        btnLogin.setOnClickListener(v -> {
            String username = etUserNam.getText().toString().trim();
            String password = etPasswor.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showDialog("Login Gagal", "Username dan Password wajib diisi!");
                return;
            }
            prosesLogin(username, password);
        });
    }
    private void prosesLogin(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.login(username, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    showDialog("Login Gagal", "Server error. Silakan coba lagi.");
                    return;
                }

                try {
                    String jsonResponse = response.body() != null ? response.body().string() : "";
                    JSONObject json = new JSONObject(jsonResponse);
                    Log.d("Login JSON", json.toString());

                    if (json.optInt("result", 0) == 1) {
                        JSONObject data = json.optJSONObject("data");

                        if (data != null && data.optInt("status", 0) == 1) {
                            // Ambil data
                            int id = data.optInt("id", -1);
                            String nama = data.optString("nama", "Guest");
                            String email = data.optString("email", "No Email");

                            Log.d("Login Data", "ID: " + id + " Nama: " + nama + " Email: " + email);

                            // Cek valid ID
                            if (id == -1) {
                                showDialog("Login Gagal", "ID pelanggan tidak valid.");
                                return;
                            }

                            SharedPreferences cartPrefs = getSharedPreferences("cart_pref", MODE_PRIVATE);
                            cartPrefs.edit().remove("cart_list").apply();
                            if (ADMIN_EMAIL.equals(email)) {
                                new MaterialAlertDialogBuilder(MainActivity.this)
                                        .setTitle("Login Berhasil (Admin)")
                                        .setMessage("Selamat datang, Admin!")
                                        .setPositiveButton("Lanjut ke Admin Panel", (dialog, which) -> {
                                            Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                        })
                                        .show();
                            } else {
                                editor.putInt("id", id);
                                editor.putString("nama", nama);
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                new MaterialAlertDialogBuilder(MainActivity.this)
                                        .setTitle("Login Berhasil")
                                        .setMessage("Selamat datang, " + nama)
                                        .setPositiveButton("Lanjut", (dialog, which) -> {
                                            Intent intent = new Intent(MainActivity.this, Home.class);
                                            intent.putExtra("nama", nama);
                                            intent.putExtra("email", email);
                                            startActivity(intent);
                                            finish();
                                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                        })
                                        .show();
                            }
                        } else {
                            showDialog("Status Tidak Aktif", "Akun Anda belum aktif.");
                        }
                    } else {
                        showDialog("Login Gagal", "Username atau Password salah!");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showDialog("Error", "Gagal memproses data.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("LOGIN_ERROR", t.getMessage());
                showDialog("Koneksi Gagal", "Periksa koneksi internet Anda.");
            }
        });
    }

    private void showDialog(String title, String message) {
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}