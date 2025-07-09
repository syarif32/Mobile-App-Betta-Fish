package com.example.cupang.admin;

import android.content.Context;
import android.graphics.Color; // Import Color class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cupang.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<OrderHistoryModel> orderList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewDetailsClick(OrderHistoryModel order);
    }

    public OrderHistoryAdapter(Context context, List<OrderHistoryModel> orderList, OnItemClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistoryModel order = orderList.get(position);

        holder.tvOrderId.setText("Order ID: #" + order.getIdOrder());
        holder.tvOrderDate.setText(order.getTglOrder());
        holder.tvCustomerName.setText("Pelanggan: " + order.getNamaPelanggan());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvTotalBayar.setText("Total Bayar: " + formatRupiah.format(order.getTotalBayar()));
        holder.tvOrderStatus.setText("Status: " + order.getFormattedStatus());

        // Logika untuk mengatur warna status
        String status = order.getStatus(); // Mengambil status numerik dari model
        switch (status) {
            case "0": // Menunggu Pembayaran
                holder.tvOrderStatus.setTextColor(Color.parseColor("#FFA500")); // Oranye
                break;
            case "1": // Sudah Dibayar (Menunggu Konfirmasi)
                holder.tvOrderStatus.setTextColor(Color.parseColor("#0000FF")); // Biru
                break;
            case "2": // Pesanan Diproses
                holder.tvOrderStatus.setTextColor(Color.parseColor("#008000")); // Ungu
                break;
            case "3": // Dalam Pengiriman
                holder.tvOrderStatus.setTextColor(Color.parseColor("#ADD8E6")); // Biru Muda
                break;
            case "4": // Selesai
                holder.tvOrderStatus.setTextColor(Color.parseColor("#008000")); // Hijau
                break;
            case "5": // Dibatalkan
                holder.tvOrderStatus.setTextColor(Color.parseColor("#FF0000")); // Merah
                break;
            default:
                holder.tvOrderStatus.setTextColor(Color.parseColor("#808080")); // Abu-abu default
                break;
        }

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrders(List<OrderHistoryModel> newOrders) {
        this.orderList.clear();
        this.orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvCustomerName, tvTotalBayar, tvOrderStatus;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_history_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_history_order_date);
            tvCustomerName = itemView.findViewById(R.id.tv_history_customer_name);
            tvTotalBayar = itemView.findViewById(R.id.tv_history_total_bayar);
            tvOrderStatus = itemView.findViewById(R.id.tv_history_order_status);
            btnViewDetails = itemView.findViewById(R.id.btn_history_view_details);
        }
    }
}