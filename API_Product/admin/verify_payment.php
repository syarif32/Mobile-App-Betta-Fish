<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $id_order = $_POST['id_order'] ?? '';
    $action = $_POST['action'] ?? ''; // "approve" atau "reject"

    if (empty($id_order) || empty($action)) {
        $response['status'] = "gagal";
        $response['message'] = "ID Order atau Aksi tidak diberikan.";
        echo json_encode($response);
        exit();
    }

    $new_status = '';
    $message = '';

    if ($action == 'approve') {
        $new_status = 2; // Status: Pesanan Diproses
        $message = "Pembayaran berhasil diverifikasi. Status order diperbarui menjadi Order Berhasil.";

        // --- LOGIKA PENGURANGAN STOK SAAT DIAPPROVE ---
        // 1. Ambil detail produk dari tbl_order_detail untuk id_order ini
        $stmt_order_detail = $conn->prepare("SELECT kode_product, qty FROM tbl_order_detail WHERE id_order = ?");
        $stmt_order_detail->bind_param("i", $id_order);
        $stmt_order_detail->execute();
        $result_order_detail = $stmt_order_detail->get_result();

        if ($result_order_detail->num_rows > 0) {
            // Loop melalui setiap produk dalam order
            while ($row = $result_order_detail->fetch_assoc()) {
                $kode_product = $row['kode_product'];
                $qty_ordered = $row['qty'];

                // 2. Kurangi stok di tbl_product
                $stmt_update_stock = $conn->prepare("UPDATE tbl_product SET stok = stok - ? WHERE kode = ?");
                $stmt_update_stock->bind_param("is", $qty_ordered, $kode_product);

                if (!$stmt_update_stock->execute()) {
                    // Jika gagal update stok, log error tapi tetap lanjutkan proses order
                    // Anda bisa memilih untuk membatalkan seluruh transaksi jika update stok gagal
                    error_log("Gagal mengurangi stok untuk produk " . $kode_product . ": " . $stmt_update_stock->error);
                    // Opsi: $response['message'] .= " (Peringatan: Gagal update stok produk " . $kode_product . ")";
                }
                $stmt_update_stock->close();
            }
        }
        $stmt_order_detail->close();
        // --- AKHIR LOGIKA PENGURANGAN STOK ---

    } elseif ($action == 'reject') {
        $new_status = 5; // Status: Dibatalkan
        $message = "Pembayaran ditolak. Status order diperbarui menjadi Dibatalkan.";
        // TIDAK ADA PENGURANGAN STOK JIKA DITOLAK
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Aksi tidak valid.";
        echo json_encode($response);
        exit();
    }

    // Update status di tbl_order
    $stmt = $conn->prepare("UPDATE tbl_order SET status = ? WHERE id_order = ?");
    $stmt->bind_param("ii", $new_status, $id_order);

    if ($stmt->execute()) {
        $response['status'] = "sukses";
        $response['message'] = $message;
    } else {
        $response['status'] = "gagal";
        $response['message'] = "Gagal memperbarui status order: " . $stmt->error;
    }

    $stmt->close();

} else {
    $response['status'] = "gagal";
    $response['message'] = "Metode request tidak valid.";
}

echo json_encode($response);
$conn->close();
?>