<?php
// Establecer el encabezado para respuesta en formato JSON
header('Content-Type: application/json');

// Parámetros de conexión
$host = "bo7u6pimi9mondx2jxvm-mysql.services.clever-cloud.com";
$database = "bo7u6pimi9mondx2jxvm";
$user = "ujcxv1mcmvh3szov";
$password = "HC2zESAuuPDUBO3WLngB";
$port = 3306;

// Crear la conexión
$conn = new mysqli($host, $user, $password, $database, $port);

// Verificar conexión
if ($conn->connect_error) {
    die(json_encode([
        "success" => false,
        "message" => "Error de conexión: " . $conn->connect_error
    ]));
}

// Obtener datos del formulario
$fkidCliente = $_POST['fkidCliente'] ?? '';
$fechaCreacion = $_POST['fechaCreacion'] ?? '';
$cantidadServicio = $_POST['cantidadServicio'] ?? '';
$cantidadDinero = $_POST['cantidadDinero'] ?? '';
$servicio = $_POST['servicio'] ?? '';

// Validar que los datos no estén vacíos
if (empty($fkidCliente) || empty($fechaCreacion) || empty($cantidadServicio) || empty($cantidadDinero) || empty($servicio)) {
    die(json_encode([
        "success" => false,
        "message" => "Por favor, completa todos los campos."
    ]));
}

try {
    // Preparar la llamada al procedimiento almacenado
    $stmt = $conn->prepare("CALL CrearRequisicion(?, ?, ?, ?, ?)");

    // Verificar que la preparación fue exitosa
    if ($stmt === false) {
        die(json_encode([
            "success" => false,
            "message" => "Error al preparar la consulta: " . $conn->error
        ]));
    }

    // Vincular los parámetros
    $stmt->bind_param("isdss", $fkidCliente, $fechaCreacion, $cantidadServicio, $cantidadDinero, $servicio);

    // Ejecutar la consulta
    $stmt->execute();

    // Obtener resultados
    $result = $stmt->get_result();

    // Verificar si se obtuvo un resultado (mensaje de éxito o error)
    if ($result) {
        $data = $result->fetch_assoc();
        $message = $data['message'] ?? 'Operación completada exitosamente';
        $idRequisicion = $data['idRequisicion'] ?? null;

        echo json_encode([
            "success" => true,
            "message" => $message,
            "idRequisicion" => $idRequisicion
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "No se pudo crear la requisición."
        ]);
    }

    // Cerrar la consulta y la conexión
    $stmt->close();
    $conn->close();
} catch (Exception $e) {
    echo json_encode([
        "success" => false,
        "message" => "Error: " . $e->getMessage()
    ]);
}
?>
