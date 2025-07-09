package com.example.cupang.admin;

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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ViewHolder> {

    private Context context;
    private List<ProductAdminModel> productList;
    private OnItemActionListener listener; // Interface untuk action edit/delete

    public interface OnItemActionListener {
        void onEditClick(ProductAdminModel product);
        void onDeleteClick(ProductAdminModel product);
    }

    public ProductAdminAdapter(Context context, List<ProductAdminModel> productList, OnItemActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductAdminModel product = productList.get(position);

        holder.productName.setText(product.getMerk());
        holder.productCategory.setText("Kategori: " + product.getKategori());
        holder.productStock.setText("Stok: " + product.getStok());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.productPrice.setText(formatRupiah.format(product.getHargajual()));

        // Memuat gambar produk
//
        // Baris asli: String imageUrl = AdminApiClient.getClient().baseUrl() + "images/product/" + product.getFoto();
        String imageUrl = "https://allend.site/API_Product/produk/" + product.getFoto(); // <-- Ganti IP & nama folder produk
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.productImage);

        // Listener untuk tombol Edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product);
            }
        });

        // Listener untuk tombol Delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productCategory, productPrice, productStock;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_admin);
            productName = itemView.findViewById(R.id.product_name_admin);
            productCategory = itemView.findViewById(R.id.product_category_admin);
            productPrice = itemView.findViewById(R.id.product_price_admin);
            productStock = itemView.findViewById(R.id.product_stock_admin);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }
    }
}