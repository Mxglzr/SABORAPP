<?php
require_once 'conexion.php';

$metodo = $_SERVER['REQUEST_METHOD'];

switch ($metodo) {
    case 'GET':
        // Obtener pedido abierto por mesa, o por ID de pedido
        $id_mesa = intval($_GET['id_mesa'] ?? 0);
        $id_pedido = intval($_GET['id_pedido'] ?? 0);

        if ($id_pedido > 0) {
            $stmt = $pdo->prepare("SELECT p.*, m.numero as numero_mesa FROM pedido p JOIN mesa m ON p.id_mesa = m.id WHERE p.id = ?");
            $stmt->execute([$id_pedido]);
            $pedido = $stmt->fetch();
        } elseif ($id_mesa > 0) {
            $stmt = $pdo->prepare("SELECT p.*, m.numero as numero_mesa FROM pedido p JOIN mesa m ON p.id_mesa = m.id WHERE p.id_mesa = ? AND p.estado = 'ABIERTO' ORDER BY p.id DESC LIMIT 1");
            $stmt->execute([$id_mesa]);
            $pedido = $stmt->fetch();
        } else {
            echo json_encode(["status" => false, "mensaje" => "Parámetro inválido"]);
            exit;
        }

        if (!$pedido) {
            echo json_encode(["status" => false, "mensaje" => "No hay pedido abierto", "datos" => null]);
            exit;
        }

        // Obtener detalles del pedido con JOIN plato (HU-09 CA1)
        $stmtDetalle = $pdo->prepare("
            SELECT d.id, d.id_plato, pl.nombre as nombre_plato, d.cantidad, d.precio_unit, d.subtotal
            FROM detalle_pedido d
            JOIN plato pl ON d.id_plato = pl.id
            WHERE d.id_pedido = ?
        ");
        $stmtDetalle->execute([$pedido['id']]);
        $detalles = $stmtDetalle->fetchAll();

        $pedido['detalles'] = $detalles;

        echo json_encode(["status" => true, "datos" => $pedido]);
        break;

    case 'POST':
        $data = json_decode(file_get_contents("php://input"), true);
        $accion = $data['accion'] ?? 'AGREGAR_PLATO';

        if ($accion === 'AGREGAR_PLATO') {
            // HU-08: Tomar pedido por mesa con transacciones
            $id_mesa = intval($data['id_mesa'] ?? 0);
            $id_plato = intval($data['id_plato'] ?? 0);
            $cantidad = intval($data['cantidad'] ?? 0);

            if ($id_mesa <= 0 || $id_plato <= 0 || $cantidad <= 0) {
                echo json_encode(["status" => false, "mensaje" => "Datos de pedido inválidos"]);
                exit;
            }

            try {
                $pdo->beginTransaction();

                // 1. Obtener o crear pedido ABIERTO para la mesa
                $stmtPed = $pdo->prepare("SELECT id FROM pedido WHERE id_mesa = ? AND estado = 'ABIERTO' LIMIT 1");
                $stmtPed->execute([$id_mesa]);
                $pedido = $stmtPed->fetch();

                if (!$pedido) {
                    $stmtNuevoPed = $pdo->prepare("INSERT INTO pedido (id_mesa, fecha, estado, total) VALUES (?, NOW(), 'ABIERTO', 0.00)");
                    $stmtNuevoPed->execute([$id_mesa]);
                    $id_pedido = $pdo->lastInsertId();

                    // Marcar mesa como OCUPADA (HU-08 CA2)
                    $stmtMesa = $pdo->prepare("UPDATE mesa SET estado = 'OCUPADA' WHERE id = ?");
                    $stmtMesa->execute([$id_mesa]);
                } else {
                    $id_pedido = $pedido['id'];
                }

                // 2. Obtener precio actual del plato
                $stmtPlato = $pdo->prepare("SELECT precio FROM plato WHERE id = ?");
                $stmtPlato->execute([$id_plato]);
                $precio_unit = floatval($stmtPlato->fetchColumn());

                if ($precio_unit <= 0) {
                    throw new Exception("El plato no tiene un precio válido");
                }

                $subtotal = $cantidad * $precio_unit;

                // 3. Insertar detalle con precio_unit histórico
                $stmtDetalle = $pdo->prepare("INSERT INTO detalle_pedido (id_pedido, id_plato, cantidad, precio_unit, subtotal) VALUES (?, ?, ?, ?, ?)");
                $stmtDetalle->execute([$id_pedido, $id_plato, $cantidad, $precio_unit, $subtotal]);

                // 4. Recalcular total del pedido
                $stmtTotal = $pdo->prepare("SELECT SUM(subtotal) FROM detalle_pedido WHERE id_pedido = ?");
                $stmtTotal->execute([$id_pedido]);
                $totalActual = floatval($stmtTotal->fetchColumn());

                $stmtActTotal = $pdo->prepare("UPDATE pedido SET total = ? WHERE id = ?");
                $stmtActTotal->execute([$totalActual, $id_pedido]);

                $pdo->commit();

                echo json_encode([
                    "status" => true,
                    "mensaje" => "Plato agregado al pedido",
                    "id_pedido" => $id_pedido,
                    "total" => $totalActual
                ]);
            } catch (Exception $e) {
                if ($pdo->inTransaction()) {
                    $pdo->rollBack();
                }
                echo json_encode(["status" => false, "mensaje" => "Error al agregar: " . $e->getMessage()]);
            }

        } elseif ($accion === 'CERRAR_CUENTA') {
            // HU-09: Cerrar la cuenta de una mesa con transacción
            $id_pedido = intval($data['id_pedido'] ?? 0);

            if ($id_pedido <= 0) {
                echo json_encode(["status" => false, "mensaje" => "ID de pedido inválido"]);
                exit;
            }

            try {
                $pdo->beginTransaction();

                // Obtener mesa del pedido
                $stmtPed = $pdo->prepare("SELECT id_mesa, total, estado FROM pedido WHERE id = ?");
                $stmtPed->execute([$id_pedido]);
                $ped = $stmtPed->fetch();

                if (!$ped || $ped['estado'] === 'CERRADO') {
                    throw new Exception("El pedido ya está cerrado o no existe");
                }

                $id_mesa = $ped['id_mesa'];

                // 1. Marcar pedido como CERRADO
                $stmtCerrar = $pdo->prepare("UPDATE pedido SET estado = 'CERRADO' WHERE id = ?");
                $stmtCerrar->execute([$id_pedido]);

                // 2. Liberar la mesa (estado = 'LIBRE')
                $stmtLiberar = $pdo->prepare("UPDATE mesa SET estado = 'LIBRE' WHERE id = ?");
                $stmtLiberar->execute([$id_mesa]);

                $pdo->commit();

                echo json_encode([
                    "status" => true,
                    "mensaje" => "Cuenta cerrada y mesa liberada con éxito"
                ]);
            } catch (Exception $e) {
                if ($pdo->inTransaction()) {
                    $pdo->rollBack();
                }
                echo json_encode(["status" => false, "mensaje" => "Error al cerrar cuenta: " . $e->getMessage()]);
            }
        }
        break;

    default:
        echo json_encode(["status" => false, "mensaje" => "Método no permitido"]);
        break;
}
?>
