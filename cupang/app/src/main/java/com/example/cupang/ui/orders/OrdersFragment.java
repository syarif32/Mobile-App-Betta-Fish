package com.example.cupang.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cupang.R;
import com.example.cupang.chechkout.Checkout;
import com.example.cupang.ui.products.Product;

import java.text.NumberFormat; // Tambahkan import ini
import java.util.Locale;     // Tambahkan import ini
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private TextView totalTextView;
    private Button checkoutButton;

    private List<Product> cartList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Inisialisasi view
        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        totalTextView = view.findViewById(R.id.textTotalPayment);
        checkoutButton = view.findViewById(R.id.btnCheckout);

        // Ambil SEMUA item di keranjang (tanpa filter login)
        cartList = OrdersManager.getCart(requireContext());

        // Inisialisasi adapter dengan delete listener
        ordersAdapter = new OrdersAdapter(getContext(), cartList, new OrdersAdapter.OnItemDeleteListener() {
            @Override
            public void onDelete(Product produk) {
                // Hapus produk dari SharedPreferences
                OrdersManager.removeFromCart(requireContext(), produk);

                // Hapus dari list lokal dan update UI
                cartList.remove(produk);
                ordersAdapter.notifyDataSetChanged();
                updateTotal();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(ordersAdapter);

        updateTotal();

        // Checkout tombol
        checkoutButton.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(getContext(), "Keranjang kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pindah ke halaman Checkout
            Intent intent = new Intent(getActivity(), Checkout.class);
            startActivity(intent);
        });

        return view;
    }

    // Menghitung total harga
    private void updateTotal() {
        double total = 0;
        for (Product product : cartList) {
            total += product.getHargabeli() * product.getQuantity();
        }

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        totalTextView.setText("Total Pembayaran: " + formatRupiah.format(total));

    }
}