<?php
include "koneksi.php";
header('Content-Type: application/json');

if (isset($_GET['email'])) {
    $email = mysqli_real_escape_string($conn, $_GET['email']);

    $sql = "SELECT email, nama AS name, alamat, kota, provinsi, kodepos, telp, foto FROM tbl_pelanggan WHERE email = ?";
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "s", $email);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($data = mysqli_fetch_assoc($result)) {
        if (!empty($data['foto'])) {
            $baseUrl = "https://allend.site/API_Product/"; // GANTI dengan IP lokal kamu
            $data['foto'] = $baseUrl . $data['foto'];
        }

        echo json_encode([
            'result' => 1,
            'data' => $data
        ]);
    } else {
        echo json_encode([
            'result' => 0,
            'message' => 'Data tidak ditemukan',
            'data' => null
        ]);
    }

    mysqli_stmt_close($stmt);
} else {
    echo json_encode([
        'result' => 0,
        'message' => 'Parameter email tidak ditemukan',
        'data' => null
    ]);
}

mysqli_close($conn);
?>
