<?php
// get_all_orders.php
include '../koneksi.php'; // Pastikan path ini benar relatif terhadap lokasi get_all_orders.php

header('Content-Type: application/json');

$response = array();

// Periksa apakah koneksi berhasil sebelum melanjutkan
if (!$conn) { // Menggunakan $conn sesuai dengan koneksi.php Anda
    $response['status'] = "gagal";
    $response['message'] = "Koneksi database gagal: " . mysqli_connect_error();
    echo json_encode($response);
    exit(); // Hentikan eksekusi script jika koneksi gagal
}

$sql = "SELECT
            o.id_order,
            o.id_pelanggan,
            p.nama AS nama_pelanggan,
            o.tgl_order,
            o.total_bayar,
            o.status,
            o.bukti_bayar,
            o.alamat_kirim,
            o.kurir,
            o.service,
            o.subtotal,
            o.ongkir
        FROM tbl_order o
        JOIN tbl_pelanggan p ON o.id_pelanggan = p.id  -- <--- PERUBAHAN PENTING DI SINI
        ORDER BY o.tgl_order DESC";

$result = $conn->query($sql); // Menggunakan $conn di sini

if ($result) {
    $orders = array();
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $orders[] = $row;
        }
        $response['status'] = "sukses";
        $response['message'] = "Data riwayat order berhasil diambil.";
        $response['data'] = $orders;
    } else {
        $response['status'] = "sukses"; // Tetap sukses jika tidak ada data, hanya data kosong
        $response['message'] = "Tidak ada riwayat order ditemukan.";
        $response['data'] = array();
    }
} else {
    $response['status'] = "gagal";
    $response['message'] = "Terjadi kesalahan SQL: " . $conn->error; // Menggunakan $conn di sini
}

echo json_encode($response);

$conn->close(); // Menggunakan $conn di sini
?>