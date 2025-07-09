<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

require_once 'koneksi.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $json = file_get_contents('php://input');
    $input = json_decode($json, true);

    $id_order = $input['id_order'] ?? null;

    if ($id_order) {
        // Ambil detail order utama
        $stmt = $conn->prepare("SELECT id_order, tgl_order, total_bayar, status FROM tbl_order WHERE id_order = ?");
        $stmt->bind_param("i", $id_order);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $order = $result->fetch_assoc();

            // Ambil detail produk dalam order
            $stmt_detail = $conn->prepare("
                SELECT od.kode_product, p.merk, p.foto, od.harga_satuan, od.qty, od.bayar
                FROM tbl_order_detail od
                JOIN tbl_product p ON od.kode_product = p.kode
                WHERE od.id_order = ?
            ");
            $stmt_detail->bind_param("i", $id_order);
            $stmt_detail->execute();
            $result_detail = $stmt_detail->get_result();

            $products = array();
            while ($row = $result_detail->fetch_assoc()) {
                $row['foto'] = 'http://192.168.1.3/API_Product/bukti_bayar/' . $row['foto'];
                $products[] = $row;
            }

            $response['status'] = 'sukses';
            $response['order'] = $order;
            $response['produk'] = $products;
        } else {
            $response['status'] = 'gagal';
            $response['message'] = 'Data tidak ditemukan';
        }
    } else {
        $response['status'] = 'gagal';
        $response['message'] = 'ID Order tidak ditemukan';
    }
} else {
    $response['status'] = 'gagal';
    $response['message'] = 'Metode request tidak valid';
}

echo json_encode($response);
?>
