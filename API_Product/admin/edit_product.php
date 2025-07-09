<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ambil data dari POST
    $kode = $_POST['kode'] ?? ''; // Kode produk harus ada untuk edit
    $merk = $_POST['merk'] ?? '';
    $kategori = $_POST['kategori'] ?? ''; // Ambil kategori
    $satuan = $_POST['satuan'] ?? '';
    $hargabeli = $_POST['hargabeli'] ?? 0;
    $diskonbeli = 0; // Android tidak mengirim ini, set default 0
    $hargapokok = $_POST['hargapokok'] ?? 0;
    $hargajual = $_POST['hargajual'] ?? 0;
    $diskonjual = 0; // Android tidak mengirim ini, set default 0
    $stok = $_POST['stok'] ?? 0;
    $deskripsi = $_POST['deskripsi'] ?? '';
    
    // Validasi sederhana
    // Stok tidak lagi divalidasi dengan empty() agar nilai 0 diizinkan
    if (empty($kode) || empty($merk) || empty($kategori) || empty($satuan) || empty($hargajual)) { 
        $response['status'] = "gagal";
        $response['message'] = "Data produk tidak lengkap atau kode tidak ditemukan.";
        echo json_encode($response);
        exit();
    }

    // Ambil data produk yang sudah ada untuk mendapatkan nama foto lama
    $existing_product_stmt = $conn->prepare("SELECT foto FROM tbl_product WHERE kode = ?");
    $existing_product_stmt->bind_param("s", $kode);
    $existing_product_stmt->execute();
    $existing_product_result = $existing_product_stmt->get_result();
    $old_foto = null;
    if ($existing_product_result->num_rows > 0) {
        $row = $existing_product_result->fetch_assoc();
        $old_foto = $row['foto'];
    }
    $existing_product_stmt->close();

    $foto_to_save = $old_foto; // Default: pertahankan foto lama

    // Penanganan Upload Foto Baru
    if (isset($_FILES['foto']) && $_FILES['foto']['error'] == 0) {
        $target_dir = "../produk/"; // Path relatif ke folder images/product/ dari admin/
        if (!is_dir($target_dir)) {
            mkdir($target_dir, 0777, true);
        }
        $file_extension = pathinfo($_FILES['foto']['name'], PATHINFO_EXTENSION);
        $foto_name = $kode . '.' . $file_extension; // Gunakan kode produk sebagai nama file
        $target_file = $target_dir . $foto_name;

        $allowed_types = ['jpg', 'jpeg', 'png', 'gif'];
        if (!in_array(strtolower($file_extension), $allowed_types)) {
            $response['status'] = "gagal";
            $response['message'] = "Hanya file JPG, JPEG, PNG & GIF yang diizinkan.";
            echo json_encode($response);
            exit();
        }

        if (move_uploaded_file($_FILES['foto']['tmp_name'], $target_file)) {
            $foto_to_save = $foto_name;
            // Hapus foto lama jika ada dan berbeda dengan yang baru diunggah
            if (!empty($old_foto) && $old_foto != $foto_name && file_exists($target_dir . $old_foto)) {
                unlink($target_dir . $old_foto);
            }
        } else {
            $response['status'] = "gagal";
            $response['message'] = "Gagal mengunggah foto baru: " . $_FILES['foto']['error'];
            echo json_encode($response);
            exit();
        }
    }

    // Update data di database
    $stmt = $conn->prepare("UPDATE tbl_product SET merk = ?, kategori = ?, satuan = ?, hargabeli = ?, diskonbeli = ?, hargapokok = ?, hargajual = ?, diskonjual = ?, stok = ?, foto = ?, deskripsi = ? WHERE kode = ?");

    $stmt->bind_param("sssdddddisss", 
        $merk, $kategori, $satuan,
        $hargabeli, $diskonbeli, $hargapokok, $hargajual,
        $diskonjual, $stok, $foto_to_save, $deskripsi, $kode
    );

    if ($stmt->execute()) {
        $response['status'] = "sukses";
        $response['message'] = "Produk berhasil diperbarui.";
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Gagal memperbarui produk di database: " . $stmt->error;
    }

    $stmt->close();

} else {
    $response['status'] = "gagal";
    $response['message'] = "Metode request tidak valid.";
}

echo json_encode($response);

$conn->close();
?>