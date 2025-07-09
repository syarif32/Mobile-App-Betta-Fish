<?php
include 'koneksi.php'; // koneksi ke database

$kode = $_POST['kode'];

if ($kode) {
    $query = "UPDATE tbl_product SET view = view + 1 WHERE kode = '$kode'";
    $result = mysqli_query($conn, $query);

    if ($result) {
        echo json_encode(['success' => true, 'message' => 'View updated']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Update failed']);
    }
} else {
    echo json_encode(['success' => false, 'message' => 'Kode is required']);
}
?>
