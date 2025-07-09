<?php
include 'koneksi.php'; // pastikan file koneksi tersedia

$data = json_decode(file_get_contents("php://input"), true);

$id_pelanggan = $data['id_pelanggan'];
$total_harga = $data['total_harga'];
$ongkir = $data['ongkir'];
$kurir = $data['kurir'];
$service = $data['service'];
$asal = $data['asal'];
$tujuan = $data['tujuan'];
$alamat_pengiriman = $data['alamat_pengiriman'];
$tanggal = date("Y-m-d H:i:s");

// Simpan ke tbl_order
$queryOrder = "INSERT INTO tbl_order (
    id_pelanggan, tanggal_order, total_harga, ongkir, kurir, service, asal, tujuan, alamat_pengiriman
) VALUES (
    '$id_pelanggan', '$tanggal', '$total_harga', '$ongkir', '$kurir', '$service', '$asal', '$tujuan', '$alamat_pengiriman'
)";
mysqli_query($conn, $queryOrder) or die(mysqli_error($conn));
$id_order = mysqli_insert_id($conn);

// Simpan ke tbl_order_detail
foreach ($data['produk'] as $item) {
    $kode = $item['kode_produk'];
    $jumlah = $item['jumlah'];
    $harga = $item['harga_satuan'];

    $queryDetail = "INSERT INTO tbl_order_detail (
        id_order, kode_produk, jumlah, harga_satuan
    ) VALUES (
        '$id_order', '$kode', '$jumlah', '$harga'
    )";
    mysqli_query($conn, $queryDetail) or die(mysqli_error($conn));
}

echo json_encode([
    "status" => "sukses",
    "id_order" => $id_order
]);
?>
