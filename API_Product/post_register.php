<?php
include "koneksi.php";
header('content-type: application/json');

$email=$_POST['email'];
$nama=$_POST['nama'];
$password=$_POST['password'];

$getstatus=0;
$getresult=0;
$message="";

$sql="select * from tbl_pelanggan where email='$email'";
$hasil=mysqli_query($conn,$sql);
if($hasil=mysqli_fetch_object($hasil)){
    $getstatus=0;
    $message="user sudah ada";
} else{
    $getstatus=1;
    // $sql="insert into tbl_pelanggan(nama, email, password, status) values('$nama','$email',md5('$password'),'1')";
    // $hasil=mysqli_query($conn,$sql);
    // if ($hasil){
        // $getresult=1;
        // $message="Simpan Berhasil";
    // } else {
        // $getresult=1;
        // $message="Simpan Gagal : ".mysqli_error($conn);
    // }
    // Hash password sebelum disimpan ke database
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    // Query untuk menyimpan data pelanggan
    $sql = "INSERT INTO tbl_pelanggan (status, nama, email, password) VALUES (1, ?, ?, ?)";
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "sss", $nama, $email, $hashed_password);
    mysqli_stmt_execute($stmt);

    if (mysqli_stmt_affected_rows($stmt) > 0) {
        $getresult=1;
        $message="Simpan Berhasil";
    } else {
        $getresult=1;
        $message="Simpan Gagal : ".mysqli_error($conn);
    }
}

echo json_encode(array('status'=>$getstatus,'result'=>$getresult,'message'=>$message));
?>