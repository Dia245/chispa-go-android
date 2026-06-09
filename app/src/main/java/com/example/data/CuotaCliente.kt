package com.example.data

typealias ClienteCuota = CuotaCliente

data class CuotaCliente(
    val nombre: String,
    val dniOrCE: String,
    val cuota: String,
    val estadoCuotaTexto: String,
    val fecha: String,
    val periodo: String,
    val mora: String,
    val distribuidor: String,
    val modelo: String,
    val colorModelo: String,
    val totalPagado: String
)
