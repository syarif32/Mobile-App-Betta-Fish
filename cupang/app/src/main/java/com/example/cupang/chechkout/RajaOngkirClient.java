package com.example.cupang.chechkout;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RajaOngkirClient {
    private static final String BASE_URL = "https://allend.site/API_Product/";
    private static Retrofit retrofit;

    public static RegisterAPI getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(RegisterAPI.class);
    }
}
