package com.example.cupang.history;

import com.example.cupang.history.OrderHistoryResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {
    @FormUrlEncoded
    @POST("get_history.php")
    Call<OrderHistoryResponse> getOrderHistory(@Field("id_pelanggan") int idPelanggan);
    // TODO 1: Tambahkan endpoint untuk mendapatkan detail pesanan
    @GET("get_order_detail.php")
    Call<OrderDetailResponse> getOrderDetail(@Query("id_order") int idOrder);

    // TODO 2: Tambahkan endpoint untuk mengunggah bukti pembayaran
    @Multipart
    @POST("upload_payment_proof.php")
    Call<UploadProofResponse> uploadPaymentProof(
            @Part("id_order") RequestBody idOrder,
            @Part MultipartBody.Part image
    );

}
