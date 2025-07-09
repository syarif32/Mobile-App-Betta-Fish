<?php
$province_id = $_GET['province'];

$curl = curl_init();

curl_setopt_array($curl, [
    CURLOPT_URL => "https://api.rajaongkir.com/starter/city?province=$province_id",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_HTTPHEADER => [
        "key: 41a138f0f1c73ff0ebf6a5016b07ea0c"
    ],
]);

$response = curl_exec($curl);
$err = curl_error($curl);
curl_close($curl);

if ($err) {
    echo json_encode(["error" => $err]);
} else {
    echo $response;
}
?>
