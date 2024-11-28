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

// Obtener datos del formulario (asegúrate de enviar los datos por POST en Postman o tu app)
$p_idRequisicion = $_POST['idRequisicion'] ?? '';
$p_estado = $_POST['estado'] ?? '';
$p_cantidadServicio = $_POST['cantidadServicio'] ?? NULL;
$p_cantidadDinero = $_POST['cantidadDinero'] ?? NULL;
$p_servicio = $_POST['servicio'] ?? NULL;
$p_fechaAlteracion = $_POST['fechaAlteracion'] ?? NULL;
$p_motivoCancelacion = $_POST['motivoCancelacion'] ?? NULL;
$p_motivoPosposicion = $_POST['motivoPosposicion'] ?? NULL;
$p_motivoReembolso = $_POST['motivoReembolso'] ?? NULL;

// Validar que los datos no estén vacíos
if (empty($p_idRequisicion) || empty($p_estado)) {
    die(json_encode([
        "success" => false,
        "message" => "Por favor, completa todos los campos obligatorios."
    ]));
}

try {
    // Preparar la llamada al procedimiento almacenado
    $stmt = $conn->prepare("CALL ModificarRequisicion(?, ?, ?, ?, ?, ?, ?, ?, ?)");

    // Verificar que la preparación fue exitosa
    if ($stmt === false) {
        die(json_encode([
            "success" => false,
            "message" => "Error al preparar la consulta: " . $conn->error
        ]));
    }

    // Vincular los parámetros
    $stmt->bind_param("isddddssss", $p_idRequisicion, $p_estado, $p_cantidadServicio, $p_cantidadDinero, $p_servicio, $p_fechaAlteracion, $p_motivoCancelacion, $p_motivoPosposicion, $p_motivoReembolso);

    // Ejecutar la consulta
    $stmt->execute();

    // Verificar si la ejecución fue exitosa y se realizaron cambios
    if ($stmt->affected_rows > 0) {
        echo json_encode([
            "success" => true,
            "message" => "Requisición modificada con éxito",
            "idRequisicion" => $p_idRequisicion
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "No se pudo modificar la requisición o no se encontraron cambios."
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
