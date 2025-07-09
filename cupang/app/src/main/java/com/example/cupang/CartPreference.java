package com.example.cupang;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class CartPreference {
    private static final String PREF_NAME = "cart_pref";
    private static final String KEY_CART_ITEMS = "cart_items";

    /** Ambil set ID produk yang sudah ada di keranjang */
    public static Set<String> getCartItems(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Pastikan kita kembalikan mutable copy
        return new HashSet<>(sp.getStringSet(KEY_CART_ITEMS, new HashSet<>()));
    }

    /** Tambah satu produk (by id) ke keranjang */
    public static void addToCart(Context context, int productId) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> current = sp.getStringSet(KEY_CART_ITEMS, new HashSet<>());
        Set<String> updated = new HashSet<>(current);
        updated.add(String.valueOf(productId));
        sp.edit()
                .putStringSet(KEY_CART_ITEMS, updated)
                .apply();
    }

    /** Hapus satu produk (by id) dari keranjang, kalau butuh */
    public static void removeFromCart(Context context, int productId) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> current = sp.getStringSet(KEY_CART_ITEMS, new HashSet<>());
        Set<String> updated = new HashSet<>(current);
        updated.remove(String.valueOf(productId));
        sp.edit()
                .putStringSet(KEY_CART_ITEMS, updated)
                .apply();
    }
}
