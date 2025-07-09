package com.example.cupang;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainRegister extends AppCompatActivity {

    private TextView tvBack;
    private EditText etNama, etEmail, etPassword;
    private Button but;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        tvBack = findViewById(R.id.tvMessage);
        etNama = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        but = findViewById(R.id.btnSubmit);

        tvBack.setOnClickListener(view -> {
            Intent intent = new Intent(MainRegister.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        but.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String nama = etNama.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            processSubmit(email, nama, password);
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showDialog(String title, String message) {
        new MaterialAlertDialogBuilder(MainRegister.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void processSubmit(String email, String nama, String password) {
        if (!isEmailValid(email)) {
            showDialog("Email Tidak Valid", "Silakan periksa kembali email Anda.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        api.register(email, nama, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    JSONObject json = new JSONObject(responseBody);

                    if (json.getString("status").equals("1")) {
                        if (json.getString("result").equals("1")) {
                            new MaterialAlertDialogBuilder(MainRegister.this)
                                    .setTitle("Berhasil")
                                    .setMessage("Register berhasil!")
                                    .setPositiveButton("OK", (dialog, which) -> {
                                        dialog.dismiss();
                                        etEmail.setText("");
                                        etNama.setText("");
                                        etPassword.setText("");
                                    })
                                    .show();
                        } else {
                            showDialog("Gagal", "Simpan gagal!");
                        }
                    } else {
                        showDialog("Peringatan", "User sudah ada.");
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showDialog("Error", "Terjadi kesalahan dalam proses.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("Info Register", "Register Gagal: " + t.toString());
                showDialog("Gagal", "Tidak dapat terhubung ke server.");
            }
        });
    }
}
