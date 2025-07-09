package com.example.cupang.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color; // Import Color class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast for messages

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Import AlertDialog
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.Maindetail;
import com.example.cupang.R;
import com.example.cupang.ui.orders.OrdersManager;
import com.example.cupang.ui.products.Product; // Pastikan ini mengacu ke kelas Product yang benar

import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini
import java.util.ArrayList;
import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ProductViewHolder> {
    private List<Product> productList;
    private List<Product> productListFull; // Data lengkap (untuk filter)
    private Context context;

    public DashboardAdapter(List<Product> productList, Context context) {
        this.context = context;
        this.productList = new ArrayList<>(productList); // agar tidak referensi langsung
        this.productListFull = new ArrayList<>(productList);
    }

    // 🆕 Digunakan saat data dari server diterima ulang
    public void updateData(List<Product> newProductList) {
        productList.clear();
        productList.addAll(newProductList);
        productListFull.clear();
        productListFull.addAll(newProductList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product12, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getMerk());

        // --- BAGIAN INI YANG DIUBAH UNTUK FORMAT RUPIAH ---
        // Dapatkan harga beli dalam double
        double hargaBeli = product.getHargabeli();

        // Buat formatter untuk Rupiah
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

        // Format harga beli ke string Rupiah
        String hargaRupiah = formatRupiah.format(hargaBeli);

        // Set teks ke productPrice
        holder.productPrice.setText(hargaRupiah);
        // --- AKHIR BAGIAN PERUBAHAN ---

        holder.productStock.setText("Stok: " + product.getStok());
        holder.productViews.setText("Dilihat: " + product.getView() + " kali");

        // URL gambar produk. Sesuaikan jika IP server berbeda dari fragment product.
        String imageUrl = "https://allend.site/API_Product/produk/" + product.getFoto();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.productImage);

        // --- Logika Status Stok dan Tombol Checkout ---
        if (product.getStok() <= 0) { // Jika stok 0 atau kurang
            holder.productStatus.setText("• Tidak Tersedia");
            holder.productStatus.setTextColor(Color.RED); // Warna merah untuk tidak tersedia
            holder.imageButton.setEnabled(false); // Nonaktifkan tombol "Tambah ke Keranjang"
            holder.imageButton.setAlpha(0.5f); // Buat sedikit transparan untuk menunjukkan tidak aktif
            holder.imageButton.setBackgroundColor(Color.parseColor("#CCCCCC")); // Warna abu-abu (opsional)
        } else { // Jika stok lebih dari 0
            holder.productStatus.setText("• Tersedia");
            holder.productStatus.setTextColor(Color.parseColor("#4CAF50")); // Warna hijau untuk tersedia
            holder.imageButton.setEnabled(true); // Aktifkan tombol "Tambah ke Keranjang"
            holder.imageButton.setAlpha(1.0f); // Kembalikan ke normal
            holder.imageButton.setBackgroundColor(Color.WHITE); // Kembalikan warna asli (opsional)
        }
        // --- Akhir Logika Status Stok dan Tombol Checkout ---

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, Maindetail.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        holder.imageButton.setOnClickListener(v -> {
            if (product.getStok() > 0) {
                // Mendapatkan kuantitas produk yang sudah ada di keranjang
                int currentQuantityInCart = OrdersManager.getProductQuantityInCart(context, product.getKode());
                int quantityToAdd = 1;


                if ((currentQuantityInCart + quantityToAdd) > product.getStok()) {
                    // Tampilkan AlertDialog jika stok tidak mencukupi
                    new AlertDialog.Builder(context)
                            .setTitle("Stok Tidak Cukup")
                            .setMessage("Maaf, penambahan produk " + product.getMerk() + "  melebihi stok yang tersedia (" + product.getStok() + ").\n" +
                                    "Jumlah di keranjang saat ini: " + currentQuantityInCart + ".")
                            .setPositiveButton("OK", null) // Tombol OK hanya menutup dialog
                            .show();
                } else {
                    OrdersManager.addToCart(context, product);
                    Toast.makeText(context, product.getMerk() + " ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();

                }
            } else {

                Toast.makeText(context, "Maaf, stok " + product.getMerk() + " habis.", Toast.LENGTH_SHORT).show();
            }
        });
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
            String lowerCaseQuery = query.toLowerCase().trim();
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
        Button btnDetail;
        ImageButton imageButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productStock = itemView.findViewById(R.id.productStock);
            productStatus = itemView.findViewById(R.id.productStatus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            imageButton = itemView.findViewById(R.id.myImageButton);
            productViews = itemView.findViewById(R.id.textViewProductViews);
        }
    }
}