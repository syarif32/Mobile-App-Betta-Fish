package com.example.cupang.chechkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;
import com.example.cupang.ui.dashboard.Product;

import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private List<Product> cartList;
    private Context context;

    public CheckoutAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOrderProduct;
        TextView textOrderName, textOrderPrice, textOrderQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageOrderProduct = itemView.findViewById(R.id.productImage);
            textOrderName = itemView.findViewById(R.id.textOrderName);
            textOrderPrice = itemView.findViewById(R.id.textOrderPrice);
            textOrderQty = itemView.findViewById(R.id.textOrderQty);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartList.get(position);
        holder.textOrderName.setText(product.getMerk());
        double hargaBeli = product.getHargabeli();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String hargaRupiah = formatRupiah.format(hargaBeli);
        holder.textOrderPrice.setText(hargaRupiah);
        holder.textOrderQty.setText("Jumlah: " + product.getQuantity());
        String imgUrl = "https://allend.site/API_Product/produk/" + product.getFoto();
        Glide.with(context).load(imgUrl).into(holder.imageOrderProduct);
    }
    @Override
    public int getItemCount() {
        return cartList.size();
    }
}