<?php
require_once 'koneksi.php'; // koneksi ke database

header('Content-Type: application/json');

// Ambil data dari POST
$email = $_POST['email'] ?? '';
$new_password = $_POST['new_password'] ?? '';

$response = array();

// Validasi input
if (empty($email) || empty($new_password)) {
    $response['result'] = 0;
    $response['message'] = 'Email dan password baru harus diisi.';
    echo json_encode($response);
    exit;
}

// Hash password baru menggunakan password_hash
$hashed_password = password_hash($new_password, PASSWORD_DEFAULT);

// Cek apakah email ada di tbl_pelanggan
$query = "SELECT * FROM tbl_pelanggan WHERE email = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    $response['result'] = 0;
    $response['message'] = 'Email tidak ditemukan di database.';
} else {
    // Update password
    $updateQuery = "UPDATE tbl_pelanggan SET password = ? WHERE email = ?";
    $updateStmt = $conn->prepare($updateQuery);
    $updateStmt->bind_param("ss", $hashed_password, $email);

    if ($updateStmt->execute()) {
        $response['result'] = 1;
        $response['message'] = 'Password berhasil direset.';
    } else {
        $response['result'] = 0;
        $response['message'] = 'Gagal mengupdate password.';
    }
}

echo json_encode($response);
?>
