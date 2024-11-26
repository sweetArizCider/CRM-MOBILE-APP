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

// Obtener datos del formulario (asegúrate de enviar los datos por POST en Postman)
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Validar que los datos no estén vacíos
if (empty($email) || empty($password)) {
    die(json_encode([
        "success" => false,
        "message" => "Por favor, proporciona un correo y una contraseña."
    ]));
}

try {
    // Preparar la llamada al procedimiento almacenado
    $stmt = $conn->prepare("CALL LoginUser(?, ?)");

    // Verificar que la preparación fue exitosa
    if ($stmt === false) {
        die(json_encode([
            "success" => false,
            "message" => "Error al preparar la consulta: " . $conn->error
        ]));
    }

    // Vincular los parámetros
    $stmt->bind_param("ss", $email, $password);
    
    // Ejecutar la consulta
    $stmt->execute();
    
    // Obtener resultados
    $result = $stmt->get_result();
    
    // Verificar si se encontraron resultados
    if ($result->num_rows > 0) {
        $data = $result->fetch_assoc();
        $message = $data['message'] ?? 'Inicio de sesión exitoso.';
        $token = $data['token'] ?? null;

        // Verificar si el token fue generado
        if ($token) {
            // Inicio de sesión exitoso
            echo json_encode([
                "success" => true,
                "message" => $message,
                "token" => $token
            ]);
        } else {
            // Credenciales incorrectas o inicio de sesión fallido
            echo json_encode([
                "success" => false,
                "message" => $message
            ]);
        }
    } else {
        // No se encontraron resultados
        echo json_encode([
            "success" => false,
            "message" => "Credenciales incorrectas o error desconocido."
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
