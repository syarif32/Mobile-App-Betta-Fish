<?php
include 'koneksi.php'; // file koneksi ke database

header("Content-Type: application/json; charset=UTF-8");

$query = "SELECT * FROM tbl_product ORDER BY view DESC LIMIT 3";
$result = mysqli_query($conn, $query);

$data = array();

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode($data);
?>
