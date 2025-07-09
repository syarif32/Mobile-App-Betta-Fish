<?php
header("Content-Type: application/json");
require_once '../config/database.php';

$id_order = $_POST['id_order'];
$target_dir = "upload/";
$file_name = basename($_FILES["buktiBayar"]["name"]);
$target_file = $target_dir . uniqid() . "_" . $file_name;
$uploadOk = 1;
$imageFileType = strtolower(pathinfo($target_file, PATHINFO_EXTENSION));

// Check if image file is a actual image or fake image
if(isset($_POST["submit"])) {
    $check = getimagesize($_FILES["buktiBayar"]["tmp_name"]);
    if($check !== false) {
        $uploadOk = 1;
    } else {
        $response = array("status" => "error", "message" => "File is not an image.");
        $uploadOk = 0;
    }
}

// Check file size (max 2MB)
if ($_FILES["buktiBayar"]["size"] > 2000000) {
    $response = array("status" => "error", "message" => "Sorry, your file is too large.");
    $uploadOk = 0;
}

// Allow certain file formats
if($imageFileType != "jpg" && $imageFileType != "png" && $imageFileType != "jpeg" && $imageFileType != "gif" ) {
    $response = array("status" => "error", "message" => "Sorry, only JPG, JPEG, PNG & GIF files are allowed.");
    $uploadOk = 0;
}

// Check if $uploadOk is set to 0 by an error
if ($uploadOk == 0) {
    echo json_encode($response);
} else {
    if (move_uploaded_file($_FILES["buktiBayar"]["tmp_name"], $target_file)) {
        // Update database
        $query = "UPDATE tbl_order SET bukti_bayar = ?, status = 1 WHERE id_order = ?";
        $stmt = $conn->prepare($query);
        $bukti_path = "uploadimage/bukti_bayar/" . basename($target_file);
        $stmt->bind_param("si", $bukti_path, $id_order);
        $stmt->execute();
        
        $response = array("status" => "sukses", "message" => "Bukti pembayaran berhasil diupload.");
        echo json_encode($response);
    } else {
        $response = array("status" => "error", "message" => "Sorry, there was an error uploading your file.");
        echo json_encode($response);
    }
}
?>