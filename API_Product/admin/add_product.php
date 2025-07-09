<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Ambil data dari POST
    $merk = $_POST['merk'] ?? '';
    $kategori = $_POST['kategori'] ?? ''; 
    $satuan = $_POST['satuan'] ?? '';
    $hargabeli = $_POST['hargabeli'] ?? 0;
    $hargapokok = $_POST['hargapokok'] ?? 0;
    $hargajual = $_POST['hargajual'] ?? 0;
    $stok = $_POST['stok'] ?? 0;
    $deskripsi = $_POST['deskripsi'] ?? '';
    
    // Set nilai default untuk diskonbeli, diskonjual, dan view karena Android tidak mengirimnya
    $diskonbeli = 0; 
    $diskonjual = 0; 
    $view_default = 0; 
    
    $foto = null; // Default nama foto

    // Validasi sederhana
    if (empty($merk) || empty($kategori) || empty($satuan) || empty($hargajual) || empty($stok)) {
        $response['status'] = "gagal";
        $response['message'] = "Data produk tidak lengkap: Merk, Kategori, Satuan, Harga Jual, Stok wajib diisi.";
        echo json_encode($response);
        exit();
    }

    // Generate kode produk baru berdasarkan kategori
    $prefix = '';
    if (strtolower($kategori) == 'impor') {
        $prefix = 'CI';
    } elseif (strtolower($kategori) == 'lokal') {
        $prefix = 'CL';
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Kategori tidak valid. Harus Lokal atau Impor.";
        echo json_encode($response);
        exit();
    }

    // Ambil kode terakhir dengan prefix yang sesuai
    $result_last_kode = $conn->query("SELECT kode FROM tbl_product WHERE kode LIKE '" . $prefix . "%' ORDER BY kode DESC LIMIT 1");
    $last_kode_num = 0;
    if ($result_last_kode->num_rows > 0) {
        $row = $result_last_kode->fetch_assoc();
        if (preg_match('/^' . $prefix . '(\d+)$/', $row['kode'], $matches)) {
            $last_kode_num = (int)$matches[1];
        }
    }
    $new_kode = $prefix . str_pad($last_kode_num + 1, 3, '0', STR_PAD_LEFT);


    // Penanganan Upload Foto
    if (isset($_FILES['foto']) && $_FILES['foto']['error'] == 0) {
        $target_dir = "../produk/"; // Path relatif ke folder images/product/ dari admin/
        if (!is_dir($target_dir)) {
            mkdir($target_dir, 0777, true);
        }
        $file_extension = pathinfo($_FILES['foto']['name'], PATHINFO_EXTENSION);
        $foto_name = $new_kode . '.' . $file_extension; // Gunakan kode produk sebagai nama file
        $target_file = $target_dir . $foto_name;

        $allowed_types = ['jpg', 'jpeg', 'png', 'gif'];
        if (!in_array(strtolower($file_extension), $allowed_types)) {
            $response['status'] = "gagal";
            $response['message'] = "Hanya file JPG, JPEG, PNG & GIF yang diizinkan.";
            echo json_encode($response);
            exit();
        }

        if (move_uploaded_file($_FILES['foto']['tmp_name'], $target_file)) {
            $foto = $foto_name;
        } else {
            $response['status'] = "gagal";
            $response['message'] = "Gagal mengunggah foto.";
            echo json_encode($response);
            exit();
        }
    }

    // Masukkan data ke database
    $stmt = $conn->prepare("INSERT INTO tbl_product (kode, merk, kategori, satuan, hargabeli, diskonbeli, hargapokok, hargajual, diskonjual, stok, foto, deskripsi, view) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    
    $stmt->bind_param("sssdddddisssi", 
        $new_kode, $merk, $kategori, $satuan,
        $hargabeli, $diskonbeli, $hargapokok, $hargajual,
        $diskonjual, $stok, $foto, $deskripsi, $view_default
    );

    if ($stmt->execute()) {
        $response['status'] = "sukses";
        $response['message'] = "Produk baru berhasil ditambahkan.";
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Gagal menambahkan produk ke database: " . $stmt->error;
        if ($foto && file_exists($target_dir . $foto)) { 
            unlink($target_dir . $foto);
        }
    }

    $stmt->close();

} else {
    $response['status'] = "gagal";
    $response['message'] = "Metode request tidak valid.";
}

echo json_encode($response);

$conn->close();
?>