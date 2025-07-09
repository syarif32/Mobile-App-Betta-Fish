package com.example.cupang.admin;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // <--- PASTIKAN INI DIIMPORT
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminApiClient {
    // PASTIKAN BASE_URL INI BENAR (IP ANDA DAN AKHIRNYA DENGAN /admin/)
    private static final String BASE_URL = "https://allend.site/API_Product/admin/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 1. Buat interceptor untuk logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Atur level logging ke BODY

            // 2. Buat OkHttpClient dan tambahkan interceptor
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging) // <--- PASTIKAN addInterceptor INI ADA
                    .build();

            // 3. Bangun Retrofit dengan OkHttpClient yang sudah dimodifikasi
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient) // <--- PASTIKAN .client(httpClient) INI ADA
                    .build();
        }
        return retrofit;
    }
}