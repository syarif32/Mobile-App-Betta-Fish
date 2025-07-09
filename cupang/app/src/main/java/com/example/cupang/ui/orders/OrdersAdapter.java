package com.example.cupang.ui.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;
import com.example.cupang.ui.products.Product;

import java.util.List;
import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<Product> cartList;
    private Context context;
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(Product produk);
    }

    public OrdersAdapter(Context context, List<Product> cartList, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.cartList = cartList;
        this.deleteListener = deleteListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOrderProduct;
        TextView textOrderName, textOrderPrice, textOrderQty;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageOrderProduct = itemView.findViewById(R.id.productImage);
            textOrderName = itemView.findViewById(R.id.textOrderName);
            textOrderPrice = itemView.findViewById(R.id.textOrderPrice);
            textOrderQty = itemView.findViewById(R.id.textOrderQty);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {
        Product produk = cartList.get(position);
        holder.textOrderName.setText(produk.getMerk());
        double hargaBeli = produk.getHargabeli();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String hargaRupiah = formatRupiah.format(hargaBeli);
        holder.textOrderPrice.setText(hargaRupiah);
        holder.textOrderQty.setText("Jumlah: " + produk.getQuantity());

        String imgUrl = "https://allend.site/API_Product/produk/" + produk.getFoto();
        Glide.with(context).load(imgUrl).into(holder.imageOrderProduct);

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(produk);
            }
        });
    }
    @Override
    public int getItemCount() {
        return cartList.size();
    }
}