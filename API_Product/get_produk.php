<?php
// Koneksi ke database
$servername = "localhost";
$username = "dtip1888_cupangsarep"; // Ganti dengan username database Anda
$password = "0D[@IS=CCxNgfGNF"; // Ganti dengan password database Anda
$dbname = "dtip1888_cupangsarep";

// Buat koneksi
// Menggunakan MySQLi untuk koneksi
$conn = new mysqli($servername, $username, $password, $dbname);

// Cek koneksi
if ($conn->connect_error) {
    // Menghentikan eksekusi skrip dan menampilkan pesan error jika koneksi gagal
    die("Connection failed: " . $conn->connect_error);
}

// Query untuk mengambil data produk
// Mengambil semua kolom dari tabel tbl_product
$sql = "SELECT * FROM tbl_product";
$result = $conn->query($sql);

$products = array(); // Inisialisasi array kosong untuk menampung produk

if ($result->num_rows > 0) {
    // Jika ada baris data yang ditemukan
    // Loop melalui setiap baris hasil query
    while($row = $result->fetch_assoc()) {
        // Menambahkan setiap baris (sebagai array asosiatif) ke array $products
        $products[] = $row;
    }
} else {
    // Jika tidak ada hasil, Anda bisa memilih untuk tidak menampilkan apapun
    // atau mengembalikan array JSON kosong secara default (karena $products sudah kosong)
    // echo "0 results"; // Baris ini biasanya dihilangkan jika ingin output JSON murni
}

// Mengatur header respons HTTP menjadi JSON
header('Content-Type: application/json');

// Mengembalikan data produk dalam format JSON
echo json_encode($products);

// Menutup koneksi database
$conn->close();
?>