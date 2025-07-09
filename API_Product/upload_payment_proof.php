<?php
header('Content-Type: application/json');
include 'koneksi.php'; // Pastikan path ini benar untuk koneksi database Anda

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_order = $_POST['id_order']; // Ambil id_order dari POST request

    if (isset($_FILES['bukti_bayar']) && $_FILES['bukti_bayar']['error'] == 0) {
        $target_dir = "bukti_bayar/"; // Folder untuk menyimpan bukti pembayaran
        if (!is_dir($target_dir)) {
            mkdir($target_dir, 0777, true);
        }

        $file_name = uniqid() . "_" . basename($_FILES['bukti_bayar']['name']);
        $target_file = $target_dir . $file_name;
        $imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

        // Periksa apakah file gambar asli atau bukan
        $check = getimagesize($_FILES['bukti_bayar']['tmp_name']);
        if ($check !== false) {
            // Izinkan format file tertentu
            if ($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg" && $imageFileType != "gif") {
                $response['status'] = "gagal";
                $response['message'] = "Maaf, hanya file JPG, JPEG, PNG & GIF yang diizinkan.";
            } else {
                if (move_uploaded_file($_FILES['bukti_bayar']['tmp_name'], $target_file)) {
                    // Update nama file bukti_bayar di database
                    $query_update = "UPDATE tbl_order SET bukti_bayar = ?, status = ? WHERE id_order = ?";
                    $status_after_upload = 1; // Ubah status menjadi 1 (Sudah Dibayar)
                    $stmt_update = $conn->prepare($query_update);
                    $stmt_update->bind_param("sii", $file_name, $status_after_upload, $id_order);

                    if ($stmt_update->execute()) {
                        $response['status'] = "sukses";
                        $response['message'] = "Bukti pembayaran berhasil diunggah.";
                    } else {
                        $response['status'] = "gagal";
                        $response['message'] = "Gagal memperbarui database: " . $stmt_update->error;
                        // Hapus file yang sudah terupload jika update database gagal
                        unlink($target_file);
                    }
                    $stmt_update->close();
                } else {
                    $response['status'] = "gagal";
                    $response['message'] = "Gagal mengunggah file.";
                }
            }
        } else {
            $response['status'] = "gagal";
            $response['message'] = "File bukan gambar.";
        }
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Tidak ada file yang diunggah atau terjadi kesalahan.";
    }
} else {
    $response['status'] = "gagal";
    $response['message'] = "Metode request tidak valid.";
}

echo json_encode($response);
$conn->close();
?>