package com.sanchez.saborapp.network

import com.sanchez.saborapp.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Autenticación (Sprint 1)
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Platos (Sprint 2 & 3)
    @GET("platos.php")
    suspend fun getPlatos(
        @Query("buscar") buscar: String? = null,
        @Query("categoria") categoria: String? = null
    ): Response<ApiResponse<List<Plato>>>

    @POST("platos.php")
    suspend fun registrarPlato(@Body plato: Plato): Response<ApiResponse<Unit>>

    @PUT("platos.php")
    suspend fun actualizarPlato(@Body plato: Plato): Response<ApiResponse<Unit>>

    @DELETE("platos.php")
    suspend fun eliminarPlato(@Query("id") id: Int): Response<ApiResponse<Unit>>

    // Mesas (Sprint 2)
    @GET("mesas.php")
    suspend fun getMesas(): Response<ApiResponse<List<Mesa>>>

    @POST("mesas.php")
    suspend fun registrarMesa(@Body mesa: Mesa): Response<ApiResponse<Unit>>

    @PUT("mesas.php")
    suspend fun actualizarEstadoMesa(@Body mesa: Mesa): Response<ApiResponse<Unit>>

    // Pedidos (Sprint 3)
    @GET("pedidos.php")
    suspend fun getPedidoPorMesa(@Query("id_mesa") idMesa: Int): Response<ApiResponse<Pedido>>

    @GET("pedidos.php")
    suspend fun getPedidoPorId(@Query("id_pedido") idPedido: Int): Response<ApiResponse<Pedido>>

    @POST("pedidos.php")
    suspend fun agregarPlatoPedido(@Body request: AgregarPlatoRequest): Response<ApiResponse<Unit>>

    @POST("pedidos.php")
    suspend fun cerrarCuenta(@Body request: CerrarCuentaRequest): Response<ApiResponse<Unit>>

    // Reportes (Sprint 4)
    @GET("reportes.php")
    suspend fun getReportes(): Response<ApiResponse<ReporteDatos>>
}
