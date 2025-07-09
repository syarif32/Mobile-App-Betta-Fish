package com.example.cupang;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RegisterAPI {
    @FormUrlEncoded
    @POST("get_login.php")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("post_register.php")
    Call<ResponseBody> register(@Field("email") String email,
                                @Field("nama") String nama,
                                @Field("password") String password);

    @FormUrlEncoded
    @POST("post_profile.php")
    Call<ResponseBody> updateProfile(
            @Field("email") String email,
            @Field("name") String name,
            @Field("alamat") String alamat,
            @Field("kota") String kota,
            @Field("provinsi") String provinsi,
            @Field("telp") String telp,
            @Field("kodepos") String kodepos);

    @Multipart
    @POST("post_profile.php")
    Call<ResponseBody> updateProfileWithImage(
            @Part("email") RequestBody email,
            @Part("name") RequestBody name,
            @Part("alamat") RequestBody alamat,
            @Part("kota") RequestBody kota,
            @Part("provinsi") RequestBody provinsi,
            @Part("telp") RequestBody telp,
            @Part("kodepos") RequestBody kodepos,
            @Part MultipartBody.Part foto);

    @GET("get_profile.php")
    Call<ResponseBody> getProfile(@Query("email") String email);

}