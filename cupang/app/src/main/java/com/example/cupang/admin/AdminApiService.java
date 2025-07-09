package com.example.cupang.admin;

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

public interface AdminApiService {
    // --- API untuk Manajemen Produk ---

    // Mengambil semua produk untuk ditampilkan dalam daftar
    @GET("get_all_products.php") // Asumsikan Anda memiliki file PHP ini
    Call<ProductListResponse> getAllProducts();

    // Mengambil detail satu produk berdasarkan kode
    @GET("get_product_detail.php") // Asumsikan Anda memiliki file PHP ini
    Call<ProductDetailResponse> getProductDetails(@Query("kode") String kode);

    // Menambah produk baru (tanpa diskon beli/jual, dengan kategori dari radio button)
    @Multipart
    @POST("add_product.php")
    Call<BaseResponse> addProduct(
            @Part("merk") RequestBody merk,
            @Part("kategori") RequestBody kategori, // Kategori akan dikirim dari RadioButton
            @Part("satuan") RequestBody satuan,
            @Part("hargabeli") RequestBody hargabeli,
            // Hapus parameter diskonbeli dan diskonjual
            @Part("hargapokok") RequestBody hargapokok,
            @Part("hargajual") RequestBody hargajual,
            @Part("stok") RequestBody stok,
            @Part("deskripsi") RequestBody deskripsi,
            @Part MultipartBody.Part foto
    );

    // Mengedit produk yang sudah ada (tanpa diskon beli/jual baru, dengan kategori dari radio button)
    @Multipart
    @POST("edit_product.php")
    Call<BaseResponse> editProduct(
            @Part("kode") RequestBody kode,
            @Part("merk") RequestBody merk,
            @Part("kategori") RequestBody kategori, // Kategori akan dikirim dari RadioButton
            @Part("satuan") RequestBody satuan,
            @Part("hargabeli") RequestBody hargabeli,
            // Hapus parameter diskonbeli dan diskonjual
            @Part("hargapokok") RequestBody hargapokok,
            @Part("hargajual") RequestBody hargajual,
            @Part("stok") RequestBody stok,
            @Part("deskripsi") RequestBody deskripsi,
            @Part MultipartBody.Part foto
    );

    // Menghapus produk
    @FormUrlEncoded
    @POST("delete_product.php")
    Call<BaseResponse> deleteProduct(@Field("kode") String kode);


    // --- API untuk Verifikasi Pembayaran (akan dijelaskan di bagian selanjutnya) ---
    // (Kode ini tetap sama seperti di respons sebelumnya)
    // Model yang dibutuhkan (PaymentVerificationListResponse, OrderDetailAdminResponse) akan dibuat di bagian selanjutnya
    @GET("get_pending_payments.php")
    Call<PaymentVerificationListResponse> getPendingPayments();

    @FormUrlEncoded
    @POST("verify_payment.php")
    Call<BaseResponse> verifyPayment(
            @Field("id_order") int idOrder,
            @Field("action") String action
    );

    @GET("get_order_details_for_admin.php")
    Call<OrderDetailAdminResponse> getOrderDetailsForAdmin(@Query("id_order") int idOrder);

    // --- API untuk Riwayat Order ---
    @GET("get_all_orders.php")
    Call<OrderHistoryListResponse> getAllOrderHistory();
}