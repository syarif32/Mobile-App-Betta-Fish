package com.example.cupang.ui.orders;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cupang.ui.products.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class OrdersManager {
    private static final String PREF_NAME = "cart_pref";
    private static final String KEY_CART = "cart_list";

    // ✅ Tambahkan ke keranjang tanpa syarat login
    public static void addToCart(Context context, Product newProduct) {
        SharedPreferences loginPrefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String id_user = loginPrefs.getString("email", null); // Ambil email jika login

        List<Product> cart = getCart(context);

        boolean found = false;
        for (Product p : cart) {
            if (p.getKode().equals(newProduct.getKode())) {
                p.setQuantity(p.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            newProduct.setQuantity(1);
            if (id_user != null) {
                newProduct.setEmail(id_user);
            }
            cart.add(newProduct);
        }

        saveCart(context, cart);
    }

    // ✅ Ambil seluruh isi keranjang
    public static List<Product> getCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CART, null);

        if (json != null) {
            Type type = new TypeToken<List<Product>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    // NEW METHOD: Untuk mendapatkan kuantitas produk tertentu di keranjang
    public static int getProductQuantityInCart(Context context, String productCode) {
        List<Product> cart = getCart(context);
        for (Product p : cart) {
            if (p.getKode().equals(productCode)) {
                return p.getQuantity();
            }
        }
        return 0; // Jika produk tidak ditemukan di keranjang
    }

    // NEW METHOD: Untuk memeriksa apakah penambahan kuantitas akan melebihi stok
    public static boolean willExceedStock(Context context, Product product, int quantityToAdd) {
        int currentStock = product.getStok(); // Stok total dari database
        int quantityInCart = getProductQuantityInCart(context, product.getKode()); // Jumlah yang sudah di keranjang

        return (quantityInCart + quantityToAdd) > currentStock;
    }

    // ✅ Ambil keranjang hanya untuk user login
    public static List<Product> getCartByUser(Context context) {
        SharedPreferences loginPrefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String id_user = loginPrefs.getString("email", null);

        List<Product> allCart = getCart(context);
        List<Product> userCart = new ArrayList<>();

        if (id_user != null) {
            for (Product p : allCart) {
                if (id_user.equals(p.getEmail())) {
                    userCart.add(p);
                }
            }
        }

        return userCart;
    }

    // ✅ Hapus semua isi keranjang
    public static void clearCart(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().remove(KEY_CART).apply();
    }

    // ✅ Hapus 1 produk dari keranjang
    public static void removeFromCart(Context context, Product product) {
        List<Product> cart = getCart(context);
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getKode().equals(product.getKode())) {
                cart.remove(i);
                break;
            }
        }
        saveCart(context, cart);
    }

    // ✅ Update quantity produk dalam keranjang
    public static void updateCart(Context context, Product updatedProduct) {
        List<Product> cart = getCart(context);
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getKode().equals(updatedProduct.getKode())) {
                cart.set(i, updatedProduct);
                break;
            }
        }
        saveCart(context, cart);
    }

    // ✅ Simpan cart ke SharedPreferences
    private static void saveCart(Context context, List<Product> cart) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        prefs.edit().putString(KEY_CART, json).apply();
    }
}