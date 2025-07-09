package com.example.cupang.ui.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cupang.About;
import com.example.cupang.ApiClient;
import com.example.cupang.MainActivity;
import com.example.cupang.R;
import com.example.cupang.RegisterAPI;
import com.example.cupang.ui.profile.ResetPassword;
import com.example.cupang.databinding.FragmentNotificationsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RegisterAPI apiService;
    private SharedPreferences sharedPreferences;
    private static final int PICK_IMAGE_REQUEST = 100;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiService = ApiClient.getClient().create(RegisterAPI.class);
        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        binding.emailEditText.setEnabled(false);

        if (!email.isEmpty()) {
            getUserProfile(email);
        }

        setupButtonListeners();
        return root;
    }

    private void setupButtonListeners() {
        binding.submitButton.setOnClickListener(v -> updateUserProfile());
        binding.btnChangePhoto.setOnClickListener(v -> openImageChooser());
        binding.btnChangePassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Verifikasi Password Lama");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

            builder.setPositiveButton("Lanjut", (dialog, which) -> {
                String oldPasswordInput = input.getText().toString().trim();

                if (oldPasswordInput.isEmpty()) {
                    Toast.makeText(requireContext(), "Password lama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ambil password yang tersimpan dari SharedPreferences
                SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String savedPassword = preferences.getString("password", "");

                // Cek apakah password input sama dengan yang tersimpan
                if (oldPasswordInput.equals(savedPassword)) {
                    Intent intent = new Intent(requireContext(), ResetPassword.class);
                    intent.putExtra("oldPassword", oldPasswordInput);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Password lama tidak sesuai", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
            builder.show();
        });


        binding.btnLogout.setOnClickListener(v -> logoutUser());
        binding.aboutus.setOnClickListener(v -> navigateToAbout());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(binding.profileImage);
        }
    }

    private void getUserProfile(String email) {
        if (!isNetworkAvailable()) {
            showToast("Tidak ada koneksi internet");
            return;
        }

        Call<ResponseBody> call = apiService.getProfile(email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);

                        if (jsonObject.getInt("result") == 1) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            populateProfileData(data);
                        } else {
                            showToast("Data tidak ditemukan");
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("ProfileFragment", "Error parsing data", e);
                        showToast("Gagal memuat data profil");
                    }
                } else {
                    showToast("Gagal mengambil data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ProfileFragment", "Network error", t);
                showToast("Kesalahan jaringan");
            }
        });
    }

    private void populateProfileData(JSONObject data) throws JSONException {
        binding.emailEditText.setText(safeGetString(data, "email"));
        binding.nameEditText.setText(safeGetString(data, "name"));
        binding.addressEditText.setText(safeGetString(data, "alamat"));
        binding.cityEditText.setText(safeGetString(data, "kota"));
        binding.provinceEditText.setText(safeGetString(data, "provinsi"));
        binding.phoneEditText.setText(safeGetString(data, "telp"));
        binding.postalCodeEditText.setText(safeGetString(data, "kodepos"));

        String fotoUrl = safeGetString(data, "foto");
        if (!fotoUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(fotoUrl)
                    .circleCrop()
                    .placeholder(R.drawable.user)
                    .into(binding.profileImage);
        }
    }

    private void updateUserProfile() {
        String email = binding.emailEditText.getText().toString();
        String name = binding.nameEditText.getText().toString();
        String alamat = binding.addressEditText.getText().toString();
        String kota = binding.cityEditText.getText().toString();
        String provinsi = binding.provinceEditText.getText().toString();
        String telp = binding.phoneEditText.getText().toString();
        String kodepos = binding.postalCodeEditText.getText().toString();

        if (!isNetworkAvailable()) {
            showToast("Tidak ada koneksi internet");
            return;
        }

        if (selectedImageUri != null) {
            uploadProfileWithImage(email, name, alamat, kota, provinsi, telp, kodepos);
        } else {
            updateProfileWithoutImage(email, name, alamat, kota, provinsi, telp, kodepos);
        }
    }

    private void uploadProfileWithImage(String email, String name, String alamat,
                                        String kota, String provinsi, String telp, String kodepos) {
        try {
            String fileName = getFileName(selectedImageUri);
            InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedImageUri);
            byte[] fileBytes = getBytes(inputStream);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("foto", fileName, requestFile);

            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
            RequestBody alamatBody = RequestBody.create(MediaType.parse("text/plain"), alamat);
            RequestBody kotaBody = RequestBody.create(MediaType.parse("text/plain"), kota);
            RequestBody provinsiBody = RequestBody.create(MediaType.parse("text/plain"), provinsi);
            RequestBody telpBody = RequestBody.create(MediaType.parse("text/plain"), telp);
            RequestBody kodeposBody = RequestBody.create(MediaType.parse("text/plain"), kodepos);

            Call<ResponseBody> call = apiService.updateProfileWithImage(
                    emailBody, nameBody, alamatBody, kotaBody,
                    provinsiBody, telpBody, kodeposBody, imagePart);

            executeProfileUpdate(call, email);
        } catch (Exception e) {
            Log.e("ProfileFragment", "Error preparing image", e);
            showToast("Gagal mempersiapkan gambar");
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void updateProfileWithoutImage(String email, String name, String alamat,
                                           String kota, String provinsi, String telp, String kodepos) {
        Call<ResponseBody> call = apiService.updateProfile(
                email, name, alamat, kota, provinsi, telp, kodepos);
        executeProfileUpdate(call, email);
    }

    private void executeProfileUpdate(Call<ResponseBody> call, String email) {
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    showToast("Profil berhasil diperbarui");
                    getUserProfile(email);
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("ProfileFragment", "Update failed: " + error);
                        showToast("Gagal memperbarui profil");
                    } catch (IOException e) {
                        Log.e("ProfileFragment", "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ProfileFragment", "Network error during update", t);
                showToast("Kesalahan jaringan");
            }
        });
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        showToast("Logout berhasil");

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void navigateToAbout() {
        startActivity(new Intent(requireActivity(), About.class));
    }

    private String safeGetString(JSONObject obj, String key) {
        try {
            String value = obj.getString(key);
            return (value != null && !value.equalsIgnoreCase("null")) ? value : "";
        } catch (JSONException e) {
            return "";
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
