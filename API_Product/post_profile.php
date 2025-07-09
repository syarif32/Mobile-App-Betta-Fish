<?php 
include "koneksi.php";
header('Content-Type: application/json');

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $required_fields = ["email", "name", "alamat", "kota", "provinsi", "telp", "kodepos"];
    foreach ($required_fields as $field) {
        if (!isset($_POST[$field])) {
            echo json_encode(["result" => 0, "message" => "Parameter $field tidak ditemukan"]);
            exit();
        }
    }

    $email = mysqli_real_escape_string($conn, $_POST['email']);
    $nama = mysqli_real_escape_string($conn, $_POST['name']);
    $alamat = mysqli_real_escape_string($conn, $_POST['alamat']);
    $kota = mysqli_real_escape_string($conn, $_POST['kota']);
    $provinsi = mysqli_real_escape_string($conn, $_POST['provinsi']);
    $telp = mysqli_real_escape_string($conn, $_POST['telp']);
    $kodepos = mysqli_real_escape_string($conn, $_POST['kodepos']);
    $foto = null;

    // Handle file upload
    if (isset($_FILES['foto']) && $_FILES['foto']['error'] == UPLOAD_ERR_OK) {
        $uploadDir = 'uploadimage/';
        if (!file_exists($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }

        $fileExt = pathinfo($_FILES['foto']['name'], PATHINFO_EXTENSION);
        $emailSafe = preg_replace('/[^a-zA-Z0-9]/', '_', $email);
        $fileName = 'profile_' . $emailSafe . '_' . time() . '.' . $fileExt;
        $uploadPath = $uploadDir . $fileName;

        $allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
        $allowedExtensions = ['jpg', 'jpeg', 'png', 'gif'];
        $fileType = mime_content_type($_FILES['foto']['tmp_name']);
        $fileSize = $_FILES['foto']['size'];
        $maxSize = 2 * 1024 * 1024; // 2 MB

        if (!in_array(strtolower($fileExt), $allowedExtensions)) {
            echo json_encode(["result" => 0, "message" => "Ekstensi file tidak diizinkan"]);
            exit();
        }

        if (!in_array($fileType, $allowedTypes)) {
            echo json_encode(["result" => 0, "message" => "Tipe file tidak valid"]);
            exit();
        }

        if ($fileSize > $maxSize) {
            echo json_encode(["result" => 0, "message" => "Ukuran foto maksimal 2MB"]);
            exit();
        }

        if (move_uploaded_file($_FILES['foto']['tmp_name'], $uploadPath)) {
            $foto = $uploadPath;

            // Hapus foto lama jika ada
            $sql = "SELECT foto FROM tbl_pelanggan WHERE email=?";
            $stmt = mysqli_prepare($conn, $sql);
            mysqli_stmt_bind_param($stmt, "s", $email);
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            $row = mysqli_fetch_assoc($result);

            if ($row['foto'] && file_exists($row['foto'])) {
                unlink($row['foto']);
            }
        }
    }

    // Update data di database
    if ($foto) {
        $sql = "UPDATE tbl_pelanggan SET nama=?, alamat=?, kota=?, provinsi=?, telp=?, kodepos=?, foto=? WHERE email=?";
        $stmt = mysqli_prepare($conn, $sql);
        mysqli_stmt_bind_param($stmt, "ssssssss", $nama, $alamat, $kota, $provinsi, $telp, $kodepos, $foto, $email);
    } else {
        $sql = "UPDATE tbl_pelanggan SET nama=?, alamat=?, kota=?, provinsi=?, telp=?, kodepos=? WHERE email=?";
        $stmt = mysqli_prepare($conn, $sql);
        mysqli_stmt_bind_param($stmt, "sssssss", $nama, $alamat, $kota, $provinsi, $telp, $kodepos, $email);
    }

    if (mysqli_stmt_execute($stmt)) {
        $baseURL = "https://allend.site/API_Product/"; // GANTI dengan IP lokal kamu
        echo json_encode([
            "result" => 1,
            "message" => "Profil berhasil diperbarui",
            "foto" => $foto ? $baseURL . $foto : null
        ]);
    } else {
        echo json_encode(["result" => 0, "message" => "Gagal memperbarui profil: " . mysqli_error($conn)]);
    }

    mysqli_stmt_close($stmt);
} else {
    echo json_encode(["result" => 0, "message" => "Metode request harus POST"]);
}

mysqli_close($conn);
?>
