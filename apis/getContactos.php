<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);
header('Content-Type: application/json');

// Parámetros de conexión
$host = "bo7u6pimi9mondx2jxvm-mysql.services.clever-cloud.com";
$database = "bo7u6pimi9mondx2jxvm";
$user = "ujcxv1mcmvh3szov";
$password = "HC2zESAuuPDUBO3WLngB";
$port = 3306;

// Conexión
$conn = new mysqli($host, $user, $password, $database, $port);

// Verificar si hubo error en la conexión
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Error de conexión: " . $conn->connect_error]));
}

// Consultar todos los contactos
$query = "SELECT id_contacto, Nombre, Apellido, Direccion, Correo, Numero, Empresa FROM contactos";
$result = $conn->query($query);

// Verificar si la consulta fue exitosa
if (!$result) {
    die(json_encode(["success" => false, "message" => "Error en la consulta: " . $conn->error]));
}

if ($result->num_rows > 0) {
    $contactos = [];
    while ($row = $result->fetch_assoc()) {
        $contactos[] = [
            "id_contacto" => $row['id_contacto'],
            "nombre" => $row['Nombre'],
            "apellido" => $row['Apellido'],
            "direccion" => $row['Direccion'],
            "correo" => $row['Correo'],
            "numero" => $row['Numero'],
            "empresa" => $row['Empresa']
        ];
    }
    echo json_encode($contactos);
} else {
    echo json_encode([]);
}

$conn->close();
?>
