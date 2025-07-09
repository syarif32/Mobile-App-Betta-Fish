<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $kode = $_POST['kode'] ?? '';

    if (empty($kode)) {
        $response['status'] = "gagal";
        $response['message'] = "Kode produk tidak diberikan.";
        echo json_encode($response);
        exit();
    }

    // Ambil nama foto sebelum menghapus record
    $existing_product_stmt = $conn->prepare("SELECT foto FROM tbl_product WHERE kode = ?");
    $existing_product_stmt->bind_param("s", $kode);
    $existing_product_stmt->execute();
    $existing_product_result = $existing_product_stmt->get_result();
    $foto_to_delete = null;
    if ($existing_product_result->num_rows > 0) {
        $row = $existing_product_result->fetch_assoc();
        $foto_to_delete = $row['foto'];
    }
    $existing_product_stmt->close();

    // Hapus dari database
    $stmt = $conn->prepare("DELETE FROM tbl_product WHERE kode = ?");
    $stmt->bind_param("s", $kode);

    if ($stmt->execute()) {
        // Jika berhasil dihapus dari DB, hapus juga file fotonya
        if (!empty($foto_to_delete) && file_exists("../images/product/" . $foto_to_delete)) { // Path relatif dari admin/
            unlink("../images/product/" . $foto_to_delete);
        }
        $response['status'] = "sukses";
        $response['message'] = "Produk berhasil dihapus.";
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Gagal menghapus produk: " . $stmt->error;
    }

    $stmt->close();

} else {
    $response['status'] = "gagal";
    $response['message'] = "Metode request tidak valid.";
}

echo json_encode($response);

$conn->close();
?>