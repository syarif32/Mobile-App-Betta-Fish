package com.example.cupang.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.cupang.MainActivity;
import com.example.cupang.R;
import com.example.cupang.history.History;
import com.example.cupang.productkategori.Impor;
import com.example.cupang.productkategori.Lokal;
import com.example.cupang.ui.products.ApiService;
import com.example.cupang.ui.products.Product;
import com.example.cupang.ui.products.ServerApi;
import com.example.cupang.ui.profile.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private DashboardAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    private RecyclerView recyclerViewProdukTerlaris;
    private ProdukTerlarisAdapter produkTerlarisAdapter;
    private List<Product> produkTerlarisList = new ArrayList<>();

    private SearchView searchView;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationTextView;

    private TextView userNameTextView, userEmailTextView;

    public DashboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_awal, container, false);

        // ✅ Ambil TextView dari layout
        userNameTextView = view.findViewById(R.id.userName);
        userEmailTextView = view.findViewById(R.id.userInfoEmail);

        // ✅ Ambil data dari SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String nama = sharedPref.getString("nama", "Guest");
        String email = sharedPref.getString("email", "Login terlebih dahulu");

        // ✅ Set ke TextView
        userNameTextView.setText(nama);
        userEmailTextView.setText(email);

        // Komponen UI lainnya
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        locationTextView = view.findViewById(R.id.location);

        productRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new DashboardAdapter(productList, getContext());
        productRecyclerView.setAdapter(productAdapter);

        recyclerViewProdukTerlaris = view.findViewById(R.id.recyclerViewProdukTerlaris);
        recyclerViewProdukTerlaris.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        produkTerlarisAdapter = new ProdukTerlarisAdapter(produkTerlarisList, getContext());
        recyclerViewProdukTerlaris.setAdapter(produkTerlarisAdapter);

        fetchProductData();
        fetchProdukTerlaris();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return true;
            }
        });

        // Image Slider
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slide1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slide2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.slide3, ScaleTypes.CENTER_INSIDE));
        imageSlider.setImageList(slideModels);

        ImageSlider imageSlider1 = view.findViewById(R.id.imageSlider1);
        List<SlideModel> beritaModels = new ArrayList<>();
        beritaModels.add(new SlideModel(R.drawable.berita1, ScaleTypes.FIT));
        beritaModels.add(new SlideModel(R.drawable.berita2, ScaleTypes.CENTER_CROP));
        beritaModels.add(new SlideModel(R.drawable.berita3, ScaleTypes.CENTER_CROP));
        imageSlider1.setImageList(beritaModels);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            getLastLocation();
        }

        // Tombol user
        ImageButton userButton = view.findViewById(R.id.btnUser);
        userButton.setOnClickListener(v -> {
            boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);
            if (isLoggedIn) {
                Fragment profileFragment = new ProfileFragment();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_home2, profileFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        // Tombol history
        ImageButton btnHistory = view.findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), History.class);
            startActivity(intent);
        });


        // Tombol lokal dan impor
        view.findViewById(R.id.btnLokal).setOnClickListener(v -> startActivity(new Intent(getActivity(), Lokal.class)));
        view.findViewById(R.id.btnImpor).setOnClickListener(v -> startActivity(new Intent(getActivity(), Impor.class)));

        return view;
    }

    private void fetchProductData() {
        ApiService apiService = ServerApi.getApiService();
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat produk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProdukTerlaris() {
        ApiService apiService = ServerApi.getApiService();
        apiService.getProdukTerlaris().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    produkTerlarisList.clear();
                    produkTerlarisList.addAll(response.body());
                    produkTerlarisAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat produk terlaris", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                locationTextView.setText(addresses.get(0).getLocality());
                            } else {
                                locationTextView.setText("Tidak Diketahui");
                            }
                        } catch (IOException e) {
                            locationTextView.setText("Error lokasi");
                        }
                    } else {
                        locationTextView.setText("Lokasi tidak tersedia");
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            locationTextView.setText("Izin lokasi ditolak");
        }
    }
}
