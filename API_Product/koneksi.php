<?php
$host = "localhost"; // Ganti dengan host database Anda
$user = "dtip1888_cupangsarep"; // Ganti dengan username database Anda
$pass = "0D[@IS=CCxNgfGNF"; // Ganti dengan password database Anda
$db   = "dtip1888_cupangsarep"; // Nama database

$conn = mysqli_connect($host, $user, $pass, $db);

if (!$conn) {
    die("Koneksi gagal: " . mysqli_connect_error());
}
?>
