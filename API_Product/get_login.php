<?php
include "koneksi.php";
header('content-type: application/json');

$email = $_POST['email'];
$password = $_POST['password']; // Password yang diinput oleh pengguna
$datauser = array();
$getstatus = 0;

// Query untuk mengambil data pelanggan berdasarkan email
$sql = "SELECT * FROM tbl_pelanggan WHERE email = ?";
$stmt = mysqli_prepare($conn, $sql);
mysqli_stmt_bind_param($stmt, "s", $email);
mysqli_stmt_execute($stmt);
$hasil = mysqli_stmt_get_result($stmt);
$data = mysqli_fetch_object($hasil);

if ($data) {
    // Verifikasi password yang diinput dengan password yang di-hash di database
    if (password_verify($password, $data->password)) {
        $getstatus = 1; // Login berhasil
        $datauser = array(
            
            "id"=> $data->id,
            'nama' => $data->nama, 
            'email' => $data->email, 
            'status' => $data->status
        );
    } else {
        $getstatus = 0; // Password tidak cocok
    }
} else {
    $getstatus = 0; // Email tidak ditemukan
}

// Mengembalikan hasil dalam format JSON
echo json_encode(array('result' => $getstatus, 'data' => $datauser));
?>