<?php
include '../koneksi.php'; // Sesuaikan path koneksi

$response = array();

if (isset($_GET['id_order'])) {
    $id_order = $_GET['id_order'];

    // Query untuk detail order utama dari tbl_order
    $query_order = "SELECT id_order, id_pelanggan, tgl_order, subtotal, ongkir, total_bayar, kurir, service, alamat_kirim, telp_kirim, kota, provinsi, lama_kirim, kodepos, metodebayar, bukti_bayar, status
                    FROM tbl_order WHERE id_order = ?";
    $stmt_order = $conn->prepare($query_order);
    $stmt_order->bind_param("i", $id_order);
    $stmt_order->execute();
    $result_order = $stmt_order->get_result();

    if ($result_order->num_rows > 0) {
        $order_data = $result_order->fetch_assoc();

        // Query untuk detail produk dalam order
        $query_products = "SELECT od.kode_product, od.harga_satuan, od.qty, od.bayar, p.merk, p.foto
                           FROM tbl_order_detail od
                           JOIN tbl_product p ON od.kode_product = p.kode
                           WHERE od.id_order = ?";
        $stmt_products = $conn->prepare($query_products);
        $stmt_products->bind_param("i", $id_order);
        $stmt_products->execute();
        $result_products = $stmt_products->get_result();

        $products = array();
        while ($row = $result_products->fetch_assoc()) {
            $products[] = $row;
        }

        // Query untuk detail pelanggan
        $query_pelanggan = "SELECT nama, email, telp FROM tbl_pelanggan WHERE id = ?";
        $stmt_pelanggan = $conn->prepare($query_pelanggan);
        $stmt_pelanggan->bind_param("i", $order_data['id_pelanggan']);
        $stmt_pelanggan->execute();
        $result_pelanggan = $stmt_pelanggan->get_result();
        $pelanggan_data = $result_pelanggan->fetch_assoc();
        $stmt_pelanggan->close();


        $response['status'] = "sukses";
        $response['data'] = [
            'order' => $order_data,
            'products' => $products,
            'pelanggan' => $pelanggan_data // Tambahkan data pelanggan
        ];
        $response['message'] = "Detail order berhasil diambil.";

    } else {
        $response['status'] = "gagal";
        $response['message'] = "Order tidak ditemukan.";
    }

    $stmt_order->close();
    $stmt_products->close();
} else {
    $response['status'] = "gagal";
    $response['message'] = "ID Order tidak diberikan.";
}

echo json_encode($response);
$conn->close();
?>