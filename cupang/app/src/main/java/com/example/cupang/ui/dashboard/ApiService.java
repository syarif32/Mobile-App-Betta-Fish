package com.example.cupang.ui.dashboard;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("get_produk.php")
    Call<List<Product>> getProducts();

    @GET("get_produk_terlaris.php")
    Call<List<Product>> getProdukTerlaris();
    @FormUrlEncoded
    @POST("update_view.php")
    Call<ResponseBody> updateView(@Field("kode") String kode);
}