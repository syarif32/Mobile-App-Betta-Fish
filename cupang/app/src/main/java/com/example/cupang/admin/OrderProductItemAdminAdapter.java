package com.example.cupang.admin;

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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderProductItemAdminAdapter extends RecyclerView.Adapter<OrderProductItemAdminAdapter.ViewHolder> {

    private Context context;
    private List<OrderProductItemAdminModel> productList;

    public OrderProductItemAdminAdapter(Context context, List<OrderProductItemAdminModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Metode untuk memperbarui data di adapter
    public void setProducts(List<OrderProductItemAdminModel> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product_admin, parent, false); // Buat layout ini
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderProductItemAdminModel product = productList.get(position);

        holder.tvProductName.setText(product.getNamaProduk());
        holder.tvProductQty.setText("Jumlah: " + product.getQty() + "x");

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvProductPrice.setText(formatRupiah.format(product.getBayar())); // 'bayar' adalah harga total untuk item ini

        // Memuat gambar produk
        // Baris asli: String imageUrl = AdminApiClient.getClient().baseUrl() + "images/product/" + product.getFotoProduk();
        String imageUrl = "https://allend.site/API_Product/produk/" + product.getFotoProduk(); // <-- Ganti IP & nama folder produk
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.imageViewProduct);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView tvProductName, tvProductQty, tvProductPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageView_product_order_item);
            tvProductName = itemView.findViewById(R.id.tv_product_name_order_item);
            tvProductQty = itemView.findViewById(R.id.tv_product_qty_order_item);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price_order_item);
        }
    }
}