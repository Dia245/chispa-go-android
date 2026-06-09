package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CuotaModel
import com.example.data.ClienteDataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ClienteViewModel : ViewModel() {
    private val service = ClienteDataService()

    val activeClienteCuotas = MutableStateFlow<List<CuotaModel>>(emptyList())
    val isClienteLoading = MutableStateFlow(false)
    val clienteError = MutableStateFlow<String?>(null)

    fun fetchClienteCuotas(dni: String) {
        val cleanDni = dni.trim()
        if (cleanDni.isEmpty()) {
            activeClienteCuotas.value = emptyList()
            clienteError.value = "No se pudo cargar la información"
            return
        }

        viewModelScope.launch {
            activeClienteCuotas.value = emptyList()
            isClienteLoading.value = true
            clienteError.value = null
            try {
                val result = service.obtenerCuotasPorDNI(cleanDni)
                activeClienteCuotas.value = result
                if (result.isEmpty()) {
                    clienteError.value = "El número de documento no registra cuotas pendientes."
                }
            } catch (e: Exception) {
                Log.e("ClienteViewModel", "Error fetching client quotas", e)
                clienteError.value = "Error de conexión con la red de CHISPA GO. Reintente."
            } finally {
                isClienteLoading.value = false
            }
        }
    }
}
