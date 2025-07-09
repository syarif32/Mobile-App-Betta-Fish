package com.example.cupang.chechkout;

import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterAPI {

    @GET("get_provinsi.php")
    Call<ProvinsiRespone> getProvinsi();  // response pakai wrapper

    @GET("get_kota.php")
    Call<KotaResponse> getKota(@Query("province") String provinceId);

    @FormUrlEncoded
    @POST("cek_ongkir.php")
    Call<ResponseBody> getOngkir(
            @Field("origin") String origin,
            @Field("destination") String destination,
            @Field("weight") int weight,
            @Field("courier") String courier
    );

    @POST("post_order.php")
    Call<ResponseBody> checkout(@Body RequestBody body);
    @Headers("Content-Type: application/json")
    @POST("get_order.php")
    Call<JSONObject> getOrders(@Body JSONObject body);
}