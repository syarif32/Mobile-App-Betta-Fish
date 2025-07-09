<?php
require_once 'koneksi.php';
header('Content-Type: application/json');

$response = array();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $id_pelanggan = $_POST['id_pelanggan'];

    $stmt = $conn->prepare("
        SELECT 
            o.id_order,
            o.tgl_order,
            o.total_bayar,
            o.status,
            GROUP_CONCAT(CONCAT(p.merk, ' (', d.qty, 'x)') SEPARATOR ', ') AS produk,
            GROUP_CONCAT(p.kode) AS kode_produk
        FROM tbl_order o
        JOIN tbl_order_detail d ON o.id_order = d.id_order
        JOIN tbl_product p ON d.kode_product = p.kode
        WHERE o.id_pelanggan = ?
        GROUP BY o.id_order
        ORDER BY o.id_order DESC
    ");

    $stmt->bind_param("i", $id_pelanggan);
    $stmt->execute();
    $result = $stmt->get_result();

    $data = array();
    while ($row = $result->fetch_assoc()) {
        $data[] = $row;
    }

    $response['status'] = 'sukses';
    $response['data'] = $data;
} else {
    $response['status'] = 'gagal';
    $response['message'] = 'Metode request harus POST';
}

echo json_encode($response);
?>
