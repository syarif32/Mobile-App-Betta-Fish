package com.example.cupang.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cupang.ApiClient;
import com.example.cupang.MainActivity;
import com.example.cupang.R;
import com.example.cupang.ui.profile.RegisterAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    private TextView tvUsername, tvEmail;
    private EditText etPassword, etNewPassword, etConfirmPassword;
    private Button btnSubmit;

    private SharedPreferences sharedPreferences;
    private RegisterAPI apiService;
    private String savedPassword, email, username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        apiService = ApiClient.getClient().create(RegisterAPI.class);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        etPassword = findViewById(R.id.etPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmitPassword);

        // Ambil data dari SharedPreferences
        username = sharedPreferences.getString("nama", "Tidak diketahui");
        email = sharedPreferences.getString("email", "Tidak diketahui");
        savedPassword = sharedPreferences.getString("password", ""); // ← Ini penting

        tvUsername.setText("Username: " + username);
        tvEmail.setText("Email: " + email);

        btnSubmit.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String enteredOldPass = etPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(enteredOldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!enteredOldPass.equals(savedPassword)) {
            Toast.makeText(this, "Password lama tidak sesuai", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Password baru dan konfirmasi tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kirim ke server (misal endpoint `resetPassword(email, newPassword)`)
        Call<ResponseBody> call = apiService.resetPassword(email, newPass);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);

                        if (json.getInt("result") == 1) {
                            // Simpan password baru sebelum logout
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("password", newPass);
                            editor.apply();

                            Toast.makeText(ResetPassword.this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                            logoutAndRedirectToLogin();
                        } else {
                            Toast.makeText(ResetPassword.this, "Gagal mengubah password", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("ResetPassword", "Error parsing response", e);
                        Toast.makeText(ResetPassword.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPassword.this, "Gagal merespon server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ResetPassword", "Request error", t);
                Toast.makeText(ResetPassword.this, "Kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutAndRedirectToLogin() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(ResetPassword.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
