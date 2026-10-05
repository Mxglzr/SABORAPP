<?php
require_once 'conexion.php';

$metodo = $_SERVER['REQUEST_METHOD'];

switch ($metodo) {
    case 'GET':
        // Listar o Buscar platos (HU-05 y HU-07)
        $buscar = $_GET['buscar'] ?? '';
        $categoria = $_GET['categoria'] ?? '';

        $sql = "SELECT id, nombre, categoria, precio, disponible FROM plato WHERE 1=1";
        $params = [];

        if (!empty($buscar)) {
            $sql .= " AND nombre LIKE ?";
            $params[] = "%" . $buscar . "%";
        }

        if (!empty($categoria) && $categoria !== 'Todos') {
            $sql .= " AND categoria = ?";
            $params[] = $categoria;
        }

        $sql .= " ORDER BY categoria, nombre";
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $platos = $stmt->fetchAll();

        echo json_encode(["status" => true, "datos" => $platos]);
        break;

    case 'POST':
        // Registrar plato (HU-05)
        $data = json_decode(file_get_contents("php://input"), true);
        $nombre = trim($data['nombre'] ?? '');
        $categoria = trim($data['categoria'] ?? '');
        $precio = floatval($data['precio'] ?? 0);
        $disponible = isset($data['disponible']) ? intval($data['disponible']) : 1;

        if (empty($nombre) || empty($categoria)) {
            echo json_encode(["status" => false, "mensaje" => "Nombre y categoría son requeridos"]);
            exit;
        }

        if ($precio <= 0) {
            echo json_encode(["status" => false, "mensaje" => "Precio inválido (debe ser mayor a 0)"]);
            exit;
        }

        $stmt = $pdo->prepare("INSERT INTO plato (nombre, categoria, precio, disponible) VALUES (?, ?, ?, ?)");
        $stmt->execute([$nombre, $categoria, $precio, $disponible]);

        echo json_encode(["status" => true, "mensaje" => "Plato registrado correctamente", "id" => $pdo->lastInsertId()]);
        break;

    case 'PUT':
        // Actualizar plato (HU-07)
        $data = json_decode(file_get_contents("php://input"), true);
        $id = intval($data['id'] ?? 0);
        $nombre = trim($data['nombre'] ?? '');
        $categoria = trim($data['categoria'] ?? '');
        $precio = floatval($data['precio'] ?? 0);
        $disponible = isset($data['disponible']) ? intval($data['disponible']) : 1;

        if ($id <= 0 || empty($nombre) || empty($categoria) || $precio <= 0) {
            echo json_encode(["status" => false, "mensaje" => "Datos incompletos o precio inválido"]);
            exit;
        }

        $stmt = $pdo->prepare("UPDATE plato SET nombre = ?, categoria = ?, precio = ?, disponible = ? WHERE id = ?");
        $stmt->execute([$nombre, $categoria, $precio, $disponible, $id]);

        echo json_encode(["status" => true, "mensaje" => "Plato actualizado correctamente"]);
        break;

    case 'DELETE':
        // Eliminar plato validando si tiene pedidos (HU-07 CA2)
        $id = intval($_GET['id'] ?? 0);
        if ($id <= 0) {
            $data = json_decode(file_get_contents("php://input"), true);
            $id = intval($data['id'] ?? 0);
        }

        if ($id <= 0) {
            echo json_encode(["status" => false, "mensaje" => "ID inválido"]);
            exit;
        }

        // Validar si tiene pedidos
        $check = $pdo->prepare("SELECT COUNT(*) FROM detalle_pedido WHERE id_plato = ?");
        $check->execute([$id]);
        if ($check->fetchColumn() > 0) {
            echo json_encode(["status" => false, "mensaje" => "No se puede eliminar: tiene pedidos"]);
            exit;
        }

        $stmt = $pdo->prepare("DELETE FROM plato WHERE id = ?");
        $stmt->execute([$id]);

        echo json_encode(["status" => true, "mensaje" => "Plato eliminado correctamente"]);
        break;

    default:
        echo json_encode(["status" => false, "mensaje" => "Método no permitido"]);
        break;
}
?>
