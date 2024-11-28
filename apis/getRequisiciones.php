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

// Consultar todas las requisiciones con la empresa asociada y orden
$query = "SELECT r.idRequisicion, r.fkidCliente, r.fechaCreacion, r.fechaEnvio, r.fechaEntrega, r.estado,
                 r.cantidadServicio, r.cantidadDinero, r.servicio, r.motivoCancelacion,
                 r.motivoPosposicion, r.motivoReembolso, r.fechaAlteracion, c.empresa
          FROM Requisiciones r
          JOIN Cliente c ON r.fkidCliente = c.idCliente
          ORDER BY r.fechaCreacion $order";
$result = $conn->query($query);

if ($result->num_rows > 0) {
    $requisiciones = [];
    while ($row = $result->fetch_assoc()) {
        $requisiciones[] = [
            "idRequisicion" => $row['idRequisicion'],
            "fkidCliente" => $row['fkidCliente'],
            "empresa" => $row['empresa'],  // Nombre de la empresa asociado al idCliente
            "fechaCreacion" => $row['fechaCreacion'],
            "fechaEnvio" => $row['fechaEnvio'],
            "fechaEntrega" => $row['fechaEntrega'],
            "estado" => $row['estado'],
            "cantidadServicio" => $row['cantidadServicio'],
            "cantidadDinero" => $row['cantidadDinero'],
            "servicio" => $row['servicio'],
            "motivoCancelacion" => $row['motivoCancelacion'],
            "motivoPosposicion" => $row['motivoPosposicion'],
            "motivoReembolso" => $row['motivoReembolso'],
            "fechaAlteracion" => $row['fechaAlteracion']
        ];
    }
    echo json_encode($requisiciones);
} else {
    echo json_encode([]);
}

$conn->close();
?>
