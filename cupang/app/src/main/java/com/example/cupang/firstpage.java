package com.example.cupang;

import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class firstpage extends AppCompatActivity {
 Button btnlogin , btnregister , btnk ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fisrtpage);
        // ✅ Inisialisasi tombol
        btnlogin = findViewById(R.id.btnLogin);
        btnregister = findViewById(R.id.btnRegister);
        btnk = findViewById(R.id.btnK);




        // tombol Login ➜ pindah ke MainActivity
        btnlogin.setOnClickListener(v -> {
            Intent intent = new Intent(firstpage.this,MainActivity.class); // ← ganti dengan nama activity login kamu
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Register ➜ pindah ke MainRegister
        btnregister.setOnClickListener(v -> {
            Intent intent = new Intent(firstpage.this, MainRegister.class); // ← ganti dengan nama activity register kamu
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        btnk.setOnClickListener(v -> {
            Intent intent = new Intent(firstpage.this, Home.class);
            intent.putExtra("target", "dashboard"); // Kirim sinyal buka dashboard
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}