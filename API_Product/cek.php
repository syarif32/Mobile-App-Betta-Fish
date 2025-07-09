<?php
require_once "cekconn.php";

// Enable error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Function to log errors
function log_error($message) {
    file_put_contents('error_log.txt', date('Y-m-d H:i:s') . " - " . $message . "\n", FILE_APPEND);
}

$response = ["status" => "", "message" => ""];

try {
    // Get JSON input
    $json = file_get_contents('php://input');
    $data = json_decode($json, true);

    // Validate input
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Invalid JSON format");
    }

    // Required fields validation
    $required = ['id_pelanggan', 'tgl_order', 'subtotal', 'ongkir', 'total_bayar', 'kurir', 'service', 'asal', 'tujuan', 'alamat_kirim', 'produk'];
    foreach ($required as $field) {
        if (!isset($data[$field])) {
            throw new Exception("Field $field is required");
        }
    }

    // Start transaction
    $conn->beginTransaction();

    // 1. Insert to tbl_order
    $sqlOrder = "INSERT INTO tbl_order (
        id_pelanggan, tgl_order, subtotal, ongkir, total_bayar, 
        kurir, service, asal, tujuan, alamat_kirim, telp_kirim
    ) VALUES (
        :id_pelanggan, :tgl_order, :subtotal, :ongkir, :total_bayar,
        :kurir, :service, :asal, :tujuan, :alamat_kirim, :telp_kirim
    )";

    $stmtOrder = $conn->prepare($sqlOrder);
    $stmtOrder->execute([
        ':id_pelanggan' => $data['id_pelanggan'],
        ':tgl_order' => $data['tgl_order'],
        ':subtotal' => $data['subtotal'],
        ':ongkir' => $data['ongkir'],
        ':total_bayar' => $data['total_bayar'],
        ':kurir' => $data['kurir'],
        ':service' => $data['service'],
        ':asal' => $data['asal'],
        ':tujuan' => $data['tujuan'],
        ':alamat_kirim' => $data['alamat_kirim'],
        ':telp_kirim' => $data['telp_kirim'] ?? null
    ]);

    $orderId = $conn->lastInsertId();

    // 2. Insert order details
    foreach ($data['produk'] as $product) {
        $sqlDetail = "INSERT INTO tbl_order_detail (
            id_order, kode_product, harga_satuan, qty, bayar
        ) VALUES (
            :id_order, :kode_product, :harga_satuan, :qty, :bayar
        )";

        $stmtDetail = $conn->prepare($sqlDetail);
        $stmtDetail->execute([
            ':id_order' => $orderId,
            ':kode_product' => $product['kode_product'],
            ':harga_satuan' => $product['harga_satuan'],
            ':qty' => $product['qty'],
            ':bayar' => $product['harga_satuan'] * $product['qty']
        ]);
    }

    // Commit transaction
    $conn->commit();

    $response = [
        "status" => "sukses",
        "message" => "Order berhasil dibuat",
        "id_order" => $orderId,
        "total_bayar" => $data['total_bayar']
    ];

} catch (PDOException $e) {
    $conn->rollBack();
    $response = [
        "status" => "gagal",
        "message" => "Database error: " . $e->getMessage(),
        "error_code" => $e->getCode()
    ];
    log_error("PDO Error: " . $e->getMessage());
    
} catch (Exception $e) {
    $response = [
        "status" => "gagal",
        "message" => $e->getMessage()
    ];
    log_error("General Error: " . $e->getMessage());
}

// Send JSON response
echo json_encode($response);

// Log the response for debugging
file_put_contents('transaction_log.txt', 
    "[" . date('Y-m-d H:i:s') . "] " . json_encode($data) . "\nResponse: " . json_encode($response) . "\n\n", 
    FILE_APPEND);
?>