package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

/**
 * ClienteDataService handles network operations to fetch and parse
 * the active client payment schedules (Cronograma) from Google Sheets or Apps Script Web App.
 */
class ClienteDataService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    /**
     * Downloads and parses the JSON matching the given document type and number.
     */
    suspend fun obtenerCuotasPorDNI(
        dni: String,
        tipoDocSeleccionado: String = ""
    ): List<CuotaModel> = withContext(Dispatchers.IO) {
        val cleanDni = dni.trim()
        if (cleanDni.isEmpty()) {
            return@withContext emptyList<CuotaModel>()
        }

        // Clean user input: convert immediately to numeric and back to string to clear any leading zeros
        val numericDoc = cleanDni.filter { it.isDigit() }.toLongOrNull() ?: 0L
        val dniParam = numericDoc.toString()

        // Google Apps Script Web App URL with String Interpolation (without brackets)
        val url = "https://script.google.com/macros/s/AKfycbwSF1BVDffoNIYLq6zhnDfce_oxcdjirtZJssz506MeKJylKL6Uj_mn1496po7xLb_diA/exec?dni=$dniParam"

        Log.d("CHISPA_URL", "URL de la API solicitada: $url")
        Log.d("CHISPA_DEBUG", "Intentando buscar - Doc original: $cleanDni, Doc limpio numérico: $dniParam")

        val request = Request.Builder().url(url).build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.e("CHISPA_DEBUG", "Error al llamar a la API de Google Apps Script: Código ${response.code}")
            throw IOException("Error de respuesta: ${response.code}")
        }

        val body = response.body
        if (body == null) {
            Log.e("CHISPA_DEBUG", "Cuerpo de respuesta vacío de la API")
            throw IOException("Cuerpo de respuesta vacío")
        }

        val bodyString = body.string()
        Log.d("CHISPA_DEBUG", "Respuesta JSON recibida de Google Apps Script: $bodyString")

        val resultList = mutableListOf<CuotaModel>()
        try {
            val trimmedBody = bodyString.trim()
            if (trimmedBody.startsWith("[")) {
                val jsonArray = JSONArray(trimmedBody)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    
                    // Map keys from JSON array element: nombre, dni, cuota, mora, estado, distribuidora, modelo, color, fecha, monto_pagado
                    val nombre = obj.optString("nombre", "")
                    val dniValue = obj.optString("dni", "")
                    
                    val cuotaDouble = if (!obj.isNull("monto_pagar")) obj.optDouble("monto_pagar", 0.0) else (if (obj.isNull("cuota")) 0.0 else obj.optDouble("cuota", 0.0))
                    val moraDouble = if (obj.isNull("mora")) 0.0 else obj.optDouble("mora", 0.0)
                    val montoPagadoDouble = if (obj.isNull("monto_pagado")) 0.0 else obj.optDouble("monto_pagado", 0.0)
                    
                    val estado = obj.optString("estado", "PENDIENTE")
                    val distribuidora = obj.optString("distribuidora", "")
                    val marcaModelo = obj.optString("modelo", "")
                    val color = obj.optString("color", "")
                    
                    // Period and due date mappings
                    val periodo = obj.optString("periodo", (i + 1).toString())
                    val rawFecha = obj.optString("fecha", "")
                    val fechaVencimiento = rawFecha.replace(" 00:00:00 UTC", "").trim()

                    resultList.add(
                        CuotaModel(
                            nombre = nombre,
                            periodo = periodo,
                            fechaVencimiento = fechaVencimiento,
                            estadoTexto = estado,
                            montoBase = cuotaDouble,
                            mora = moraDouble,
                            totalPagado = montoPagadoDouble,
                            modeloMoto = marcaModelo,
                            colorMoto = color,
                            distribuidora = distribuidora,
                            financiamiento = ""
                        )
                    )
                }
            } else if (trimmedBody.startsWith("{")) {
                // Handle if the API returned a wrapped response object
                val rootObj = JSONObject(trimmedBody)
                val success = rootObj.optBoolean("success", true)
                val dataArray = rootObj.optJSONArray("data")
                if (dataArray != null) {
                    for (i in 0 until dataArray.length()) {
                        val obj = dataArray.getJSONObject(i)
                        val nombre = obj.optString("nombre", "")
                        val dniValue = obj.optString("dni", "")
                        val cuotaDouble = if (!obj.isNull("monto_pagar")) obj.optDouble("monto_pagar", 0.0) else (if (obj.isNull("cuota")) 0.0 else obj.optDouble("cuota", 0.0))
                        val moraDouble = if (obj.isNull("mora")) 0.0 else obj.optDouble("mora", 0.0)
                        val montoPagadoDouble = if (obj.isNull("monto_pagado")) 0.0 else obj.optDouble("monto_pagado", 0.0)
                        val estado = obj.optString("estado", "PENDIENTE")
                        val distribuidora = obj.optString("distribuidora", "")
                        val marcaModelo = obj.optString("modelo", "")
                        val color = obj.optString("color", "")
                        val periodo = obj.optString("periodo", (i + 1).toString())
                        val rawFecha = obj.optString("fecha", "")
                        val fechaVencimiento = rawFecha.replace(" 00:00:00 UTC", "").trim()

                        resultList.add(
                            CuotaModel(
                                nombre = nombre,
                                periodo = periodo,
                                fechaVencimiento = fechaVencimiento,
                                estadoTexto = estado,
                                montoBase = cuotaDouble,
                                mora = moraDouble,
                                totalPagado = montoPagadoDouble,
                                modeloMoto = marcaModelo,
                                colorMoto = color,
                                distribuidora = distribuidora,
                                financiamiento = ""
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CHISPA_DEBUG", "Error parsing Apps Script API JSON: ${e.message}", e)
            throw IOException("Error al procesar la respuesta del servidor.")
        }

        Log.d("CHISPA_DEBUG", "Búsqueda finalizada exitosamente. Encontradas: ${resultList.size} cuotas en la API.")
        return@withContext resultList
    }

    /**
     * Endpoint simulado para registrar o enviar los datos del prospecto (Cliente Potencial)
     * cuando decida iniciar su solicitud de financiamiento de motocicleta.
     */
    suspend fun registrarProspecto(
        dni: String,
        nombre: String,
        distrito: String,
        ingresoMensual: Double,
        valorMoto: Double,
        inicial: Double,
        modeloMoto: String
    ): Boolean {
        Log.d("CHISPA_DEBUG", "Registrando prospecto en base de datos de captación de leads:")
        Log.d("CHISPA_DEBUG", " -> DNI/C.E.: $dni")
        Log.d("CHISPA_DEBUG", " -> Nombre Completo: $nombre")
        Log.d("CHISPA_DEBUG", " -> Distrito: $distrito")
        Log.d("CHISPA_DEBUG", " -> Ingreso Mensual: S/. $ingresoMensual")
        Log.d("CHISPA_DEBUG", " -> Modelo de Moto: $modeloMoto - Valor: S/. $valorMoto - Inicial: S/. $inicial")
        return true
    }

    /**
     * Cleans up regional coin formatting commas, quotes and replaces with periods before double parsing.
     */
    private fun parseRegionMoney(rawMoney: String?): Double {
        if (rawMoney == null) return 0.0
        val clean = rawMoney.trim()
            .replace("\"", "")
            .replace("S/.", "")
            .replace("S/", "")
            .replace("$", "")
            .replace(",", ".") // replace comma separators with point for proper double parsing
            .trim()
        val match = "[0-9.]+".toRegex().find(clean)?.value
        return match?.toDoubleOrNull() ?: 0.0
    }
}
