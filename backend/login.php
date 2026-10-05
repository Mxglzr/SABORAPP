<?php
require_once 'conexion.php';

$data = json_decode(file_get_contents("php://input"), true);
$usuario = $data['usuario'] ?? '';
$clave = $data['clave'] ?? '';

if (empty($usuario) || empty($clave)) {
    echo json_encode(["status" => false, "mensaje" => "Complete todos los campos"]);
    exit;
}

$stmt = $pdo->prepare("SELECT id, usuario, rol FROM usuario WHERE usuario = ? AND clave = ?");
$stmt->execute([$usuario, $clave]);
$user = $stmt->fetch();

if ($user) {
    echo json_encode([
        "status" => true,
        "mensaje" => "Acceso correcto",
        "usuario" => $user
    ]);
} else {
    echo json_encode(["status" => false, "mensaje" => "Credenciales incorrectas"]);
}
?>  