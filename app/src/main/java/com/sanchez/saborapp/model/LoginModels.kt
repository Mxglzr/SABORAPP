package com.sanchez.saborapp.model

data class Usuario(
    val id: Int,
    val usuario: String,
    val rol: String
)

data class LoginRequest(
    val usuario: String,
    val clave: String
)

data class LoginResponse(
    val status: Boolean,
    val mensaje: String,
    val usuario: Usuario?
)
