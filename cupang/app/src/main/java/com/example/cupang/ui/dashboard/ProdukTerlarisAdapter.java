package com.example.cupang.ui.dashboard;

import android.content.Context;
import android.graphics.Color; // Import Color class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Tambahkan import ini jika ada tombol "Tambah ke Keranjang"
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Tambahkan import ini jika ada notifikasi

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cupang.R;
import com.example.cupang.ui.products.Product; // Pastikan ini mengacu ke kelas Product yang benar
import com.example.cupang.ui.orders.OrdersManager; // Tambahkan import ini jika adaOrdersManager

import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini
import java.util.List;

public class ProdukTerlarisAdapter extends RecyclerView.Adapter<ProdukTerlarisAdapter.ViewHolder> {

    private List<Product> produkList;
    private Context context;

    public ProdukTerlarisAdapter(List<Product> produkList, Context context) {
        this.produkList = produkList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProdukTerlarisAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produk_placeholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdukTerlarisAdapter.ViewHolder holder, int position) {
        Product product = produkList.get(position);
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

        Glide.with(context)
                .load("https://allend.site/API_Product/produk/" + product.getFoto())
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(holder.productImage);

        // --- Logika Status Stok ---
        if (product.getStok() <= 0) { // Jika stok 0 atau kurang
            holder.productStatus.setText("• Tidak Tersedia");
            holder.productStatus.setTextColor(Color.RED); // Warna merah untuk tidak tersedia

            // Nonaktifkan tombol "Tambah ke Keranjang" jika ada
            if (holder.imageButton != null) {
                holder.imageButton.setEnabled(false);
                holder.imageButton.setAlpha(0.5f);
                holder.imageButton.setBackgroundColor(Color.parseColor("#CCCCCC"));
            }
        } else { // Jika stok lebih dari 0
            holder.productStatus.setText("• Tersedia");
            holder.productStatus.setTextColor(Color.parseColor("#4CAF50")); // Warna hijau untuk tersedia

            // Aktifkan tombol "Tambah ke Keranjang" jika ada
            if (holder.imageButton != null) {
                holder.imageButton.setEnabled(true);
                holder.imageButton.setAlpha(1.0f);
                holder.imageButton.setBackgroundColor(Color.WHITE); // Kembalikan warna asli (opsional)
            }
        }
        // --- Akhir Logika Status Stok ---

        // Tambahkan OnClickListener untuk tombol "Tambah ke Keranjang" jika ada
        if (holder.imageButton != null) {
            holder.imageButton.setOnClickListener(v -> {
                // Periksa stok sebelum menambahkan ke keranjang
                if (product.getStok() > 0) {
                    OrdersManager.addToCart(context, product);
                    Toast.makeText(context, product.getMerk() + " ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Maaf, stok " + product.getMerk() + " habis.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        // Tambahkan productStatus di sini
        TextView productName, productPrice, productStock, productViews, productStatus;
        // Tambahkan ImageButton jika ada tombol "Tambah ke Keranjang"
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productStock = itemView.findViewById(R.id.productStock);
            productViews = itemView.findViewById(R.id.textViewProductViews);
            // Inisialisasi productStatus
            productStatus = itemView.findViewById(R.id.productStatus); // Pastikan ID ini ada di item_produk_placeholder.xml
            // Inisialisasi ImageButton (jika ada di layout item_produk_placeholder)
            imageButton = itemView.findViewById(R.id.myImageButton); // Pastikan ID ini ada di item_produk_placeholder.xml
        }
    }
}