package com.example.cupang.history;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Tambahkan import ini untuk ContextCompat
import androidx.recyclerview.widget.RecyclerView;
import com.example.cupang.R;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderHistoryModel> orderList;

    public HistoryAdapter(Context context, List<OrderHistoryModel> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        OrderHistoryModel order = orderList.get(position);

        holder.txtOrderId.setText("#ORD" + order.getId_order());
        holder.txtTanggal.setText(order.getTgl_order());
        holder.txtProduk.setText(order.getProduk());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.txtTotal.setText(formatRupiah.format(order.getTotal_bayar()));

        // Mendapatkan status numerik
        String statusNumeric = order.getStatus();
        holder.txtStatus.setText(mapStatus(statusNumeric));

        // --- Logika Pewarnaan Status ---
        int colorResId;
        switch (statusNumeric) {
            case "0": // Menunggu Pembayaran
                colorResId = R.color.orange_warning; // Warna peringatan (misal: oranye)
                break;
            case "1":
                colorResId = R.color.yellow_confirmation;
                break;
            case "2": // Order Berhasil
            case "4":
                colorResId = R.color.green_success;
                break;
            default:
                colorResId = R.color.grey_status_default;
                break;
        }
        holder.txtStatus.setTextColor(ContextCompat.getColor(context, colorResId));
        // --- Akhir Logika Pewarnaan Status ---

        // Klik -> ke detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetail.class);
            intent.putExtra("ORDER_ID", order.getId_order());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    private String mapStatus(String status) {
        switch (status) {
            case "0":
                return "Menunggu Pembayaran";
            case "1":
                return "Sudah Dibayar (Menunggu Verifikasi)"; // Ubah teks agar lebih ringkas
            case "2":
                return "Order Berhasil";
            case "3":
                return "Dalam Pengiriman";
            case "4":
                return "Order Berhasil";
            case "5":
                return "Dibatalkan";
            default:
                return "Status Tidak Diketahui";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggal, txtProduk, txtTotal, txtStatus;
        TextView txtOrderId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtProduk = itemView.findViewById(R.id.txtProduk);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}