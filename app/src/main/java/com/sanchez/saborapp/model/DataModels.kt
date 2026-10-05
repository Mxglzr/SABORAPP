package com.sanchez.saborapp.model

import java.io.Serializable

data class Plato(
    val id: Int = 0,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val disponible: Int = 1
) : Serializable

data class Mesa(
    val id: Int = 0,
    val numero: Int,
    val capacidad: Int,
    val estado: String = "LIBRE"
) : Serializable

data class DetallePedido(
    val id: Int = 0,
    val id_plato: Int,
    val nombre_plato: String,
    val cantidad: Int,
    val precio_unit: Double,
    val subtotal: Double
) : Serializable

data class Pedido(
    val id: Int = 0,
    val id_mesa: Int,
    val numero_mesa: Int? = null,
    val fecha: String,
    val estado: String,
    val total: Double,
    val detalles: List<DetallePedido> = emptyList()
) : Serializable

data class AgregarPlatoRequest(
    val accion: String = "AGREGAR_PLATO",
    val id_mesa: Int,
    val id_plato: Int,
    val cantidad: Int
)

data class CerrarCuentaRequest(
    val accion: String = "CERRAR_CUENTA",
    val id_pedido: Int
)

data class TopPlato(
    val nombre: String,
    val total_vendido: Int
) : Serializable

data class ReporteDatos(
    val venta_hoy: Double,
    val total_pedidos: Int,
    val ticket_promedio: Double,
    val top_platos: List<TopPlato> = emptyList()
) : Serializable

data class ApiResponse<T>(
    val status: Boolean,
    val mensaje: String? = null,
    val datos: T? = null,
    val id: Int? = null
)
