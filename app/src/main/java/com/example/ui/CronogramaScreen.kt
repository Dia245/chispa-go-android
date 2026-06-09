package com.example.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CuotaModel
import com.example.ui.theme.BackgroundGrey
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SolidRed
import com.example.ui.theme.SuccessGreen
import java.util.Locale

private fun parseAmount(amountStr: String): Double {
    val clean = amountStr.trim()
        .replace("S/", "")
        .replace("S/.", "")
        .replace("$", "")
        .replace(",", "")
        .trim()
    val digits = clean.replace("[^\\d.]".toRegex(), "")
    return digits.toDoubleOrNull() ?: 0.0
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronogramaScreen(
    cuotas: List<CuotaModel>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val screenBg = if (isDark) Color(0xFF121212) else BackgroundGrey
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textMain = if (isDark) Color.White else Color.Black
    val textSub = if (isDark) Color(0xFFCCCCCC) else Color.DarkGray
    val strokeCol = if (isDark) Color(0xFF2D3748) else Color.LightGray.copy(alpha = 0.5f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "CHISPA",
                                style = androidx.compose.ui.text.TextStyle(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.SansSerif,
                                    letterSpacing = 0.5.sp,
                                    color = if (isDark) Color.White else RoyalBlue
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SolidRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "GO",
                                    style = androidx.compose.ui.text.TextStyle(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.testTag("cronograma_refresh_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar Cronograma",
                                tint = RoyalBlue
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("cronograma_back_btn")) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = RoyalBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = screenBg)
            )
        },
        modifier = modifier.fillMaxSize().background(screenBg)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(screenBg)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Results UI area
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = RoyalBlue)
                            Text(
                                "Descargando cronograma seguro...",
                                fontSize = 13.sp,
                                color = textSub,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = SolidRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = error,
                                fontSize = 14.sp,
                                color = textMain,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                            ) {
                                Text("Reintentar Conexión", color = Color.White)
                            }
                        }
                    }
                }
                cuotas.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No data",
                                tint = RoyalBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No se encontraron registros de cuotas para el documento indicado en el sistema.",
                                fontSize = 14.sp,
                                color = textSub,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    // Precalculate financial metrics dynamically based on active sheet records
                    val totalCuotas = cuotas.size
                    val cuotasPagadas = cuotas.count {
                        val est = (it.estadoTexto ?: "PENDIENTE").trim()
                        est.equals("Pagado", ignoreCase = true) || est.equals("Pagada", ignoreCase = true)
                    }
                    val totalDeudaPendiente = cuotas.filter {
                        val est = (it.estadoTexto ?: "PENDIENTE").trim()
                        !est.equals("Pagado", ignoreCase = true) && !est.equals("Pagada", ignoreCase = true)
                    }.sumOf { it.montoBase }

                    val totalMoraActual = cuotas.sumOf { it.mora }

                    // Render the scrollable list including the premium summary dashboard at the very top
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .testTag("cronograma_list"),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                    ) {
                        // Premium Financial Summary Card (Dashboard de Resumen Superior)
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDark) Color(0xFF1E293B) else RoyalBlue
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("cronograma_summary_card")
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "RESUMEN DE CUOTAS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isDark) Color(0xFF60A5FA) else Color.White.copy(alpha = 0.8f),
                                            letterSpacing = 1.sp
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(alpha = 0.15f))
                                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "SISTEMA ONLINE",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = "DEUDA TOTAL PENDIENTE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "S/ ${String.format(Locale.US, "%,.2f", totalDeudaPendiente)}",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }

                                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "PROGRESO",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "$cuotasPagadas / $totalCuotas cuotas completadas",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "MORA ACTUAL",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "S/ ${String.format(Locale.US, "%,.2f", totalMoraActual)}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (totalMoraActual > 0) Color(0xFFFCA5A5) else Color.White
                                            )
                                        }
                                    }

                                    if (totalCuotas > 0) {
                                        LinearProgressIndicator(
                                            progress = { cuotasPagadas.toFloat() / totalCuotas.toFloat() },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(CircleShape),
                                            color = if (isDark) Color(0xFF3B82F6) else Color.White,
                                            trackColor = Color.White.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }

                        // Compact and beautifully designed cards for each single quota
                        items(cuotas) { item ->
                            CuotaItemDesplegable(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CuotaItemDesplegable(cuota: CuotaModel) {
    var expanded by remember { mutableStateOf(false) }
    
    // Tratamiento seguro contra nulos para los estados de texto
    val estadoLimpio = (cuota.estadoTexto ?: "PENDIENTE").uppercase()
    val isPagada = estadoLimpio.contains("PAGAD")
    val isVencida = estadoLimpio.contains("VENCID") || estadoLimpio.contains("MORA")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isVencida) Color(0xFFFCF5F5) else if (isPagada) Color.White else Color(0xFFF8FAFC)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isVencida) Color(0xFFFCE8E6) else Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize()
            .testTag("cuota_item_${cuota.periodo}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Periodo / Semana: ${cuota.periodo ?: "-"}", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                    Text("Vence: ${cuota.fechaVencimiento ?: "-"} ", color = if (isVencida) Color(0xFFC53030) else Color(0xFF64748B))
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPagada) Color(0xFFE6F4EA) else if (isVencida) Color(0xFFFCE8E6) else Color(0xFFF1F5F9))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isPagada) "PAGADO" else if (isVencida) "¡VENCIDO!" else "PENDIENTE",
                        color = if (isPagada) Color(0xFF137333) else if (isVencida) Color(0xFFC53030) else Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { 
                        Text("Monto Base Semanal:", color = Color(0xFF64748B))
                        Text("S/. ${cuota.montoBase}") 
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { 
                        Text("Recargo Mora:", color = Color(0xFF64748B))
                        Text("S/. ${cuota.mora}", color = if (cuota.mora > 0) Color(0xFFC53030) else Color(0xFF64748B)) 
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { 
                        Text("Total a Pagar:", fontWeight = FontWeight.Bold)
                        Text("S/. ${(cuota.montoBase) + (cuota.mora)}", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A)) 
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { 
                        Text("Monto Real Pagado:", color = Color(0xFF64748B))
                        Text("S/. ${cuota.totalPagado}") 
                    }
                }
            }
        }
    }
}
