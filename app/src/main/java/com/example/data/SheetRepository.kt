package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url
import java.io.BufferedReader
import java.io.InputStreamReader

interface SheetApiService {
    @GET
    suspend fun downloadCsv(@Url url: String): ResponseBody
}

class SheetRepository {
    private val apiService: SheetApiService

    init {
        val okHttpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://docs.google.com/")
            .client(okHttpClient)
            .build()
        apiService = retrofit.create(SheetApiService::class.java)
    }

    /**
     * Parses a single CSV line, handling quotes properly.
     */
    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val current = java.lang.StringBuilder()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.setLength(0)
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result
    }

    fun parseCSV(csvData: String): List<CuotaCliente> {
        val resultList = mutableListOf<CuotaCliente>()
        val lines = csvData.split("\n")
        if (lines.isEmpty()) return resultList

        // Inspect header to dynamically find columns
        val headerLine = lines[0]
        val headers = parseCsvLine(headerLine).map { it.replace("\"", "").trim().lowercase() }
        
        val idxNombre = headers.indexOfFirst { it == "nombre" || it == "nombre_completo" || it == "subscription_name" || it == "first_name" }
        val idxApellido = headers.indexOfFirst { it == "apellido" || it == "apellidos" || it == "last_name" }
        val idxDNI = headers.indexOfFirst { it == "dni_o_ce" || it == "dni" || it == "subscription_identity_document_number" || it == "documento" }
        val idxCuota = headers.indexOfFirst { it == "cuota" || it == "total_fee" || it == "monto" || it == "deuda" || it == "total_fee" }
        val idxTotalPagado = headers.indexOfFirst { it == "total_pagado" || it == "total_paid" || it == "pagado" || it == "monto_pagado" }
        val idxEstado = headers.indexOfFirst { it == "estado_cuota" || it == "estado_cuota_texto" || it == "estado" || it == "status" }
        val idxPeriodo = headers.indexOfFirst { it == "periodo" || it == "period" || it == "installment" || it == "n_cuota" }
        
        val idxFecha = headers.indexOfFirst { it == "date" || it == "fecha" || it == "fecha_vencimiento" }
        val idxMora = headers.indexOfFirst { it == "mora_accumulated" || it == "mora" || it == "mora_acumulada" }
        val idxDistribuidor = headers.indexOfFirst { it == "distribuidor" || it == "distributor" || it == "tienda" || it == "tienda_vinculada" }
        val idxModelo = headers.indexOfFirst { it == "modelo" || it == "model" || it == "modelo_de_moto" || it == "moto" }
        val idxColorModelo = headers.indexOfFirst { it == "color_modelo" || it == "color" || it == "color_moto" }

        for (rowIdx in 1 until lines.size) {
            val line = lines[rowIdx].trim()
            if (line.isEmpty()) continue
            val cols = parseCsvLine(line)

            try {
                val rowDni = if (idxDNI != -1) cols.getOrNull(idxDNI)?.replace("\"", "")?.trim() ?: "" else ""
                val rawNombre = if (idxNombre != -1) cols.getOrNull(idxNombre)?.replace("\"", "")?.trim() ?: "" else ""
                val rawApellido = if (idxApellido != -1) cols.getOrNull(idxApellido)?.replace("\"", "")?.trim() ?: "" else ""
                val combinedName = if (rawApellido.isNotBlank()) "$rawNombre $rawApellido" else rawNombre
                
                val rawCuota = if (idxCuota != -1) cols.getOrNull(idxCuota)?.replace("\"", "")?.trim() ?: "" else ""
                val rawEstado = if (idxEstado != -1) cols.getOrNull(idxEstado)?.replace("\"", "")?.trim() ?: "" else ""
                val rawFecha = if (idxFecha != -1) cols.getOrNull(idxFecha)?.replace("\"", "")?.trim() ?: "" else ""
                val rawPeriodo = if (idxPeriodo != -1) cols.getOrNull(idxPeriodo)?.replace("\"", "")?.trim() ?: "" else ""
                val rawMora = if (idxMora != -1) cols.getOrNull(idxMora)?.replace("\"", "")?.trim() ?: "" else ""
                val rawDistribuidor = if (idxDistribuidor != -1) cols.getOrNull(idxDistribuidor)?.replace("\"", "")?.trim() ?: "" else ""
                val rawModelo = if (idxModelo != -1) cols.getOrNull(idxModelo)?.replace("\"", "")?.trim() ?: "" else ""
                val rawColorModelo = if (idxColorModelo != -1) cols.getOrNull(idxColorModelo)?.replace("\"", "")?.trim() ?: "" else ""
                val rawTotalPagado = if (idxTotalPagado != -1) cols.getOrNull(idxTotalPagado)?.replace("\"", "")?.trim() ?: "" else ""

                resultList.add(
                    CuotaCliente(
                        nombre = combinedName,
                        dniOrCE = rowDni,
                        cuota = rawCuota,
                        estadoCuotaTexto = rawEstado,
                        fecha = rawFecha,
                        periodo = rawPeriodo,
                        mora = rawMora,
                        distribuidor = rawDistribuidor,
                        modelo = rawModelo,
                        colorModelo = rawColorModelo,
                        totalPagado = rawTotalPagado
                    )
                )
            } catch (rowEx: Exception) {
                Log.e("SheetRepository", "Error parsing row $rowIdx: ${rowEx.message}", rowEx)
            }
        }
        return resultList
    }

    suspend fun obtenerCronogramaPorDNI(dni: String): List<CuotaCliente> = withContext(Dispatchers.IO) {
        val cleanDni = dni.trim()
        if (cleanDni.isEmpty()) return@withContext emptyList<CuotaCliente>()

        val url = "https://docs.google.com/spreadsheets/d/1bPapOV__y17jN5zaBxHzMZw10aby_UUSSvmi-3Wq-CE/gviz/tq?tqx=out:csv&gid=559036149"

        try {
            val responseBody = apiService.downloadCsv(url)
            val csvData = responseBody.string()
            val allRecords = parseCSV(csvData)

            // Robust clean string match
            return@withContext allRecords.filter { it.dniOrCE.trim().equals(cleanDni, ignoreCase = true) }
        } catch (e: Exception) {
            Log.e("SheetRepository", "Error downloading or processing client payment schedule: ${e.message}", e)
            return@withContext emptyList<CuotaCliente>()
        }
    }
}
