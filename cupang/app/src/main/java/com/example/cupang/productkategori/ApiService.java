package com.example.cupang.productkategori;

import com.example.cupang.model.OrderHistoryItem;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Field;
import retrofit2.http.Query;

public interface ApiService {

    @GET("get_produk.php")
    Call<List<Product>> getProducts();

    // ⬅️ INI YANG HARUS DITAMBAHKAN agar error hilang
    @GET("get_products_by_kategori.php")
    Call<List<Product>> getProductsByKategori(@Query("kategori_id") String kategoriId);


    @FormUrlEncoded
    @POST("update_view.php")
    Call<ResponseBody> updateView(@Field("kode") String kode);
    @GET("get_order.php")
    Call<List<OrderHistoryItem>> getOrderHistory(@Query("id") String userId);

}
