package com.example.cupang.productkategori;

import android.content.Context;
import android.graphics.Color; // Import Color class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
// Hapus import yang tidak digunakan jika tidak ada tombol atau toast
// import android.widget.ImageButton;
// import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;
// Pastikan ini adalah import yang benar untuk kelas Product yang Anda gunakan secara universal
// HAPUS BARIS INI JIKA ADA: import com.example.cupang.ui.products.Product;

import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private List<Product> productListFull;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.productListFull = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk_placeholder, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getMerk());
        double hargaBeli = product.getHargabeli();
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String hargaRupiah = formatRupiah.format(hargaBeli);
        holder.productPrice.setText(hargaRupiah);
        holder.productStock.setText("" + product.getStok());
        holder.productViews.setText("" + product.getView() + " kali");
        String imageUrl = "https://allend.site/API_Product/produk/" + product.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.productImage);

        // stok
        if (product.getStok() <= 0) {
            holder.productStatus.setText("Tidak Tersedia");
            holder.productStatus.setTextColor(Color.RED);
        } else { // Jika stok lebih dari 0
            holder.productStatus.setText("Tersedia");
            holder.productStatus.setTextColor(Color.parseColor("#4CAF50"));
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filter(String query) {
        productList.clear();
        if (query == null || query.trim().isEmpty()) {
            productList.addAll(productListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Product product : productListFull) {
                if (product.getMerk().toLowerCase().contains(lowerCaseQuery)) {
                    productList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;

        TextView productName, productPrice, productStock, productViews, productStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productStock = itemView.findViewById(R.id.productStock);
            productViews = itemView.findViewById(R.id.textViewProductViews);
            productStatus = itemView.findViewById(R.id.productStatus);

        }
    }
}