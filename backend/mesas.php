<?php
require_once 'conexion.php';

$metodo = $_SERVER['REQUEST_METHOD'];

switch ($metodo) {
    case 'GET':
        // Listar mesas (HU-06)
        $stmt = $pdo->query("SELECT id, numero, capacidad, estado FROM mesa ORDER BY numero ASC");
        $mesas = $stmt->fetchAll();
        echo json_encode(["status" => true, "datos" => $mesas]);
        break;

    case 'POST':
        // Registrar mesa (HU-06)
        $data = json_decode(file_get_contents("php://input"), true);
        $numero = intval($data['numero'] ?? 0);
        $capacidad = intval($data['capacidad'] ?? 0);

        if ($numero <= 0) {
            echo json_encode(["status" => false, "mensaje" => "Número de mesa inválido"]);
            exit;
        }

        // CA2: Capacidad entre 1 y 12
        if ($capacidad < 1 || $capacidad > 12) {
            echo json_encode(["status" => false, "mensaje" => "Capacidad inválida (debe ser entre 1 y 12)"]);
            exit;
        }

        // CA1: Validar si la mesa ya existe
        $check = $pdo->prepare("SELECT COUNT(*) FROM mesa WHERE numero = ?");
        $check->execute([$numero]);
        if ($check->fetchColumn() > 0) {
            echo json_encode(["status" => false, "mensaje" => "La mesa ya existe"]);
            exit;
        }

        // CA3: Estado inicial LIBRE
        $stmt = $pdo->prepare("INSERT INTO mesa (numero, capacidad, estado) VALUES (?, ?, 'LIBRE')");
        $stmt->execute([$numero, $capacidad]);

        echo json_encode([
            "status" => true,
            "mensaje" => "Mesa registrada correctamente",
            "id" => $pdo->lastInsertId()
        ]);
        break;

    case 'PUT':
        // Cambiar estado de mesa (LIBRE / OCUPADA)
        $data = json_decode(file_get_contents("php://input"), true);
        $id = intval($data['id'] ?? 0);
        $estado = strtoupper(trim($data['estado'] ?? 'LIBRE'));

        if ($id <= 0 || !in_array($estado, ['LIBRE', 'OCUPADA'])) {
            echo json_encode(["status" => false, "mensaje" => "Datos de mesa inválidos"]);
            exit;
        }

        $stmt = $pdo->prepare("UPDATE mesa SET estado = ? WHERE id = ?");
        $stmt->execute([$estado, $id]);

        echo json_encode(["status" => true, "mensaje" => "Estado de mesa actualizado"]);
        break;

    default:
        echo json_encode(["status" => false, "mensaje" => "Método no permitido"]);
        break;
}
?>
