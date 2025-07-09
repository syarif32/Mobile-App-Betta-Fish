<?php
include 'koneksi.php';

if (isset($_GET['kategori_id'])) {
    $prefix = $_GET['kategori_id']; // CI atau CL

    // Cari produk yang kode-nya diawali prefix
    $stmt = $conn->prepare("SELECT * FROM tbl_product WHERE kode LIKE CONCAT(?, '%')");
    $stmt->bind_param("s", $prefix);
    $stmt->execute();
    $result = $stmt->get_result();

    $products = [];

    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }

    echo json_encode($products);
} else {
    echo json_encode([
        "error" => true,
        "message" => "Parameter kategori_id tidak ditemukan"
    ]);
}
?>
