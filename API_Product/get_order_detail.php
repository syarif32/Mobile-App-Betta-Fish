<?php
header('Content-Type: application/json');
include 'koneksi.php'; // Pastikan path ini benar untuk koneksi database Anda

$response = array();

if (isset($_GET['id_order'])) { // API endpoint Anda menggunakan GET untuk id_order
    $id_order = $_GET['id_order'];

    // Query untuk mengambil detail pesanan dari tbl_order
    // Tambahkan kolom 'metodebayar' dalam SELECT
    $query_order = "SELECT id_order, tgl_order, total_bayar, status, bukti_bayar, alamat_kirim, telp_kirim, kurir, service, ongkir, metodebayar FROM tbl_order WHERE id_order = ?"; // *** BARIS INI DITAMBAHKAN metodebayar ***
    $stmt_order = $conn->prepare($query_order);
    $stmt_order->bind_param("i", $id_order);
    $stmt_order->execute();
    $result_order = $stmt_order->get_result();

    if ($result_order->num_rows > 0) {
        $order_data = $result_order->fetch_assoc();

        // Mengambil detail produk dari tbl_order_detail dan tbl_product
        $query_products = "SELECT od.kode_product, od.harga_satuan, od.qty, p.merk, p.foto
                           FROM tbl_order_detail od
                           JOIN tbl_product p ON od.kode_product = p.kode
                           WHERE od.id_order = ?";
        $stmt_products = $conn->prepare($query_products);
        $stmt_products->bind_param("i", $id_order);
        $stmt_products->execute();
        $result_products = $stmt_products->get_result();

        $products = array();
        while ($row = $result_products->fetch_assoc()) {
            // Perhatikan path URL foto produk. Ini harusnya 'produk/' bukan 'bukti_bayar/'
            // Jika ini get_history_detail.php, mungkin ini path yang salah dari contoh sebelumnya
            // Sesuaikan dengan path sebenarnya di server Anda. Contoh:
            $row['foto'] = 'https://allend.site/API_Product/produk/' . $row['foto'];
            $products[] = $row;
        }

        $response['status'] = "sukses";
        $response['id_order'] = (int)$order_data['id_order']; // Pastikan integer
        $response['tgl_order'] = $order_data['tgl_order'];
        $response['total_bayar'] = (double)$order_data['total_bayar']; // Pastikan double
        $response['status_order'] = (string)$order_data['status']; // Pastikan string
        $response['bukti_bayar'] = $order_data['bukti_bayar'];
        $response['alamat_kirim'] = $order_data['alamat_kirim'];
        $response['telp_kirim'] = $order_data['telp_kirim'];
        $response['kurir'] = $order_data['kurir'];
        $response['service'] = $order_data['service'];
        $response['ongkir'] = (double)$order_data['ongkir']; // Pastikan double
        $response['metodebayar'] = (int)$order_data['metodebayar']; // *** BARIS INI DITAMBAHKAN ***
        $response['products'] = $products;

    } else {
        $response['status'] = "gagal";
        $response['message'] = "Detail pesanan tidak ditemukan.";
    }

    $stmt_order->close();
    $stmt_products->close();
} else {
    $response['status'] = "gagal";
    $response['message'] = "ID Pesanan tidak diberikan.";
}

echo json_encode($response);
$conn->close();
?>