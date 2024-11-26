<?php
// Depuración: ver qué datos están llegando al script
file_put_contents('php://stderr', print_r($_POST, true));

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

// Obtener los datos del formulario (asegúrate de enviar los datos por POST en Postman o tu app)
$nombre = $_POST['nombre'] ?? '';
$apellido = $_POST['apellido'] ?? '';
$direccion = $_POST['direccion'] ?? '';
$correo = $_POST['correo'] ?? '';
$numero = $_POST['numero'] ?? '';
$empresa = $_POST['empresa'] ?? '';

// Validar que los datos no estén vacíos
if (empty($nombre) || empty($apellido) || empty($direccion) || empty($correo) || empty($numero)) {
    die(json_encode([
        "success" => false,
        "message" => "Por favor, completa todos los campos."
    ]));
}

try {
    // Preparar la llamada al procedimiento almacenado
    $stmt = $conn->prepare("CALL addContacto(?, ?, ?, ?, ?, ?)");

    // Verificar que la preparación fue exitosa
    if ($stmt === false) {
        die(json_encode([
            "success" => false,
            "message" => "Error al preparar la consulta: " . $conn->error
        ]));
    }

    // Vincular los parámetros
    $stmt->bind_param("ssssss", $nombre, $apellido, $direccion, $correo, $numero, $empresa);
    
    // Ejecutar la consulta
    $stmt->execute();
    
    // Verificar si se ejecutó correctamente
    if ($stmt->affected_rows > 0) {
        echo json_encode([
            "success" => true,
            "message" => "Contacto registrado exitosamente."
        ]);
    } else {
        echo json_encode([
            "success" => false,
            "message" => "Hubo un problema al registrar el contacto."
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
