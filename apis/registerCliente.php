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
$empresa = $_POST['empresa'] ?? '';
$cdMatriz = $_POST['cdMatriz'] ?? '';
$presupuesto = $_POST['presupuesto'] ?? '';
$estatus = $_POST['estatus'] ?? '';
$calle = $_POST['calle'] ?? '';
$cp = $_POST['cp'] ?? '';
$numExterior = $_POST['numExterior'] ?? '';

// Validar que los datos no estén vacíos
if (empty($empresa) || empty($cdMatriz) || empty($presupuesto) || empty($estatus) || empty($calle) || empty($cp) || empty($numExterior)) {
    die(json_encode([
        "success" => false,
        "message" => "Por favor, completa todos los campos."
    ]));
}

try {
    // Preparar la llamada al procedimiento almacenado
    $stmt = $conn->prepare("CALL InsertarClienteConDireccion(?, ?, ?, ?, ?, ?, ?)");

    // Verificar que la preparación fue exitosa
    if ($stmt === false) {
        die(json_encode([
            "success" => false,
            "message" => "Error al preparar la consulta: " . $conn->error
        ]));
    }

    // Vincular los parámetros
    $stmt->bind_param("ssdssss", $empresa, $cdMatriz, $presupuesto, $estatus, $calle, $cp, $numExterior);
    
    // Ejecutar la consulta
    $stmt->execute();
    
    // Obtener resultados
    $result = $stmt->get_result();
    
    // Verificar si se obtuvo un resultado (mensaje de éxito o error)
    if ($result) {
        $data = $result->fetch_assoc();
        $message = $data['message'] ?? 'Operación completada exitosamente';

        echo json_encode([
            "success" => true,
            "message" => $message
        ]);
    } else {
        echo json_encode([
            "success" => true,
            "message" => "El cliente se ha registrado exitosamente."
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
