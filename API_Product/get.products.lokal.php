<?php
$servername = "localhost";
$username = "dtip1888_cupangsarep";
$password = "0D[@IS=CCxNgfGNF";
$dbname = "dtip1888_cupangsarep";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT * FROM tbl_product WHERE kode LIKE 'CL%'";
$result = $conn->query($sql);

$products = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $products[] = $row;
    }
}

header('Content-Type: application/json');
echo json_encode($products);

$conn->close();
?>
