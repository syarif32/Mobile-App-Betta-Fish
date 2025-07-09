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
    $required = ['id_pelanggan', 'tgl_order', 'subtotal', 'ongkir', 'total_bayar', 'kurir', 'service', 'asal', 'tujuan', 'alamat_kirim', 'telp_kirim', 'kodepos', 'metodebayar', 'produk'];
    foreach ($required as $field) {
        if (!isset($data[$field])) {
            throw new Exception("Field '$field' is required");
        }
    }

    // Determine initial status based on payment method
    $initial_status = 0; // Default to 'Menunggu Pembayaran'
    if (isset($data['metodebayar']) && (int)$data['metodebayar'] === 0) { // If payment method is COD (assuming 0 for COD)
        $initial_status = 2; // Set status to 'Pembayaran Diverifikasi' or 'Order Berhasil' directly
                               // Use 1 for 'Pembayaran Diverifikasi' as per your status map in get_order.php and HistoryDetail.java
                               // If you want it as 'Selesai' (4) directly, change to 4.
    }

    // Start transaction
    $conn->beginTransaction();

    // --- NEW: Step 1.5: Validate and Reserve Stock ---
    // Create a temporary array to hold product stock before actual update
    $productStockUpdates = [];

    // Prepare statement to get current stock
    $stmtCheckStock = $conn->prepare("SELECT stok FROM tbl_product WHERE kode = :kode_product FOR UPDATE"); // FOR UPDATE locks the row

    foreach ($data['produk'] as $product) {
        $kode_product = $product['kode_product'];
        $qty_ordered = $product['qty'];

        if (!isset($kode_product) || !isset($qty_ordered)) {
            throw new Exception("Product details (kode_product or qty) are missing in the request.");
        }

        // Get current stock
        $stmtCheckStock->execute([':kode_product' => $kode_product]);
        $current_stock = $stmtCheckStock->fetchColumn();

        if ($current_stock === false) {
            throw new Exception("Product with kode $kode_product not found.");
        }

        if ($current_stock < $qty_ordered) {
            throw new Exception("Stok produk '{$kode_product}' tidak mencukupi. Sisa stok: {$current_stock}. Jumlah diminta: {$qty_ordered}.");
        }

        // Calculate new stock and store for later update
        $productStockUpdates[$kode_product] = $current_stock - $qty_ordered;
    }
    // --- END NEW: Step 1.5: Validate and Reserve Stock ---

    // 1. Insert to tbl_order
    $sqlOrder = "INSERT INTO tbl_order (
        id_pelanggan, tgl_order, subtotal, ongkir, total_bayar,
        kurir, service, asal, tujuan, alamat_kirim, telp_kirim, kodepos, metodebayar, status
    ) VALUES (
        :id_pelanggan, :tgl_order, :subtotal, :ongkir, :total_bayar,
        :kurir, :service, :asal, :tujuan, :alamat_kirim, :telp_kirim, :kodepos, :metodebayar, :status
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
        ':telp_kirim' => $data['telp_kirim'] ?? null,
        ':kodepos' => $data['kodepos'] ?? null,
        ':metodebayar' => $data['metodebayar'] ?? null,
        ':status' => $initial_status // Use the determined initial status here
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

    // --- NEW: Step 3: Update Stock after successful order and details insertion ---
    $stmtUpdateStock = $conn->prepare("UPDATE tbl_product SET stok = :new_stock WHERE kode = :kode_product");
    foreach ($productStockUpdates as $kode => $new_stock_value) {
        $stmtUpdateStock->execute([
            ':new_stock' => $new_stock_value,
            ':kode_product' => $kode
        ]);
    }
    // --- END NEW: Step 3: Update Stock ---

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
    $conn->rollBack(); // Rollback on any general exception too
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