<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

$sql = "SELECT kode, merk, kategori, hargajual, stok, foto FROM tbl_product ORDER BY kode ASC";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $products = array();
    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }
    $response['status'] = "sukses";
    $response['data'] = $products;
    $response['message'] = "Data produk berhasil diambil.";
} else {
    $response['status'] = "sukses"; 
    $response['data'] = [];
    $response['message'] = "Tidak ada produk tersedia.";
}

echo json_encode($response);

$conn->close();
?>