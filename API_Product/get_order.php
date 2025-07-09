<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type");

require_once 'koneksi.php';

// Set waktu response unlimited
set_time_limit(0);

// Inisialisasi response
$response = [
    'status' => 'error',
    'message' => 'Unknown error',
    'data' => null
];

try {
    // Dapatkan raw input
    $jsonInput = file_get_contents('php://input');
    
    // Log raw input
    file_put_contents('api_input.log', date('Y-m-d H:i:s')." - Raw input: ".$jsonInput.PHP_EOL, FILE_APPEND);

    // Validasi metode request
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        throw new Exception("Metode request harus POST");
    }

    // Validasi JSON input
    if (empty($jsonInput)) {
        throw new Exception("Request body tidak boleh kosong");
    }

    $input = json_decode($jsonInput, true);
    
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Format JSON tidak valid: ".json_last_error_msg());
    }

    // Validasi field wajib
    if (!isset($input['id_pelanggan']) || empty($input['id_pelanggan'])) {
        throw new Exception("Field id_pelanggan wajib diisi");
    }

    $id_pelanggan = (int)$input['id_pelanggan'];
    
    // Log validasi berhasil
    file_put_contents('api_debug.log', date('Y-m-d H:i:s')." - Valid request for id_pelanggan: ".$id_pelanggan.PHP_EOL, FILE_APPEND);

    // Query database
    $stmt = $conn->prepare("
        SELECT 
            o.id_order,
            DATE_FORMAT(o.tgl_order, '%Y-%m-%d') as tgl_order,
            o.total_bayar,
            o.status,
            o.bukti_bayar,
            o.metodebayar, -- *** BARIS INI DITAMBAHKAN ***
            o.alamat_kirim,
            o.telp_kirim,
            o.kurir,
            o.service,
            o.provinsi,
            o.kota,
            o.lama_kirim,
            GROUP_CONCAT(CONCAT(p.merk, ' (', od.qty, 'x)') SEPARATOR ', ') AS produk,
            GROUP_CONCAT(od.kode_product) AS kode_produk
        FROM tbl_order o
        LEFT JOIN tbl_order_detail od ON o.id_order = od.id_order
        LEFT JOIN tbl_product p ON od.kode_product = p.kode
        WHERE o.id_pelanggan = ?
        GROUP BY o.id_order
        ORDER BY o.tgl_order DESC
    ");
    
    if (!$stmt) {
        throw new Exception("Prepare statement failed: ".$conn->error);
    }

    $stmt->bind_param("i", $id_pelanggan);
    
    if (!$stmt->execute()) {
        throw new Exception("Execute failed: ".$stmt->error);
    }

    $result = $stmt->get_result();
    $orders = [];

    while ($row = $result->fetch_assoc()) {
        // Format status
        $statusMap = [
            0 => "Menunggu Pembayaran",
            1 => "Pembayaran Diverifikasi",
            2 => "Sedang Diproses", 
            3 => "Dikirim",
            4 => "Selesai",
            5 => "Dibatalkan" // Pastikan ini ada di map jika status 5 digunakan
        ];
        
        $statusText = $statusMap[(int)$row['status']] ?? "Status Tidak Dikenal"; // Pastikan status adalah integer untuk map

        // Handle kode_produk NULL
        $kodeProduk = !empty($row['kode_produk']) ? explode(',', $row['kode_produk']) : [];

        $orders[] = [
            'id_order' => (int)$row['id_order'],
            'tgl_order' => $row['tgl_order'],
            'total_bayar' => (float)$row['total_bayar'],
            'status' => $statusText,
            'status_code' => (int)$row['status'],
            'produk' => $row['produk'] ?? '',
            'kode_produk' => $kodeProduk,
            'bukti_bayar' => $row['bukti_bayar'],
            'metodebayar' => isset($row['metodebayar']) ? (int)$row['metodebayar'] : null, // *** BARIS INI DITAMBAHKAN ***
            'alamat_kirim' => $row['alamat_kirim'] ?? '',
            'telp_kirim' => $row['telp_kirim'] ?? '',
            'kurir' => $row['kurir'] ?? '',
            'service' => $row['service'] ?? '',
            'provinsi' => $row['provinsi'] ?? '',
            'kota' => $row['kota'] ?? '',
            'lama_kirim' => $row['lama_kirim'] ?? ''
        ];
    }

    $response = [
        'status' => 'sukses',
        'message' => count($orders) > 0 ? 'Data ditemukan' : 'Tidak ada data order',
        'data' => $orders
    ];

} catch (Exception $e) {
    // Log error
    file_put_contents('api_error.log', date('Y-m-d H:i:s')." - Error: ".$e->getMessage().PHP_EOL, FILE_APPEND);
    
    $response = [
        'status' => 'gagal',
        'message' => $e->getMessage(),
        'data' => null
    ];
} finally {
    // Pastikan koneksi ditutup
    if (isset($stmt)) $stmt->close();
    if (isset($conn)) $conn->close();

    // Output response
    echo json_encode($response);
}
?>