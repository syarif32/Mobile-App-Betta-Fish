<?php


$origin = $_POST['origin'];
$destination = $_POST['destination'];
$weight = $_POST['weight']; // dalam gram
$courier = $_POST['courier'];

$curl = curl_init();

curl_setopt_array($curl, [
    CURLOPT_URL => "https://api.rajaongkir.com/starter/cost",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => http_build_query([
        'origin' => $origin,
        'destination' => $destination,
        'weight' => $weight,
        'courier' => $courier
    ]),
    CURLOPT_HTTPHEADER => [
        "key:41a138f0f1c73ff0ebf6a5016b07ea0c ", // Ganti dengan API Key kamu
        "content-type: application/x-www-form-urlencoded"
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
