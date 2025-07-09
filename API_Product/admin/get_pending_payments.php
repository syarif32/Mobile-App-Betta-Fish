<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

// Mengambil order dengan status 1 (Menunggu Konfirmasi Pembayaran)
$sql = "SELECT id_order, id_pelanggan, tgl_order, subtotal, ongkir, total_bayar, kurir, service, alamat_kirim, telp_kirim, bukti_bayar, status FROM tbl_order WHERE status = 1 ORDER BY tgl_order DESC";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $orders = array();
    while ($row = $result->fetch_assoc()) {
        // Anda bisa menambahkan detail pelanggan jika perlu, tapi untuk kesederhanaan, hanya detail order
        $orders[] = $row;
    }
    $response['status'] = "sukses";
    $response['data'] = $orders;
    $response['message'] = "Daftar pembayaran menunggu verifikasi berhasil diambil.";
} else {
    $response['status'] = "sukses";
    $response['data'] = [];
    $response['message'] = "Tidak ada pembayaran yang menunggu verifikasi.";
}

echo json_encode($response);
$conn->close();
?>