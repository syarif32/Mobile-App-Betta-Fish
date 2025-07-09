package com.example.cupang.admin;

import android.content.Context;
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

public class PaymentVerificationAdapter extends RecyclerView.Adapter<PaymentVerificationAdapter.ViewHolder> {

    private Context context;
    private List<PaymentVerificationModel> paymentList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onViewProofClick(PaymentVerificationModel payment);
    }

    public PaymentVerificationAdapter(Context context, List<PaymentVerificationModel> paymentList, OnItemClickListener listener) {
        this.context = context;
        this.paymentList = paymentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentVerificationModel payment = paymentList.get(position);

        holder.tvOrderId.setText("Order ID: #" + payment.getIdOrder());
        holder.tvOrderDate.setText(payment.getTglOrder());
        // Asumsi kita ingin menampilkan ID pelanggan saja, atau Anda bisa tambahkan lookup nama pelanggan di PHP
        holder.tvCustomerInfo.setText("Pelanggan ID: " + payment.getIdPelanggan());

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        holder.tvTotalPayment.setText("Total: " + formatRupiah.format(payment.getTotalBayar()));
        holder.tvStatusPayment.setText("Status: " + payment.getFormattedStatus()); // Menggunakan helper di model

        holder.btnViewProof.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewProofClick(payment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList != null ? paymentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvCustomerInfo, tvTotalPayment, tvStatusPayment;
        Button btnViewProof;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id_payment);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date_payment);
            tvCustomerInfo = itemView.findViewById(R.id.tv_customer_info_payment);
            tvTotalPayment = itemView.findViewById(R.id.tv_total_payment);
            tvStatusPayment = itemView.findViewById(R.id.tv_status_payment);
            btnViewProof = itemView.findViewById(R.id.btn_view_proof_payment);
        }
    }
}