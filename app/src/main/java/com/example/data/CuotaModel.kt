package com.example.data

data class CuotaModel(
    val nombre: String = "",
    val periodo: String = "",
    val fechaVencimiento: String = "",
    val estadoTexto: String = "PENDIENTE",
    val montoBase: Double = 0.0,
    val mora: Double = 0.0,
    val totalPagado: Double = 0.0,
    val modeloMoto: String = "",
    val colorMoto: String = "",
    val distribuidora: String = "",
    val financiamiento: String = ""
)
