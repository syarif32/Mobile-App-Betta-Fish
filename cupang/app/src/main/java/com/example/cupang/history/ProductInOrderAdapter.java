package com.example.cupang.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Tambahkan library Glide untuk memuat gambar
import com.example.cupang.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductInOrderAdapter extends RecyclerView.Adapter<ProductInOrderAdapter.ViewHolder> {

    private Context context;
    private List<ProductDetailModel> productList;

    public ProductInOrderAdapter(Context context, List<ProductDetailModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetailModel product = productList.get(position);

        holder.namaProduk.setText(product.getNamaProduk());
        holder.jumlahProduk.setText("x" + product.getQty());

        // Format harga ke mata uang Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.hargaProduk.setText(formatRupiah.format(product.getHargaSatuan() * product.getQty()));

        // *** BARIS INI YANG DIUBAH ***
        String imageUrl = product.getFotoProduk(); // Ambil langsung URL lengkap dari model
        // *** END PERUBAHAN ***

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo) // Gambar placeholder saat loading
                .error(R.drawable.logo) // Gambar error jika gagal load
                .into(holder.gambarProduk);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gambarProduk;
        TextView namaProduk, jumlahProduk, hargaProduk;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gambarProduk = itemView.findViewById(R.id.gambarProduk);
            namaProduk = itemView.findViewById(R.id.namaProduk);
            jumlahProduk = itemView.findViewById(R.id.jumlahProduk);
            hargaProduk = itemView.findViewById(R.id.hargaProduk);
        }
    }
}