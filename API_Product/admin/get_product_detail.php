<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if (isset($_GET['kode'])) {
    $kode = $_GET['kode'];

    $stmt = $conn->prepare("SELECT kode, merk, kategori, satuan, hargabeli, hargapokok, hargajual, stok, foto, deskripsi, view FROM tbl_product WHERE kode = ?");
    $stmt->bind_param("s", $kode);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $product = $result->fetch_assoc();
        $response['status'] = "sukses";
        $response['data'] = $product;
        $response['message'] = "Detail produk berhasil diambil.";
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Produk tidak ditemukan.";
    }

    $stmt->close();
} else {
    $response['status'] = "gagal";
    $response['message'] = "Parameter kode tidak diberikan.";
}

echo json_encode($response);

$conn->close();
?>