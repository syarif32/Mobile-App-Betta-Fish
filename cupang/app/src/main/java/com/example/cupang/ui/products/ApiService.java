package com.example.cupang.ui.products;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;

public interface ApiService {
    @GET("get_produk.php")
    Call<List<Product>> getProducts();
    @FormUrlEncoded
    @POST("update_view.php")
    Call<ResponseBody> updateView(@Field("kode") String kode);
    @GET("get_produk_terlaris.php")
    Call<List<Product>> getProdukTerlaris();

    Call<List<Product>> getProductsByKategori(String ci);

}
