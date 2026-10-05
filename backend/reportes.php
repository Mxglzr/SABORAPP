<?php
require_once 'conexion.php';

// HU-10: Reportes de ventas
try {
    // 1. Venta de hoy y cantidad de pedidos cerrados hoy
    $stmtHoy = $pdo->query("
        SELECT 
            COALESCE(SUM(total), 0) as venta_hoy,
            COUNT(id) as total_pedidos
        FROM pedido 
        WHERE estado = 'CERRADO' 
        AND DATE(fecha) = CURDATE()
    ");
    $resHoy = $stmtHoy->fetch();
    $venta_hoy = floatval($resHoy['venta_hoy']);
    $total_pedidos = intval($resHoy['total_pedidos']);
    $ticket_promedio = ($total_pedidos > 0) ? ($venta_hoy / $total_pedidos) : 0.00;

    // 2. Top platos más pedidos (SUM de cantidad agrupado por plato)
    $stmtTop = $pdo->query("
        SELECT 
            pl.nombre, 
            SUM(d.cantidad) as total_vendido
        FROM detalle_pedido d
        JOIN pedido p ON d.id_pedido = p.id
        JOIN plato pl ON d.id_plato = pl.id
        WHERE p.estado = 'CERRADO'
        GROUP BY d.id_plato, pl.nombre
        ORDER BY total_vendido DESC
        LIMIT 5
    ");
    $top_platos = $stmtTop->fetchAll();

    echo json_encode([
        "status" => true,
        "datos" => [
            "venta_hoy" => $venta_hoy,
            "total_pedidos" => $total_pedidos,
            "ticket_promedio" => round($ticket_promedio, 2),
            "top_platos" => $top_platos
        ]
    ]);
} catch (Exception $e) {
    echo json_encode(["status" => false, "mensaje" => "Error al generar reportes: " . $e->getMessage()]);
}
?>
