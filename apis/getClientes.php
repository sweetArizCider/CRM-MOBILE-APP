<?php
header('Content-Type: application/json');

// Parámetros de conexión
$host = "bo7u6pimi9mondx2jxvm-mysql.services.clever-cloud.com";
$database = "bo7u6pimi9mondx2jxvm";
$user = "ujcxv1mcmvh3szov";
$password = "HC2zESAuuPDUBO3WLngB";
$port = 3306;

// Conexión
$conn = new mysqli($host, $user, $password, $database, $port);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Error de conexión"]));
}

// Obtener el parámetro 'order' (ascendente o descendente)
$order = isset($_GET['order']) ? $_GET['order'] : 'ASC';  // Por defecto 'ASC'

// Consultar clientes con orden
$query = "SELECT empresa, CdMatriz AS ciudad, estatus FROM Cliente ORDER BY empresa $order";
$result = $conn->query($query);

if ($result->num_rows > 0) {
    $clientes = [];
    while ($row = $result->fetch_assoc()) {
        // Cambiar CdMatriz por ciudad
        $clientes[] = [
            "empresa" => $row['empresa'],
            "ciudad" => $row['ciudad'], // Usamos 'ciudad' en lugar de 'CdMatriz'
            "estatus" => $row['estatus']
        ];
    }
    echo json_encode($clientes);
} else {
    echo json_encode([]);
}

$conn->close();
?>
