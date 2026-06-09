package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuotaEntity
import com.example.data.CuotaModel
import com.example.data.CuotaCliente
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import coil.compose.AsyncImage
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.zIndex
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainClientScreen(
    viewModel: ChispaViewModel,
    clienteViewModel: ClienteViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val quotas by viewModel.currentQuotas.collectAsState()
    val isComodinActive by viewModel.isComodinfrozen.collectAsState()
    val hasUnreadNotification by viewModel.hasUnreadNotification.collectAsState()
    val showNotificationAlert by viewModel.showNotificationAlert.collectAsState()
    val selectedQuotaToPay by viewModel.selectedQuotaToPay.collectAsState()

    val isDark by viewModel.isDarkMode.collectAsState()
    val appBg = if (isDark) Color(0xFF121212) else BackgroundGrey
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textMain = if (isDark) Color.White else Color.Black
    val textSub = if (isDark) Color(0xFFCCCCCC) else Color.DarkGray
    val strokeCol = if (isDark) Color(0xFF2D3748) else Color.LightGray.copy(alpha = 0.5f)

    if (user == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val initials = user?.name?.split(" ")?.filter { it.isNotEmpty() }?.take(2)?.map { it.first().uppercase() }?.joinToString("") ?: "GG"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Official stylized brand logo designed by code
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        }

                        // Theme Quick Toggle, Notifications bell and Logout
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Clear/Dark Quick Toggle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(cardBg)
                                    .border(1.dp, strokeCol, CircleShape)
                                    .clickable { viewModel.toggleDarkMode() }
                                    .testTag("theme_bar_toggle"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isDark) "☀️" else "🌙",
                                    fontSize = 16.sp
                                )
                            }

                            // Notification bell with badge
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(cardBg)
                                    .border(1.dp, strokeCol, CircleShape)
                                    .clickable { viewModel.triggerNotificationClick() }
                                    .testTag("notification_bell"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                if (hasUnreadNotification) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 1.dp, y = (-1).dp)
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SolidRed)
                                              .border(1.5.dp, cardBg, CircleShape)
                                    )
                                }
                            }

                            // Log out button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(cardBg)
                                    .border(1.dp, strokeCol, CircleShape)
                                    .clickable { viewModel.logout() }
                                    .testTag("logout_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Cerrar Sesión",
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBg)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = cardBg,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.activeTab.value = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Inicio", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalBlue,
                        selectedTextColor = RoyalBlue,
                        indicatorColor = RoyalBlue.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_dashboard")
                )
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.activeTab.value = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Guantera") },
                    label = { Text("Guantera", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalBlue,
                        selectedTextColor = RoyalBlue,
                        indicatorColor = RoyalBlue.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_guantera")
                )
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.activeTab.value = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Beneficios") },
                    label = { Text("Beneficios", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalBlue,
                        selectedTextColor = RoyalBlue,
                        indicatorColor = RoyalBlue.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_academia")
                )
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.activeTab.value = 3 },
                    icon = { Icon(Icons.Default.Send, contentDescription = "Canal de Soporte") },
                    label = { Text("Soporte", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalBlue,
                        selectedTextColor = RoyalBlue,
                        indicatorColor = RoyalBlue.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tab_asistente")
                )
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(appBg)
    ) { innerPadding ->
        val showCronograma by viewModel.showCronogramaScreen.collectAsState()
        val cronogramaCuotas by clienteViewModel.activeClienteCuotas.collectAsState()
        val isCronogramaLoading by clienteViewModel.isClienteLoading.collectAsState()
        val cronogramaError by clienteViewModel.clienteError.collectAsState()

        val userDocNum = remember(user) { viewModel.getCurrentUserDocumentNumber() }
        LaunchedEffect(userDocNum) {
            if (userDocNum.isNotEmpty()) {
                clienteViewModel.fetchClienteCuotas(userDocNum)
            }
        }

        if (showCronograma) {
            CronogramaScreen(
                cuotas = cronogramaCuotas,
                isLoading = isCronogramaLoading,
                error = cronogramaError,
                onRefresh = {
                    if (userDocNum.isNotEmpty()) {
                        clienteViewModel.fetchClienteCuotas(userDocNum)
                    }
                },
                onBack = { viewModel.showCronogramaScreen.value = false },
                isDark = isDark,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = appBg
            ) {
                when (activeTab) {
                    0 -> DashboardTab(viewModel, clienteViewModel)
                    1 -> GuanteraTab(viewModel)
                    2 -> MotoAcademiaTab(viewModel)
                    3 -> AsistenteTab(viewModel)
                }
            }
        }

        // Notification Alerts Modal Dialog (Custom Translucent & Styled Dialog)
        if (showNotificationAlert) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { viewModel.showNotificationAlert.value = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SolidRed.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = SolidRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                "Notificaciones Chispa Go",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = textMain
                            )
                        }

                        val calendar = java.util.Calendar.getInstance()
                        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                        val timeGreeting = when (currentHour) {
                            in 6..11 -> "Buenos días"
                            in 12..18 -> "Buenas tardes"
                            else -> "Buenas noches"
                        }
                        val firstCronogramaItem = cronogramaCuotas.firstOrNull()
                        val rawName = if (firstCronogramaItem != null && firstCronogramaItem.nombre.isNotBlank()) {
                            firstCronogramaItem.nombre
                        } else {
                            user?.name ?: "Conductor"
                        }
                        val cleanFirstName = rawName.trim().split("\\s+".toRegex()).firstOrNull()?.trim()?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Conductor"
                        val greetingText = "$timeGreeting, $cleanFirstName"
                        Text(
                            text = "$greetingText Revisa tus alertas de financiamiento:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = textSub
                        )

                        HorizontalDivider(color = strokeCol)

                        // Condition-based custom alerts
                        if (user?.code == "GG-911") {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SolidRed.copy(alpha = 0.1f))
                                    .border(1.dp, SolidRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "⚠️ ALERTA DE MORA: Tienes 2 cuotas semanales vencidas acumuladas. Tu vehículo está programado para congelamiento preventivo si no regularizas la deuda.",
                                    color = if (isDark) Color(0xFFFFB7B7) else SolidRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SuccessGreen.copy(alpha = 0.1f))
                                    .border(1.dp, SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "ESTADO DE CUENTA AL DÍA: Su historial de pagos registra ${user?.recordWeeks} semanas canceladas de forma oportuna. Siga así para conservar sus beneficios institucionales habilitados.",
                                    color = if (isDark) Color(0xFFACF7C5) else SuccessGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        if (isComodinActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RoyalBlue.copy(alpha = 0.1f))
                                    .border(1.dp, RoyalBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "🔒 COMODÍN EMERGENCIAS ACTIVO: Tu estado se encuentra congelado temporalmente. No se calcularán cargos de mora atrasados durante esta semana.",
                                    color = RoyalBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.dismissNotificationAlert() },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("notification_confirm")
                        ) {
                            Text("Entendido S/. 0", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // level up dialog removed for compliance

        // Bottom Sheet Payment Modal
        if (selectedQuotaToPay != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectedQuotaToPay.value = null },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                containerColor = Color.White
            ) {
                val q = selectedQuotaToPay!!
                val containsMora = q.status == "VENCIDA_MORA"
                val isCarlos = user?.code == "GG-911"

                // Dynamic payment option selection state
                var paymentOption by remember { mutableStateOf("B") }

                val baseAmount = q.amount
                val extraCharges = if (containsMora) 10.0 else 0.0

                // Option and totals dynamic calculation
                val displayBase = if (isCarlos) 100.0 else baseAmount
                val displayMora = if (isCarlos) {
                    if (paymentOption == "A") 5.0 else 10.0
                } else {
                    extraCharges
                }
                val displayTotal = displayBase + displayMora

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Realizar Pago de Cuota",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    )
                    Text(
                        text = q.dueDate,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextGrey)
                    )

                    // Interactive Selectors for Carlos GG-911
                    if (isCarlos) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Seleccione opción de pago:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundGrey, RoundedCornerShape(16.dp))
                                .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Row Option A
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (paymentOption == "A") Color.White else Color.Transparent)
                                    .border(
                                        1.5.dp,
                                        if (paymentOption == "A") (if (isCarlos) SolidRed else RoyalBlue) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { paymentOption = "A" }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentOption == "A",
                                    onClick = { paymentOption = "A" },
                                    colors = RadioButtonDefaults.colors(selectedColor = if (isCarlos) SolidRed else RoyalBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Opción A: Pagar solo 1 cuota vencida (S/. 105.00)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBlue
                                    )
                                    Text(
                                        text = "[Cuota base + mora individual]",
                                        fontSize = 11.sp,
                                        color = TextGrey
                                    )
                                }
                            }

                            // Row Option B
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (paymentOption == "B") Color.White else Color.Transparent)
                                    .border(
                                        1.5.dp,
                                        if (paymentOption == "B") (if (isCarlos) SolidRed else RoyalBlue) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { paymentOption = "B" }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentOption == "B",
                                    onClick = { paymentOption = "B" },
                                    colors = RadioButtonDefaults.colors(selectedColor = if (isCarlos) SolidRed else RoyalBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Opción B: Pagar TODO el saldo pendiente (S/. 110.00)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBlue
                                    )
                                    Text(
                                        text = "[Ambas cuotas + moras completas]",
                                        fontSize = 11.sp,
                                        color = TextGrey
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = BackgroundGrey),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Monto Base:", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                                Text("S/. ${"%.2f".format(displayBase)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                            if (containsMora) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isCarlos && paymentOption == "A") "Recargo Mora Individual:" else "Recargo Mora Acumulada:", 
                                        style = MaterialTheme.typography.bodyMedium, 
                                        color = TextDarkRed
                                    )
                                    Text("S/. ${"%.2f".format(displayMora)}", style = MaterialTheme.typography.bodyMedium, color = TextDarkRed, fontWeight = FontWeight.Bold)
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total a Transferir:", style = MaterialTheme.typography.titleMedium, color = RoyalBlue, fontWeight = FontWeight.Bold)
                                Text("S/. ${"%.2f".format(displayTotal)}", style = MaterialTheme.typography.titleMedium, color = RoyalBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Seleccione su billetera o canal integrado de pago en Perú:",
                        style = MaterialTheme.typography.labelMedium.copy(color = TextGrey),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 native Peru payment methods
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Yape Button
                        Button(
                            onClick = { viewModel.processPaymentSimulation("Yape", paymentOption) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C6FF)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_via_yape")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Yape", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pagar S/. ${"%.2f".format(displayTotal)} con Yape instantáneo", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Plin Button
                        Button(
                            onClick = { viewModel.processPaymentSimulation("Plin", paymentOption) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_via_plin")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Plin", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pagar S/. ${"%.2f".format(displayTotal)} con Plin directo", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        // PagoEfectivo Button
                        Button(
                            onClick = { viewModel.processPaymentSimulation("PagoEfectivo", paymentOption) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_via_pagoefectivo")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "PagoEfectivo", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Pagar S/. ${"%.2f".format(displayTotal)} por PagoEfectivo (CIP)", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// TAB 1: FINANCIAL DASHBOARD
// ============================================
@Composable
fun DashboardTab(viewModel: ChispaViewModel, clienteViewModel: ClienteViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val quotas by viewModel.currentQuotas.collectAsState()
    val cronogramaCuotas by clienteViewModel.activeClienteCuotas.collectAsState()

    if (user == null) return

    var isQuotasCalendarExpanded by remember { mutableStateOf(false) }
    var isProfileExpanded by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val isDark by viewModel.isDarkMode.collectAsState()
    val appBg = if (isDark) Color(0xFF121212) else BackgroundGrey

    // Culqi Yape state trackers
    var yapeSelectedCuota by remember { mutableStateOf<CuotaCliente?>(null) }
    var yapeCelular by remember { mutableStateOf("") }
    var yapeCodigo by remember { mutableStateOf("") }
    val isProcessingYape by viewModel.isProcessingYapePayment.collectAsState()
    val yapeSuccess by viewModel.yapePaymentSuccessMessage.collectAsState()
    val yapeError by viewModel.yapePaymentErrorMessage.collectAsState()

    // Floating success celebration banner (animación flotante)
    var showAnimationBanner by remember { mutableStateOf(false) }

    LaunchedEffect(yapeSuccess) {
        if (yapeSuccess != null) {
            showAnimationBanner = true
            kotlinx.coroutines.delay(4000)
            showAnimationBanner = false
            viewModel.yapePaymentSuccessMessage.value = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textMain = if (isDark) Color.White else Color.Black
    val textSub = if (isDark) Color(0xFFCCCCCC) else Color.DarkGray
    val strokeCol = if (isDark) Color(0xFF2D3748) else Color.LightGray.copy(alpha = 0.5f)

    // Helper to extract amounts robustly
    val parseAmount: (String) -> Double = { amountStr ->
        val clean = amountStr.trim()
            .replace("S/", "")
            .replace("S/.", "")
            .replace("$", "")
            .replace(",", "")
            .trim()
        val digits = clean.replace("[^\\d.]".toRegex(), "")
        digits.toDoubleOrNull() ?: 0.0
    }

    // Financial calculations
    val countPaid = quotas.count { it.status == "PAGADA" }
    val countPending = quotas.count { it.status == "PENDIENTE_SEMANAL" }
    val overdueQuotas = quotas.filter { it.status == "VENCIDA_MORA" }
    val countOverdue = overdueQuotas.size

    val baseUnpaid = countOverdue * 100.0 + countPending * 100.0
    val totalMoraPenalty = countOverdue * 10.0
    
    val totalPaidAmount = if (cronogramaCuotas.isNotEmpty()) {
        cronogramaCuotas.sumOf { it.totalPagado }
    } else {
        countPaid * 100.0
    }

    val totalPendingAmount = if (cronogramaCuotas.isNotEmpty()) {
        val totalCuotasBase = cronogramaCuotas.sumOf { it.montoBase }
        val totalPagadoSum = cronogramaCuotas.sumOf { it.totalPagado }
        (totalCuotasBase - totalPagadoSum).coerceAtLeast(0.0)
    } else {
        baseUnpaid + totalMoraPenalty
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1.0 TOP LEVEL: HIGH-VISIBILITY SUMMARY CARD (SIMPLIFIED DEUTA TOTAL ONLY WITH TRAFFIC LIGHT & NEXT QUOTA)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalBlue), // Fondo azul premium
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(2.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val statusColor = if (cronogramaCuotas.isNotEmpty()) {
                        val hasOverdue = cronogramaCuotas.any {
                            val st = (it.estadoTexto ?: "").trim().lowercase()
                            st.contains("vencid") || st.contains("mora") || it.mora > 0.0
                        }
                        val hasPending = cronogramaCuotas.any {
                            val st = (it.estadoTexto ?: "").trim().lowercase()
                            !st.contains("pagad")
                        }
                        when {
                            hasOverdue -> SolidRed
                            hasPending -> Color(0xFFC0C0C0) // Plata (Al día)
                            else -> SuccessGreen // Verde
                        }
                    } else {
                        when {
                            countOverdue > 0 -> SolidRed
                            countPending > 0 -> Color(0xFFC0C0C0)
                            else -> SuccessGreen
                        }
                    }

                    val statusLabel = if (cronogramaCuotas.isNotEmpty()) {
                        val hasOverdue = cronogramaCuotas.any {
                            val st = (it.estadoTexto ?: "").trim().lowercase()
                            st.contains("vencid") || st.contains("mora") || it.mora > 0.0
                        }
                        val hasPending = cronogramaCuotas.any {
                            val st = (it.estadoTexto ?: "").trim().lowercase()
                            !st.contains("pagad")
                        }
                        when {
                            hasOverdue -> "Vencida"
                            hasPending -> "Al día"
                            else -> "Adelantada"
                        }
                    } else {
                        when {
                            countOverdue > 0 -> "Vencida"
                            countPending > 0 -> "Al día"
                            else -> "Adelantada"
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HAS PAGADO HASTA EL MOMENTO",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        
                        // Small Payment Traffic Light (Semáforo de Pago)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(statusColor.copy(alpha = 0.25f))
                                .border(1.5.dp, statusColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Text(
                                text = statusLabel,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Highlighted pure numeric debt amount (monto blanco gigante)
                    Text(
                        text = "S/. ${"%,.2f".format(totalPaidAmount)}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                    )

                    // Franja inferior de vencimiento info card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Saldo pendiente por pagar: S/. ${"%,.2f".format(totalPendingAmount)}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2.0 SALUDO DINÁMICO POR HORA
        item {
            val calendar = java.util.Calendar.getInstance()
            val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val timeGreeting = when (currentHour) {
                in 6..11 -> "Buenos días"
                in 12..18 -> "Buenas tardes"
                else -> "Buenas noches"
            }

            val activeCronograma by viewModel.activeCronograma.collectAsState()
            val displayCuotas = if (activeCronograma.isNotEmpty()) {
                activeCronograma
            } else {
                cronogramaCuotas.map {
                    CuotaCliente(
                        nombre = it.nombre,
                        dniOrCE = user?.code ?: "",
                        cuota = "S/. ${it.montoBase}",
                        estadoCuotaTexto = it.estadoTexto,
                        fecha = it.fechaVencimiento,
                        periodo = it.periodo,
                        mora = "S/. ${it.mora}",
                        distribuidor = it.distribuidora,
                        modelo = it.modeloMoto,
                        colorModelo = it.colorMoto,
                        totalPagado = "S/. ${it.totalPagado}"
                    )
                }
            }

            val rawClientName = displayCuotas.firstOrNull()?.nombre?.trim() ?: user?.name ?: "Conductor"
            val cleanClientName = rawClientName.trim().split("\\s+".toRegex()).firstOrNull()?.trim()?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Conductor"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$timeGreeting, $cleanClientName",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMain,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Bienvenido a tu panel de control de Chispa Go",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSub
                )
            }
        }

        // 3.0 TARJETA PRINCIPAL DE CUOTAS (DESPLEGABLE / ACCORDION)
        item {
            val activeCronograma by viewModel.activeCronograma.collectAsState()
            val displayCuotas = if (activeCronograma.isNotEmpty()) {
                activeCronograma
            } else {
                cronogramaCuotas.map {
                    CuotaCliente(
                        nombre = it.nombre,
                        dniOrCE = user?.code ?: "",
                        cuota = "S/. ${it.montoBase}",
                        estadoCuotaTexto = it.estadoTexto,
                        fecha = it.fechaVencimiento,
                        periodo = it.periodo,
                        mora = "S/. ${it.mora}",
                        distribuidor = it.distribuidora,
                        modelo = it.modeloMoto,
                        colorModelo = it.colorMoto,
                        totalPagado = "S/. ${it.totalPagado}"
                    )
                }
            }

            var isQuotasAccordionExpanded by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Area (Clickable to Toggle)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isQuotasAccordionExpanded = !isQuotasAccordionExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Mis Cuotas",
                                    tint = RoyalBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Mis Cuotas",
                                    color = textMain,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Total de cuotas: ${displayCuotas.size}",
                                    color = textSub,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isQuotasAccordionExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse",
                            tint = RoyalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    AnimatedVisibility(visible = isQuotasAccordionExpanded) {
                        var selectedFilter by remember { mutableStateOf("Todas") }

                        val filteredCuotas = remember(displayCuotas, selectedFilter) {
                            when (selectedFilter) {
                                "Pagadas" -> displayCuotas.filter {
                                    val statusStr = it.estadoCuotaTexto.trim().uppercase()
                                    statusStr == "PAGADO" || statusStr == "PAGADA" || statusStr == "AL DIA" || statusStr == "AL DÍA"
                                }
                                "Pendientes/Vencidas" -> displayCuotas.filter {
                                    val statusStr = it.estadoCuotaTexto.trim().uppercase()
                                    statusStr != "PAGADO" && statusStr != "PAGADA" && statusStr != "AL DIA" && statusStr != "AL DÍA"
                                }
                                else -> displayCuotas
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HorizontalDivider(color = strokeCol.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                            // Interactive Filter Chips Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val filters = listOf("Todas", "Pagadas", "Pendientes/Vencidas")
                                filters.forEach { filter ->
                                    val isSelected = selectedFilter == filter
                                    val bg = if (isSelected) RoyalBlue else (if (isDark) Color(0xFF1E293B) else BackgroundGrey)
                                    val tc = if (isSelected) Color.White else textMain
                                    val borderCol = if (isSelected) RoyalBlue else strokeCol
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(bg)
                                            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
                                            .clickable { selectedFilter = filter }
                                            .padding(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = filter,
                                            color = tc,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(color = strokeCol.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 2.dp))

                            if (filteredCuotas.isEmpty()) {
                                Text(
                                    text = "No se encontraron cuotas para este filtro.",
                                    fontSize = 12.sp,
                                    color = textSub,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            } else {
                                filteredCuotas.forEachIndexed { index, item ->
                                    val status = item.estadoCuotaTexto.trim().uppercase()
                                    val isPaid = status == "PAGADO" || status == "PAGADA" || status == "AL DIA"
                                    val isOverdue = status == "PENDIENTE" || status == "EN MORA" || status == "VENCIDA" || status == "VENCIDA_MORA"

                                    val tagBgColor = when {
                                        isPaid -> SoftSuccessGreen
                                        status == "PENDIENTE" -> Color(0xFFFFF7ED) // Orange tint
                                        else -> Color(0xFFFEF2F2) // Red tint
                                    }

                                    val tagTextColor = when {
                                        isPaid -> TextDarkGreen
                                        status == "PENDIENTE" -> Color(0xFFC2410C) // Orange text
                                        else -> SolidRed // Red text
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, strokeCol),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Cuota Semanal #${item.periodo.ifBlank { (index + 1).toString() }}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = textMain
                                                )

                                                // Status Tag Indicator with Conditional Color formatting
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(tagBgColor)
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = item.estadoCuotaTexto,
                                                        color = tagTextColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                            }

                                            HorizontalDivider(color = strokeCol.copy(alpha = 0.3f))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Vencimiento:", fontSize = 11.sp, color = textSub)
                                                    Text(item.fecha, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMain)
                                                }
                                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                                    Text("Cuota Base:", fontSize = 11.sp, color = textSub)
                                                    Text(item.cuota, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textMain)
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("Monto Pagado:", fontSize = 11.sp, color = textSub)
                                                    Text(item.totalPagado, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isPaid) TextDarkGreen else textMain)
                                                }
                                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                                    Text("Mora Acumulada:", fontSize = 11.sp, color = textSub)
                                                    Text(
                                                        text = item.mora,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isOverdue && item.mora != "S/. 0.0" && item.mora != "S/. 0.00") SolidRed else textMain
                                                    )
                                                }
                                            }

                                            if (!isPaid) {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Button(
                                                    onClick = {
                                                        yapeSelectedCuota = item
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF742D8A)),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(40.dp)
                                                        .testTag("yape_pay_button_${item.periodo}")
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                     ) {
                                                        Text("⚡", color = Color.White, fontSize = 14.sp)
                                                        Text(
                                                            text = "Pagar con Yape",
                                                            color = Color.White,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4.0 TARJETA DE INFORMACIÓN DE LA MOTO (INFERIOR)
        item {
            val activeCronograma by viewModel.activeCronograma.collectAsState()
            val displayCuotas = if (activeCronograma.isNotEmpty()) {
                activeCronograma
            } else {
                cronogramaCuotas.map {
                    CuotaCliente(
                        nombre = it.nombre,
                        dniOrCE = user?.code ?: "",
                        cuota = "S/. ${it.montoBase}",
                        estadoCuotaTexto = it.estadoTexto,
                        fecha = it.fechaVencimiento,
                        periodo = it.periodo,
                        mora = "S/. ${it.mora}",
                        distribuidor = it.distribuidora,
                        modelo = it.modeloMoto,
                        colorModelo = it.colorMoto,
                        totalPagado = "S/. ${it.totalPagado}"
                    )
                }
            }

            val firstQuota = displayCuotas.firstOrNull()
            val distribuidorName = firstQuota?.distribuidor?.ifBlank { null } ?: "Distribuidor Oficial Global Go"
            val modeloName = firstQuota?.modelo?.ifBlank { null } ?: "Modelo Adjudicado"
            val colorName = firstQuota?.colorModelo?.ifBlank { null } ?: "Color por definir"

            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏍️", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Mi Motocicleta Financiada",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMain
                            )
                            Text(
                                text = "Crédito vehicular activo Global Go",
                                fontSize = 11.sp,
                                color = textSub
                            )
                        }
                    }

                    HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Distribuidor Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Distribuidor:",
                                fontSize = 12.sp,
                                color = textSub,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = distribuidorName,
                                fontSize = 12.sp,
                                color = textMain,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(2.5f)
                            )
                        }

                        // Modelo Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Modelo:",
                                fontSize = 12.sp,
                                color = textSub,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = modeloName,
                                fontSize = 12.sp,
                                color = textMain,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(2.5f)
                            )
                        }

                        // Color Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Color:",
                                fontSize = 12.sp,
                                color = textSub,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.weight(1.2f)
                            )
                            Text(
                                text = colorName,
                                fontSize = 12.sp,
                                color = textMain,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.weight(2.5f)
                            )
                        }
                    }
                }
            }
        }

    }

        // Yape Selected Cuota Modal/Dialog
        if (yapeSelectedCuota != null) {
            val cuotaItem = yapeSelectedCuota!!
            val parsedBase = parseAmount(cuotaItem.cuota)
            val parsedPagado = parseAmount(cuotaItem.totalPagado)
            val montoNeto = (parsedBase - parsedPagado).coerceAtLeast(0.0)

            androidx.compose.ui.window.Dialog(
                onDismissRequest = {
                    if (!isProcessingYape) {
                        yapeSelectedCuota = null
                        yapeCelular = ""
                        yapeCodigo = ""
                        viewModel.yapePaymentSuccessMessage.value = null
                        viewModel.yapePaymentErrorMessage.value = null
                    }
                }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, strokeCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("yape_payment_modal")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header with Yape Icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF742D8A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Culqi Yape Checkout",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textMain
                        )

                        HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))

                        // Neto visual feedback: "Monto a Pagar: S/ $montoNeto"
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF742D8A).copy(alpha = 0.1f))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Monto a Pagar: S/ ${"%,.2f".format(montoNeto)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF742D8A)
                            )
                            Text(
                                text = "Cuota semanal #${cuotaItem.periodo}",
                                fontSize = 11.sp,
                                color = textSub,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Field 1: Número de celular Yape (Input numérico de 9 dígitos)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Número de celular Yape",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMain
                            )
                            OutlinedTextField(
                                value = yapeCelular,
                                onValueChange = { newVal ->
                                    if (newVal.all { it.isDigit() } && newVal.length <= 9) {
                                        yapeCelular = newVal
                                    }
                                },
                                placeholder = { Text("Ej: 987654321") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF742D8A),
                                    cursorColor = Color(0xFF742D8A)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("yape_celular_input")
                            )
                            if (yapeCelular.isNotEmpty() && yapeCelular.length != 9) {
                                Text(
                                    text = "⚠️ Debe contener exactamente 9 dígitos.",
                                    color = SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Field 2: Código de aprobación Yape (Input numérico de 6 dígitos)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Código de aprobación Yape",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textMain
                            )
                            OutlinedTextField(
                                value = yapeCodigo,
                                onValueChange = { newVal ->
                                    if (newVal.all { it.isDigit() } && newVal.length <= 6) {
                                        yapeCodigo = newVal
                                    }
                                },
                                placeholder = { Text("6 dígitos de aprobación") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF742D8A),
                                    cursorColor = Color(0xFF742D8A)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("yape_codigo_input")
                            )
                            Text(
                                text = "Genera este código de 6 dígitos dentro del menú 'Código de Yape' en tu aplicación móvil Yape",
                                fontSize = 10.sp,
                                color = textSub,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                            if (yapeCodigo.isNotEmpty() && yapeCodigo.length != 6) {
                                Text(
                                    text = "⚠️ Debe contener exactamente 6 dígitos.",
                                    color = SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Error message
                        if (yapeError != null) {
                            Text(
                                text = yapeError!!,
                                color = SolidRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        // Bottom Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    yapeSelectedCuota = null
                                    yapeCelular = ""
                                    yapeCodigo = ""
                                    viewModel.yapePaymentSuccessMessage.value = null
                                    viewModel.yapePaymentErrorMessage.value = null
                                },
                                enabled = !isProcessingYape,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Cancelar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (yapeCelular.length == 9 && yapeCodigo.length == 6) {
                                        viewModel.procesarPagoCulqiYape(
                                            dni = user?.code ?: "",
                                            periodo = cuotaItem.periodo,
                                            montoNeto = montoNeto,
                                            celularYape = yapeCelular,
                                            codigoAprobacion = yapeCodigo,
                                            onSuccess = {
                                                // Instant local update for activeClienteCuotas of ClienteViewModel
                                                val updatedList = cronogramaCuotas.map {
                                                    if (it.periodo == cuotaItem.periodo) {
                                                        it.copy(
                                                            estadoTexto = "PAGADO",
                                                            totalPagado = it.montoBase
                                                        )
                                                    } else {
                                                        it
                                                    }
                                                }
                                                clienteViewModel.activeClienteCuotas.value = updatedList

                                                // Update local state in ChispaViewModel's activeCronograma
                                                val updatedListCron = viewModel.activeCronograma.value.map {
                                                    if (it.periodo == cuotaItem.periodo) {
                                                        it.copy(
                                                            estadoCuotaTexto = "PAGADO",
                                                            totalPagado = it.cuota
                                                        )
                                                    } else {
                                                        it
                                                    }
                                                }
                                                viewModel.activeCronograma.value = updatedListCron

                                                // Close dialog
                                                yapeSelectedCuota = null
                                                yapeCelular = ""
                                                yapeCodigo = ""
                                            }
                                        )
                                    }
                                },
                                enabled = !isProcessingYape && yapeCelular.length == 9 && yapeCodigo.length == 6,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF742D8A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(48.dp)
                                    .testTag("yape_confirm_button")
                            ) {
                                if (isProcessingYape) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Confirmar y Yapear", fontSize = 12.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated overlay toast / banner ("¡Pago Procesado Exitosamente con Yape!")
        AnimatedVisibility(
            visible = showAnimationBanner,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(99f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("yape_success_floating_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎉", fontSize = 16.sp)
                        }
                        Text(
                            text = "¡Pago Procesado Exitosamente con Yape!",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// OBSOLETE/TEMPORARY COMPATIBILITY HOLDER
// ============================================
@Composable
fun ObsoleteDashboardTab(viewModel: ChispaViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val quotas by viewModel.currentQuotas.collectAsState()
    val isFrozen by viewModel.isComodinfrozen.collectAsState()

    if (user == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Personalized Greeting Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, RoyalBlue.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val rawName = user?.name ?: "Estimado Cliente"
                    val firstName = rawName.split(" ").firstOrNull()?.trim()?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Cliente"
                    
                    val currentHour = remember {
                        try {
                            java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        } catch (e: Exception) {
                            12
                        }
                    }
                    val greetingPrefix = when {
                        currentHour in 6..11 -> "¡Buen día"
                        currentHour in 12..18 -> "¡Buenas tardes"
                        else -> "¡Buenas noches"
                    }

                    Text(
                        text = "$greetingPrefix, $firstName! Bienvenido de nuevo",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Text(
                        text = "Plataforma de Control Financiero • Global Go Perú",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RoyalBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Inline warning panel
                    val userCode = user?.code ?: ""
                    val documentNum = userCode.removePrefix("GG-DNI-").removePrefix("GG-CE-").removePrefix("GG-").removePrefix("DNI-").removePrefix("CE-")
                    val isRiskDni = documentNum.startsWith("52") || documentNum.startsWith("78") || documentNum.startsWith("104") || userCode.contains("-52") || userCode.contains("-78") || userCode.contains("-104")
                    if (isRiskDni) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SolidRed.copy(alpha = 0.1f))
                                .border(1.dp, SolidRed, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Risk Warning",
                                    tint = SolidRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Documento requiere revisión adicional por observaciones pendientes",
                                    color = SolidRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        // DEDICATED DATA BLOCK FOR REGISTRATION TRACKING (Tienda Vinculada, Modelo Derivado, Detalle Financiero)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, RoyalBlue.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "FICHA DE SEGUIMIENTO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SolidRed,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Evaluación Activa",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        }
                    }

                    // Client Legal Name (MUST be permanently visible at the top part of the dashboard layout)
                    Column {
                        Text(
                            text = "Nombre Legal del Cliente:",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextGrey)
                        )
                        Text(
                            text = (user?.name ?: "CLIENTE REGISTRADO").uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Divider(color = Color.LightGray.copy(alpha = 0.4f))

                    // 1. Tienda Vinculada
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = "TIENDA", tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Tienda Vinculada",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        }
                        Text(
                            text = "Global Go Perú - Concesionario Principal Lima Norte",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Dirección: Av. Alfredo Mendiola 3929, Los Olivos - San Martín de Porres",
                            color = TextGrey,
                            fontSize = 11.sp
                        )
                    }

                    // 2. Modelo Derivado
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Build, contentDescription = "MODELO", tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Modelo Derivado",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        }
                        Text(
                            text = "Yamaha FZ-S FI V3 (Inyección Electrónica)",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Especificaciones: Motor de 149cc, Sistema de Frenos ABS, Color Azul Metálico, Placa: 4321-MX",
                            color = TextGrey,
                            fontSize = 11.sp
                        )
                    }

                    // 3. Detalle Financiero
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "FINANCIERO", tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Detalle Financiero",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Cuota Inicial", fontSize = 10.sp, color = TextGrey)
                                Text("S/. 1,500.00", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Column {
                                Text("Monto Financiado", fontSize = 10.sp, color = TextGrey)
                                Text("S/. 4,500.00", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Cuota Semanal", fontSize = 10.sp, color = TextGrey)
                                Text("S/. 100.00", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = SolidRed)
                            }
                        }
                    }
                }
            }
        }

        // COMPOSTABLE TRANSPARENT DEBT CARD (José GG-777 vs Carlos GG-911)
        item {
            val pendingQuotas = quotas.filter { it.status != "PAGADA" }
            val overdueQuotas = quotas.filter { it.status == "VENCIDA_MORA" }

            if (overdueQuotas.isNotEmpty()) {
                // OVERDUE STATE: RED COLOR WRAPPER MATCHING ELEGANT DESIGN HTML
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftWarningRed),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(2.dp, Color(0xFFD32F2F)), // Solid Rojo Alerta border
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        val countVencidas = overdueQuotas.size
                        val totalMora = countVencidas * 10.0
                        val totalToPay = (countVencidas * 100.0) + totalMora

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "ESTADO: MODO MORA",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F), // Solid high-contrast Alert Red
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "S/. ${"%.2f".format(totalToPay)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = RoyalBlue // Large bold #0B3C5D text
                                    )
                                )
                            }

                            // Customer profile is clear and clean
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "CLIENTE ACTIVO",
                                    color = RoyalBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom breakdowns
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                    Text(
                                        "Cuota Semanal",
                                        color = RoyalBlue.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "S/. ${"%.2f".format(countVencidas * 100.0)}",
                                        color = RoyalBlue,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Mora (${countVencidas * 2} días de retraso)",
                                    color = Color(0xFFD32F2F), // Solid high-contrast Alert Red
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "+ S/. ${"%.2f".format(totalMora)}",
                                    color = Color(0xFFD32F2F), // Solid high-contrast Alert Red
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.selectQuotaForPayment(overdueQuotas.first()) },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed), // Solid Red
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Pagar Cuota con Recargo", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // ACTIVE STATE (AL DÍA) - ELEGANT GREEN ADAPTATION
                val activeQuota = pendingQuotas.find { it.status == "PENDIENTE_SEMANAL" }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftSuccessGreen),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFC8E6C9)), // Soft green border
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    "ESTADO: AL DÍA ✅",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkGreen,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (activeQuota != null) "S/. 100.00" else "S/. 0.00",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = RoyalBlue
                                    )
                                )
                            }

                            // Semi-transparent pill representing client code (e.g. GG-777)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "CLIENTE REGISTRADO",
                                    color = RoyalBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (activeQuota != null) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Concepto",
                                        color = RoyalBlue.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        "Cuota Semanal Ordinaria",
                                        color = RoyalBlue,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Vence el:",
                                        color = TextGrey,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        activeQuota.dueDate,
                                        color = RoyalBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.selectQuotaForPayment(activeQuota) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("pay_quota_button")
                            ) {
                                Text("Pagar Cuota Semanal", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = "Se encuentra completamente al día con sus pagos de financiamiento. ¡Su historial crediticio y su vehículo están protegidos al 100%!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RoyalBlue,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // QUOTA HISTORY WIDGET BREAKDOWN (CALENDARIO DE CUOTAS)
        item {
            var expandedQuotaId by remember { mutableStateOf<String?>(null) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    val countPaid = quotas.count { it.status == "PAGADA" }
                    val countPending = quotas.count { it.status == "PENDIENTE_SEMANAL" }
                    val countOverdue = quotas.count { it.status == "VENCIDA_MORA" }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CALENDARIO DE CUOTAS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8), // Slate gray label
                                letterSpacing = 1.sp
                            )
                        )

                        Text(
                            text = "Pagadas: $countPaid | Restantes: ${countPending + countOverdue}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        quotas.forEachIndexed { idx, quota ->
                            val isVencida = quota.status == "VENCIDA_MORA"
                            val isPagada = quota.status == "PAGADA"

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        when {
                                            isVencida -> Color(0xFFFCF5F5) // Soft pinkish-red background for alert items
                                            isPagada -> Color.White
                                            else -> Color(0xFFF8FAFC) // Soft grey slate-50 background of normal list items
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isVencida -> Color(0xFFFCE8E6)
                                            else -> Color(0xFFF1F5F9)
                                        },
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        expandedQuotaId = if (expandedQuotaId == quota.quotaId) null else quota.quotaId
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isPagada -> SoftSuccessGreen
                                                        isVencida -> SoftWarningRed
                                                        else -> Color(0xFFE2E8F0)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when {
                                                    isPagada -> Icons.Default.Check
                                                    isVencida -> Icons.Default.Warning
                                                    else -> Icons.Default.Info
                                                },
                                                contentDescription = quota.status,
                                                tint = when {
                                                    isPagada -> TextDarkGreen
                                                    isVencida -> TextDarkRed
                                                    else -> TextGrey
                                                },
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "Cuota ${idx + 1}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalBlue
                                            )
                                            Text(
                                                text = if (isVencida) "Vencida el ${quota.dueDate}" else "Vence el ${quota.dueDate}",
                                                fontSize = 10.sp,
                                                color = if (isVencida) Color(0xFFC53030) else TextGrey,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = if (isVencida) "S/. 110.00" else "S/. 100.00",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isVencida) Color(0xFFC53030) else RoyalBlue
                                        )
                                        Icon(
                                            imageVector = if (expandedQuotaId == quota.quotaId) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand details",
                                            tint = TextGrey,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                // Embedded Accordion Micro-Detail Card (Interactive Compliance Layer)
                                if (expandedQuotaId == quota.quotaId) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Local verification states for interactive document indicators
                                            var isTiveVerified by remember { mutableStateOf(true) }
                                            var isSoatVerified by remember { mutableStateOf(true) }
                                            var isDniChecked by remember { mutableStateOf(true) }

                                            // 1. [Estado de Cuota]
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Estado de Cuota:",
                                                    fontSize = 11.sp,
                                                    color = TextGrey,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = when {
                                                            isPagada -> Icons.Default.CheckCircle
                                                            isVencida -> Icons.Default.Warning
                                                            else -> Icons.Default.Info
                                                        },
                                                        contentDescription = null,
                                                        tint = when {
                                                            isPagada -> TextDarkGreen
                                                            isVencida -> SolidRed
                                                            else -> Color(0xFFF59E0B)
                                                        },
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Text(
                                                        text = when {
                                                            isPagada -> "Pagado ✓"
                                                            isVencida -> "Vencido/Mora ⚠"
                                                            else -> "Próximo a Vencer ⌛"
                                                        },
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = when {
                                                            isPagada -> TextDarkGreen
                                                            isVencida -> SolidRed
                                                            else -> Color(0xFFF59E0B)
                                                        }
                                                    )
                                                }
                                            }

                                            Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

                                            // 2. [Desglose Financiero]
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Monto Base:", fontSize = 11.sp, color = TextGrey)
                                                Text("S/. 100.00", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                                            }

                                            val accruedMora = if (isVencida) 10.00 else 0.00
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Recargo Mora Acumulada:", fontSize = 11.sp, color = TextGrey)
                                                Text(
                                                    text = "S/. ${accruedMora}0",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (accruedMora > 0) SolidRed else TextGrey
                                                )
                                            }

                                            // Mora Validation banner UI
                                            if (isVencida) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(SolidRed.copy(alpha = 0.08f))
                                                        .border(1.dp, SolidRed.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                        .padding(8.dp)
                                                ) {
                                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        Text(
                                                            text = "⚠️ VALIDACIÓN DE MORA ACTIVA",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = SolidRed
                                                        )
                                                        Text(
                                                            text = "RECARGO ADICIONAL POR MORA: S/. 10.00 por semana vencida de retraso acumulado.",
                                                            fontSize = 9.sp,
                                                            color = TextDarkRed,
                                                            lineHeight = 11.sp
                                                        )
                                                    }
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Costo Total Transacción:", fontSize = 11.sp, color = DarkBlue, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = "S/. ${quota.amount + accruedMora}0",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isVencida) SolidRed else RoyalBlue
                                                )
                                            }

                                            Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

                                            // 3. [Detalle Operativo] (Operational Metadata)
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Pasarela / Canal Pago:", fontSize = 10.sp, color = TextGrey)
                                                    Text(
                                                        text = if (isPagada) "Yape (Pasarela de Pago Express)" else "PagoEfectivo / Agentes BCP",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkBlue
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Unique Reference ID (Hash):", fontSize = 10.sp, color = TextGrey)
                                                    Text(
                                                        text = if (isPagada) "TXN-${quota.quotaId}-YAPE39281" else "PENDIENTE_DE_REGISTRO",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkBlue
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text("Timestamp Completo (D/M/Y):", fontSize = 10.sp, color = TextGrey)
                                                    Text(
                                                        text = if (isPagada) "Miércoles, 20 de Mayo, 2026 - 18:45:12" else "No registrado",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = DarkBlue
                                                    )
                                                }
                                            }

                                            Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)

                                            // 4. [Compliance Document Links]
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Trazabilidad de Documentos de Cumplimiento (Toque para validar):",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = RoyalBlue
                                                )

                                                // DNI/C.E. verification row
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isDniChecked) Color(0xFFF0FDF4) else Color(0xFFFEF2F2))
                                                        .clickable { isDniChecked = !isDniChecked }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "• Identidad Oficial [DNI/C.E. del Titular]",
                                                        fontSize = 10.sp,
                                                        color = Color.Black
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isDniChecked) SuccessGreen.copy(alpha = 0.2f) else SoftWarningRed)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isDniChecked) "Validado ✅" else "Revisión Pendiente ❌",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isDniChecked) TextDarkGreen else TextDarkRed
                                                        )
                                                    }
                                                }

                                                // SOAT verification row
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isSoatVerified) Color(0xFFF0FDF4) else Color(0xFFFEF2F2))
                                                        .clickable { isSoatVerified = !isSoatVerified }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "• Cobertura de Accidentes [SOAT MOTO]",
                                                        fontSize = 10.sp,
                                                        color = Color.Black
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isSoatVerified) SuccessGreen.copy(alpha = 0.2f) else SoftWarningRed)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isSoatVerified) "Vigente ✅" else "Vencido/No Activo ❌",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSoatVerified) TextDarkGreen else TextDarkRed
                                                        )
                                                    }
                                                }

                                                // TIVE verification row
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isTiveVerified) Color(0xFFF0FDF4) else Color(0xFFFEF2F2))
                                                        .clickable { isTiveVerified = !isTiveVerified }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "• Tarjeta de Propiedad [TIVE SUNARP]",
                                                        fontSize = 10.sp,
                                                        color = Color.Black
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isTiveVerified) SuccessGreen.copy(alpha = 0.2f) else SoftWarningRed)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isTiveVerified) "Consolidada ✅" else "Incompleta ❌",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isTiveVerified) TextDarkGreen else TextDarkRed
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4.0 NEW VISUAL COMPONENT: REWARDS & BENEFITS TIER (REPSOL INTEGRATION)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "",
                                tint = RoyalBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Mis Beneficios Global Go",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = RoyalBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Servicios y convenios comerciales vigentes por tu puntualidad",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextGrey,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Corporate benefits horizontal carousel (compact, responsive, scrollable)
                    val benefitSpecs = listOf(
                        Pair("Convenio Oficial", "Descuento REPSOL: S/. 2.00 por galón"),
                        Pair("Beneficio Mensual", "Lavado de Moto Gratis de Flota"),
                        Pair("Seguro Preferente", "SOAT Ampliado La Positiva"),
                        Pair("Asistencia Vial", "Auxilio Mecánico en Ruta 24/7")
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        benefitSpecs.forEach { (tier, desc) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SoftSuccessGreen.copy(alpha = 0.45f)),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    1.dp,
                                    Color(0xFFC8E6C9)
                                ),
                                modifier = Modifier
                                    .width(240.dp) // Compact responsive size for mobile carousel card
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SuccessGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = desc,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = DarkBlue
                                            ),
                                            softWrap = false,
                                            maxLines = 1,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = tier,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextDarkGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            softWrap = false,
                                            maxLines = 1
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1B5E20))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "✓",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5.0 LEGAL DOCUMENT REPOSITORY SYSTEM (SBS COMPLIANCE)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, RoyalBlue.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "",
                            tint = RoyalBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Repositorio Legal y Transparencia SBS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    Text(
                        text = "Documentación contractual obligatoria regulada por la Superintendencia de Banca, Seguros y AFP (SBS):",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextGrey),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val context = androidx.compose.ui.platform.LocalContext.current

                    // Link 1
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BackgroundGrey),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com"))
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "📄", fontSize = 18.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Contrato de Financiamiento Corporativo Global Go (PDF) 📄",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )
                                Text(
                                    text = "Toque para abrir y examinar cláusulas comerciales",
                                    fontSize = 9.sp,
                                    color = TextGrey
                                )
                            }
                        }
                    }

                    // Link 2
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BackgroundGrey),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com"))
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "📄", fontSize = 18.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hoja de Resumen de Intereses y Comisiones Regulada SBS (PDF) 📄",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )
                                Text(
                                    text = "Toque para abrir el documento oficial de tasas y mora",
                                    fontSize = 9.sp,
                                    color = TextGrey
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===========================================
@Composable
fun DocumentThumbnailPreview(docType: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simulated thumbnail with high-contrast icon
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = when (docType) {
                            "TIVE" -> Icons.Default.Info
                            "SOAT" -> Icons.Default.Warning
                            "BREVETE" -> Icons.Default.Star
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        tint = RoyalBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "JPG ✓",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Real simulated metadata of the uploaded document with checkmark
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Validado",
                        tint = TextDarkGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "VISTA PREVIA DE $docType CARGADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkGreen,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Archivo: scan_${docType.lowercase()}_982741.jpg",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "Dimensiones: 1080 x 1920 • Peso: 1.4 MB",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
                Text(
                    text = "ESTADO: CAPTURADO Y SINCRONIZADO AL SERVIDOR",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue
                )
            }
        }
    }
}

// ===========================================
// HELPERS FOR OFFLINE DIGITAL GUANTERA
// ===========================================
fun saveUriToFile(context: android.content.Context, uri: android.net.Uri, fileName: String): File? {
    return try {
        val destinationFile = File(context.filesDir, fileName)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        destinationFile
    } catch (e: Exception) {
         e.printStackTrace()
         null
    }
}

fun saveBitmapToFile(context: android.content.Context, bitmap: android.graphics.Bitmap, fileName: String): File? {
    return try {
        val destinationFile = File(context.filesDir, fileName)
        FileOutputStream(destinationFile).use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
        }
        destinationFile
    } catch (e: Exception) {
         e.printStackTrace()
         null
    }
}

fun generateDemoDocument(context: android.content.Context, title: String, userDni: String): android.graphics.Bitmap {
    val width = 800
    val height = 500
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = android.graphics.Paint()
    
    // Background card (Soft blue/grey border with white center)
    paint.color = android.graphics.Color.rgb(241, 245, 249)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    
    paint.color = android.graphics.Color.WHITE
    canvas.drawRoundRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), 16f, 16f, paint)
    
    paint.color = android.graphics.Color.rgb(37, 99, 235) // Royal blue border
    paint.style = android.graphics.Paint.Style.STROKE
    paint.strokeWidth = 4f
    canvas.drawRoundRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), 16f, 16f, paint)
    
    // Header band
    paint.style = android.graphics.Paint.Style.FILL
    paint.color = android.graphics.Color.rgb(37, 99, 235) // Royal blue
    canvas.drawRect(20f, 20f, (width - 20).toFloat(), 100f, paint)
    
    // Title
    paint.color = android.graphics.Color.WHITE
    paint.textSize = 28f
    paint.isAntiAlias = true
    canvas.drawText("GLOBAL GO PERÚ - DOCUMENTO OFICIAL", 50f, 65f, paint)
    
    // Watermark
    paint.color = android.graphics.Color.rgb(226, 232, 240)
    paint.textSize = 64f
    canvas.drawText("COPIA LOCAL", 200f, 300f, paint)
    
    // Details
    paint.color = android.graphics.Color.rgb(15, 23, 42)
    paint.textSize = 24f
    canvas.drawText("Tipo de Documento: $title", 60f, 160f, paint)
    canvas.drawText("Usuario ID: $userDni", 60f, 210f, paint)
    canvas.drawText("Estado: VALIDADO 100% OFFLINE", 60f, 260f, paint)
    canvas.drawText("Fecha de Registro: 07 de Junio, 2026", 60f, 310f, paint)
    canvas.drawText("CHISPA GO - Tu Guantera Digital Privada", 60f, 400f, paint)
    
    // Security bar at the bottom
    paint.color = android.graphics.Color.rgb(220, 38, 38) // Red
    canvas.drawRect(20f, (height - 40).toFloat(), (width - 20).toFloat(), (height - 20).toFloat(), paint)
    
    return bitmap
}

@Composable
fun DocumentPreview(filePath: String?, modifier: Modifier = Modifier, onImageClick: () -> Unit = {}) {
    if (filePath == null) return
    val bitmap = remember(filePath) {
        try {
            val file = java.io.File(filePath)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(filePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Vista previa de documento local",
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, RoyalBlue, RoundedCornerShape(12.dp))
                .clickable { onImageClick() },
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Text("Archivo no encontrado en almacenamiento", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

// ===========================================
@Composable
fun GuanteraTab(viewModel: ChispaViewModel) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val appBg = if (isDark) Color(0xFF121212) else BackgroundGrey
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val textMain = if (isDark) Color.White else Color.Black
    val textSub = if (isDark) Color(0xFFCCCCCC) else Color.DarkGray
    val strokeCol = if (isDark) Color(0xFF2D3748) else Color.LightGray.copy(alpha = 0.5f)

    val userDni = viewModel.currentUserCode.collectAsState().value ?: ""

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPreferences = remember(context) { context.getSharedPreferences("GuanteraPrefs", android.content.Context.MODE_PRIVATE) }

    // Local paths from SharedPreferences
    var dniPath by remember(userDni) { mutableStateOf(sharedPreferences.getString("${userDni}_path_dni", null)) }
    var licenciaPath by remember(userDni) { mutableStateOf(sharedPreferences.getString("${userDni}_path_licencia", null)) }
    var tarjetaPath by remember(userDni) { mutableStateOf(sharedPreferences.getString("${userDni}_path_tarjeta", null)) }
    var soatPath by remember(userDni) { mutableStateOf(sharedPreferences.getString("${userDni}_path_soat", null)) }

    // Expanded accordion states
    var isDniExpanded by remember { mutableStateOf(false) }
    var isLicenciaExpanded by remember { mutableStateOf(false) }
    var isTarjetaExpanded by remember { mutableStateOf(false) }
    var isSoatExpanded by remember { mutableStateOf(false) }

    var activeDocTypeForOption by remember { mutableStateOf<String?>(null) }
    var zoomedImagePath by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Activity Result Launchers
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            val docType = activeDocTypeForOption ?: return@let
            val fileName = "${userDni}_${docType}.jpg"
            val savedFile = saveUriToFile(context, it, fileName)
            if (savedFile != null) {
                val savedPath = savedFile.absolutePath
                sharedPreferences.edit().putString("${userDni}_path_${docType}", savedPath).apply()
                when (docType) {
                    "dni" -> dniPath = savedPath
                    "licencia" -> licenciaPath = savedPath
                    "tarjeta" -> tarjetaPath = savedPath
                    "soat" -> soatPath = savedPath
                }
            }
            activeDocTypeForOption = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: android.graphics.Bitmap? ->
        bitmap?.let {
            val docType = activeDocTypeForOption ?: return@let
            val fileName = "${userDni}_${docType}.jpg"
            val savedFile = saveBitmapToFile(context, it, fileName)
            if (savedFile != null) {
                val savedPath = savedFile.absolutePath
                sharedPreferences.edit().putString("${userDni}_path_${docType}", savedPath).apply()
                when (docType) {
                    "dni" -> dniPath = savedPath
                    "licencia" -> licenciaPath = savedPath
                    "tarjeta" -> tarjetaPath = savedPath
                    "soat" -> soatPath = savedPath
                }
            }
            activeDocTypeForOption = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Guantera Shield",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Mi Guantera Digital",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Visualiza tus documentos de forma 100% OFFLINE y local. No compartas tus fotos privadas en Internet, manténlas seguras en tu almacenamiento local privado.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BackgroundGrey.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        // 1. DNI / C.E. Accordion
        item {
            val isDniUploaded = dniPath != null
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDniExpanded = !isDniExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🪪", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = "DNI / C.E. (Identidad)",
                                    fontWeight = FontWeight.Bold,
                                    color = textMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Documento Nacional de Identidad",
                                    fontSize = 11.sp,
                                    color = textSub
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDniUploaded) SoftSuccessGreen else Color(0xFFFEF2F2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isDniUploaded) "GUARDADO" else "PENDIENTE",
                                    color = if (isDniUploaded) TextDarkGreen else SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isDniExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = RoyalBlue
                            )
                        }
                    }

                    AnimatedVisibility(visible = isDniExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))
                            Text(
                                text = "Captura tu Documento de Identidad (frente/reverso) para resguardarlo de forma segura en las carpetas de almacenamiento interno de tu móvil.",
                                fontSize = 12.sp,
                                color = textSub,
                                lineHeight = 16.sp
                            )

                            if (isDniUploaded) {
                                DocumentPreview(dniPath, onImageClick = { zoomedImagePath = dniPath })
                            }

                            Button(
                                onClick = { activeDocTypeForOption = "dni" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isDniUploaded) SuccessGreen else RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (isDniUploaded) "TOMAR FOTO / SUBIR NUEVO" else "TOMAR FOTO / SUBIR ARCHIVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Licencia de Conducir Accordion
        item {
            val isLicenciaUploaded = licenciaPath != null
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isLicenciaExpanded = !isLicenciaExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🪪", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = "Licencia de Conducir",
                                    fontWeight = FontWeight.Bold,
                                    color = textMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Brevete de Motocicleta",
                                    fontSize = 11.sp,
                                    color = textSub
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isLicenciaUploaded) SoftSuccessGreen else Color(0xFFFEF2F2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isLicenciaUploaded) "GUARDADO" else "PENDIENTE",
                                    color = if (isLicenciaUploaded) TextDarkGreen else SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isLicenciaExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = RoyalBlue
                            )
                        }
                    }

                    AnimatedVisibility(visible = isLicenciaExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))
                            Text(
                                text = "Sube la foto del brevete vigente para mantener tu expediente libre de infracciones.",
                                fontSize = 12.sp,
                                color = textSub,
                                lineHeight = 16.sp
                            )

                            if (isLicenciaUploaded) {
                                DocumentPreview(licenciaPath, onImageClick = { zoomedImagePath = licenciaPath })
                            }

                            Button(
                                onClick = { activeDocTypeForOption = "licencia" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isLicenciaUploaded) SuccessGreen else RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (isLicenciaUploaded) "TOMAR FOTO / SUBIR NUEVO" else "TOMAR FOTO / SUBIR ARCHIVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Tarjeta de Propiedad Accordion
        item {
            val isTarjetaUploaded = tarjetaPath != null
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isTarjetaExpanded = !isTarjetaExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📝", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = "Tarjeta de Propiedad",
                                    fontWeight = FontWeight.Bold,
                                    color = textMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "TIV de la motocicleta",
                                    fontSize = 11.sp,
                                    color = textSub
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTarjetaUploaded) SoftSuccessGreen else Color(0xFFFEF2F2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isTarjetaUploaded) "GUARDADO" else "PENDIENTE",
                                    color = if (isTarjetaUploaded) TextDarkGreen else SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isTarjetaExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = RoyalBlue
                            )
                        }
                    }

                    AnimatedVisibility(visible = isTarjetaExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))
                            Text(
                                text = "Registra de forma offline tu Tarjeta de Identificación Vehicular (TIV) de la moto para acreditar la propiedad legítima.",
                                fontSize = 12.sp,
                                color = textSub,
                                lineHeight = 16.sp
                            )

                            if (isTarjetaUploaded) {
                                DocumentPreview(tarjetaPath, onImageClick = { zoomedImagePath = tarjetaPath })
                            }

                            Button(
                                onClick = { activeDocTypeForOption = "tarjeta" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isTarjetaUploaded) SuccessGreen else RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (isTarjetaUploaded) "TOMAR FOTO / SUBIR NUEVO" else "TOMAR FOTO / SUBIR ARCHIVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. SOAT de la Moto Accordion
        item {
            val isSoatUploaded = soatPath != null
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, strokeCol),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSoatExpanded = !isSoatExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏍️", fontSize = 18.sp)
                            }
                            Column {
                                Text(
                                    text = "SOAT de la Moto",
                                    fontWeight = FontWeight.Bold,
                                    color = textMain,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Seguro de Accidentes Obligatorio",
                                    fontSize = 11.sp,
                                    color = textSub
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSoatUploaded) SoftSuccessGreen else Color(0xFFFEF2F2))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isSoatUploaded) "GUARDADO" else "PENDIENTE",
                                    color = if (isSoatUploaded) TextDarkGreen else SolidRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = if (isSoatExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = RoyalBlue
                            )
                        }
                    }

                    AnimatedVisibility(visible = isSoatExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HorizontalDivider(color = strokeCol.copy(alpha = 0.5f))
                            Text(
                                text = "Sube la constancia digital de tu SOAT vigente. Es indispensable para circular seguro y de conformidad con el reglamento nacional de tránsito peruano.",
                                fontSize = 12.sp,
                                color = textSub,
                                lineHeight = 16.sp
                            )

                            if (isSoatUploaded) {
                                DocumentPreview(soatPath, onImageClick = { zoomedImagePath = soatPath })
                            }

                            Button(
                                onClick = { activeDocTypeForOption = "soat" },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isSoatUploaded) SuccessGreen else RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = if (isSoatUploaded) "TOMAR FOTO / SUBIR NUEVO" else "TOMAR FOTO / SUBIR ARCHIVO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Capture Selector Dialog
    if (activeDocTypeForOption != null) {
        val docName = when (activeDocTypeForOption) {
            "dni" -> "DNI / C.E. (Identidad)"
            "licencia" -> "Licencia de Conducir"
            "tarjeta" -> "Tarjeta de Propiedad"
            "soat" -> "SOAT de la Moto"
            else -> ""
        }
        AlertDialog(
            onDismissRequest = { activeDocTypeForOption = null },
            title = {
                Text(
                    text = "Cargar Documento Offline",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿Cómo deseas guardar tu $docName en tu almacenamiento local?",
                        fontSize = 12.sp,
                        color = textMain
                    )
                    
                    Button(
                        onClick = {
                            try {
                                cameraLauncher.launch(null)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📸 Tomar Foto con Cámara", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            try {
                                galleryLauncher.launch("image/*")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🖼️ Elegir de mi Galería", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val docType = activeDocTypeForOption!!
                            val fileName = "${userDni}_${docType}.jpg"
                            val bitmap = generateDemoDocument(context, docName, userDni)
                            val savedFile = saveBitmapToFile(context, bitmap, fileName)
                            if (savedFile != null) {
                                val savedPath = savedFile.absolutePath
                                sharedPreferences.edit().putString("${userDni}_path_${docType}", savedPath).apply()
                                when (docType) {
                                    "dni" -> dniPath = savedPath
                                    "licencia" -> licenciaPath = savedPath
                                    "tarjeta" -> tarjetaPath = savedPath
                                    "soat" -> soatPath = savedPath
                                }
                            }
                            activeDocTypeForOption = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💾 Autogenerar Demo Oficial (100% Offline)", color = Color.White, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeDocTypeForOption = null }) {
                    Text("Cancelar", color = SolidRed)
                }
            }
        )
    }

    // Full-Screen Image Zoom Dialog
    if (zoomedImagePath != null) {
        val path = zoomedImagePath!!
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { zoomedImagePath = null },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .testTag("full_screen_visor")
            ) {
                // Decode the bitmap on demand
                val bitmap = remember(path) {
                    try {
                        val file = File(path)
                        if (file.exists()) {
                            android.graphics.BitmapFactory.decodeFile(path)
                        } else {
                            null
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    var scale by remember { mutableStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }
                    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                        scale = (scale * zoomChange).coerceIn(1f, 4f)
                        offset += offsetChange
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        if (scale > 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            scale = 2.5f
                                        }
                                    }
                                )
                            }
                            .transformable(state = transformState),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Documento Expandido offline",
                            modifier = Modifier
                                .fillMaxSize(0.9f)
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .testTag("expanded_document_view"),
                            contentScale = ContentScale.Fit
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se pudo cargar el archivo original", color = Color.White, fontSize = 14.sp)
                    }
                }

                // Floating "X" close button in top right corner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { zoomedImagePath = null },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            .testTag("close_full_screen_visor")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar Visor",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// TAB 3: CATÁLOGO DE BENEFICIOS COOPERATIVOS
// ============================================
data class CorporateBenefit(
    val title: String,
    val description: String,
    val condition: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val type: String
)

@Composable
fun MotoAcademiaTab(viewModel: ChispaViewModel) {
    val benefits by viewModel.benefitsList.collectAsState()
    val isLoading by viewModel.isBenefitsLoading.collectAsState()
    val error by viewModel.benefitsError.collectAsState()

    var selectedBenefitForDialog by remember { mutableStateOf<ActiveClientBenefit?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchBenefits()
    }

    if (selectedBenefitForDialog != null) {
        val b = selectedBenefitForDialog!!
        AlertDialog(
            onDismissRequest = { selectedBenefitForDialog = null },
            title = {
                Text(
                    text = "Beneficio Exclusivo: ${b.partnerName}",
                    fontWeight = FontWeight.Bold,
                    color = RoyalBlue
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "¡Cupón listo para ser utilizado!",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Show discount code
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = b.discountCode.uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SolidRed,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    }
                    
                    Text(
                        text = "MÉTODO DE REDENCIÓN:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = RoyalBlue
                    )
                    
                    Text(
                        text = b.redemptionMethod.ifBlank { "Muestra este código exclusivo de Global Go en ventanilla al momento del pago." },
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    Text(
                        text = "Cobertura: ${b.coverage}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextGrey
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedBenefitForDialog = null }) {
                    Text("De Acuerdo", color = RoyalBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High-fidelity branding card
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "GLOBAL GO PREMIUM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )
                    Text(
                        text = "Vitrina de Beneficios Premium 🚀",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Text(
                        text = "Tu financiamiento activa una gama completa de privilegios corporativos sin costo adicional para garantizar tu éxito y seguridad diaria.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // Subtitle instructions
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explora tus convenios exclusivos",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextGrey,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${benefits.size} Beneficios",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RoyalBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        if (isLoading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = RoyalBlue,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Consultando convenios exclusivos...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }
            }
        } else if (error != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SolidRed.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SolidRed.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = SolidRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Error de Consulta de Beneficios",
                                fontWeight = FontWeight.Bold,
                                color = SolidRed,
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "Detalle: $error\n¿Desea reintentar la conexión?",
                            fontSize = 11.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.fetchBenefits() },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Reintentar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        } else if (benefits.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay beneficios activos en este momento.", color = Color.DarkGray, fontSize = 12.sp)
                }
            }
        } else {
            items(benefits.size) { index ->
                val b = benefits[index]
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = RoyalBlue.copy(alpha = 0.12f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedBenefitForDialog = b }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Banner with overlay logo
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                            if (b.bannerUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = b.bannerUrl,
                                    contentDescription = "Publicidad",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = b.partnerName,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            if (b.logoUrl.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = (-8).dp, y = (8).dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = b.logoUrl,
                                        contentDescription = "Logo partner",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = b.partnerName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = DarkBlue,
                                    fontWeight = FontWeight.Black
                                ),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RoyalBlue.copy(alpha = 0.08f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = b.coverage,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )
                            }

                            Text(
                                text = b.benefitText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.DarkGray,
                                    lineHeight = 13.sp
                                ),
                                maxLines = 3,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SuccessGreen, RoundedCornerShape(8.dp))
                                    .clickable { selectedBenefitForDialog = b }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = b.discountCode.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// TAB 4: ASISTENTE CHISPA CHAT
// ============================================
@Composable
fun AsistenteTab(viewModel: ChispaViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isEscalated by viewModel.isEscalatedToHuman.collectAsState()
    var textTyped by remember { mutableStateOf("") }
    val isDark by viewModel.isDarkMode.collectAsState()
    var showFaqDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val user by viewModel.currentUser.collectAsState()
    val clientName = user?.name ?: "Conductor"
    val clientDni = user?.code ?: "No especificado"

    val whatsappMsgText = remember(clientName, clientDni) {
        "Hola GlobalGo, soy el conductor $clientName con DNI $clientDni y necesito soporte técnico/financiero en mi cuenta"
    }

    val encodedWhatsappMsg = remember(whatsappMsgText) {
        try {
            java.net.URLEncoder.encode(whatsappMsgText, "UTF-8")
        } catch (e: Exception) {
            "Hola%20GlobalGo"
        }
    }

    val whatsappUrl = "https://api.whatsapp.com/send?phone=51999999999&text=$encodedWhatsappMsg"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // CENTRO DE AYUDA RAPID / FAQ MODULE - ELEVATED HIGH-RELIEF CARDVIEW WITH MARKED SHADOW
        Card(
            colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp), // Elevated shadows (high-relief)
            border = BorderStroke(
                width = 1.dp,
                color = if (isDark) Color(0xFF2D3748) else Color.LightGray.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = "Centro de Auto-Atención Rápida",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else RoyalBlue
                            )
                            Text(
                                text = "Resuelve tus dudas al instante",
                                fontSize = 11.sp,
                                color = if (isDark) Color.LightGray else Color.Gray
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SolidRed.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AUTO-AYUDA GO",
                            color = SolidRed,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showFaqDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("open_faq_modal_btn")
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Preguntas Frecuentes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            val intent = android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(whatsappUrl)
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("whatsapp_transfer_btn")
                    ) {
                        Text("🟢 WhatsApp Go", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Chat messages feed
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // First item: prompt chips
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        "Preguntas Frecuentes (FAQ):",
                        fontSize = 11.sp,
                        color = TextGrey,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        BankingActionChip(
                            label = "Accidente en Moto",
                            onClick = { viewModel.executeBankingChip("Accidente en Moto") },
                            modifier = Modifier.testTag("chip_accidente")
                        )

                        BankingActionChip(
                            label = "Números de Emergencia",
                            onClick = { viewModel.executeBankingChip("Números de Emergencia") },
                            modifier = Modifier.testTag("chip_emergencia")
                        )

                        BankingActionChip(
                            label = "Cobertura SOAT",
                            onClick = { viewModel.executeBankingChip("Cobertura SOAT") },
                            modifier = Modifier.testTag("chip_soat_cov")
                        )

                        BankingActionChip(
                            label = "Moto Robada",
                            onClick = { viewModel.executeBankingChip("Moto Robada") },
                            modifier = Modifier.testTag("chip_robo")
                        )

                        BankingActionChip(
                            label = "Imprevisto de Pago",
                            onClick = { viewModel.executeBankingChip("Imprevisto de Pago") },
                            modifier = Modifier.testTag("chip_imprevisto")
                        )

                        BankingActionChip(
                            label = "Evitar Accidentes",
                            onClick = { viewModel.executeBankingChip("Evitar Accidentes") },
                            modifier = Modifier.testTag("chip_evitar")
                        )
                    }
                }
            }

            items(messages) { msg ->
                val fromMascot = msg.sender == "SOPORTE"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (fromMascot) Arrangement.Start else Arrangement.End
                ) {
                    if (fromMascot) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isEscalated) Color(0xFFE2E8F0) else SolidRed)
                                .align(Alignment.Bottom),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEscalated) "🧑‍💼" else "🛡️", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (fromMascot) Color.White else RoyalBlue
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (fromMascot) 2.dp else 16.dp,
                            bottomEnd = if (fromMascot) 16.dp else 2.dp
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (fromMascot) DarkBlue else Color.White,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        val showHumanConfirmation by viewModel.showHumanConfirmation.collectAsState()
        val isAIPaused by viewModel.isAIPaused.collectAsState()

        if (showHumanConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.showHumanConfirmation.value = false },
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Confirmar Asistencia Humana 👥",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                text = {
                    Text(
                        text = "¿Está seguro de que desea pausar el Asistente Inteligente y ser transferido de forma inmediata con un operador humano de Global Go?",
                        color = Color.Black
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.showHumanConfirmation.value = false
                            viewModel.isAIPaused.value = true
                            viewModel.escalateToHuman()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SolidRed)
                    ) {
                        Text("Sí, Conectar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { viewModel.showHumanConfirmation.value = false },
                        border = BorderStroke(1.dp, Color.Gray)
                    ) {
                        Text("Cancelar", color = Color.Black)
                    }
                }
            )
        }

        if (isAIPaused) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SolidRed),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Asistente Virtual Pausado - Transfiriendo a Operador Humano",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(whatsappUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text(
                            text = "Conectar con Asesor vía WhatsApp / Central ➔",
                            color = SolidRed,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // Message text box typing input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textTyped,
                    onValueChange = { textTyped = it },
                    placeholder = { Text("Escribe una consulta de soporte...", fontSize = 13.sp) },
                    singleLine = true,
                    maxLines = 1,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textTyped.trim().isNotEmpty()) {
                            viewModel.sendCustomText(textTyped)
                            textTyped = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // --- FULLY INTERACTIVE FAQ DIALOG (Accordion Style, structured fully in LIGHT MODE) ---
        if (showFaqDialog) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showFaqDialog = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White), // Explicit light mode background
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        // Title bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("⚡", fontSize = 20.sp)
                                Column {
                                    Text(
                                        "Centro de Ayuda FAQ",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = RoyalBlue
                                    )
                                    Text(
                                        "GLOBALGO FINANCIERA DE MOTOS",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        color = SolidRed
                                    )
                                }
                            }
                            IconButton(onClick = { showFaqDialog = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close modal",
                                    tint = Color.Black
                                )
                            }
                        }

                        HorizontalDivider(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // FAQ Scrollable List - 12 FIXED QUESTIONS & AUTHORITATIVE ANSWERS
                        var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }
                        val faqs = listOf(
                            Pair("¿Qué pasa si me atraso en el pago de mi cuota semanal?", "Se aplica una penalidad diaria automática de mora de S/. 10.00 y se suspenden temporalmente los beneficios corporativos adicionales de ruta."),
                            Pair("¿Cómo funciona el Comodín de Emergencia?", "Te permite congelar/postergar una cuota semanal sin mora en caso de siniestro probado o descanso médico. Debe solicitarse con soporte formal."),
                            Pair("¿Cuál es el proceso para solicitar una Refinanciación de mi deuda?", "Debes tener al menos 4 cuotas pagadas, presentar tu récord crediticio y solicitar evaluación comercial mediante nuestros asesores de Global Go."),
                            Pair("¿Dónde realizo los abonos de mis cuotas semanales?", "Aceptamos pagos seguros vía Yape, Plim, transferencias bancarias directas BCP/BBVA o pago físico en agentes autorizados."),
                            Pair("¿Qué documentos son estrictamente obligatorios de llevar en ruta?", "DNI o C.E. vigente, Licencia de Conducir física/digital, Tarjeta de Propiedad (TIVE) y SOAT activo ante controles policiales."),
                            Pair("¿Cómo demuestro ante la policía que mi SOAT está activo?", "Puedes visualizar la constancia digital en tiempo real desde la sección 'Guantera' de esta aplicación o en la app de APESEG."),
                            Pair("¿Puedo realizar abonos parciales a mi cuota del periodo?", "No. Los abonos deben cubrir el monto total de la cuota semanal vigente más penalidades (si corresponden) para evitar mora."),
                            Pair("¿Qué es la Reevaluación Comercial de solicitud?", "Si tu scoring comercial no califica inicialmente por DNI/CE, tu expediente se rige bajo 'FINANCIAMIENTO SUJETO A REEVALUACIÓN' pendiente de nuevo sustento de ingresos."),
                            Pair("¿Qué talleres mecánicos están autorizados ante desperfectos?", "Global Go cuenta con una red nacional homologada. Consulta los talleres autorizados con el Asistente en la pestaña de consultas rápidas."),
                            Pair("¿Cómo canjeo los cupones de descuento corporativos?", "Ve a la pestaña 'Moto Premium', genera tu código (Ej: repsolgo) y muéstralo en ventanilla siguiendo el método de redención de la tienda asociada."),
                            Pair("¿Puedo cambiar la fecha de vencimiento de mis cuotas semanales?", "No. Las cuotas semanales vencen puntualmente cada 7 días calendario a partir del día de adjudicación y entrega física de la moto."),
                            Pair("¿Cómo contacto de forma inmediata con un asesor humano directo?", "Si el chatbot no resuelve tu consulta en 2 intentos o dices 'soporte', se habilitará el botón destacado de enlace/acción con WhatsApp para atención directa.")
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            faqs.forEachIndexed { idx, faq ->
                                val isExpanded = expandedFaqIndex == idx
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), // Explicit light mode gray
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedFaqIndex = if (isExpanded) null else idx }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = faq.first,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = RoyalBlue,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = RoyalBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        if (isExpanded) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = faq.second,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = Color.DarkGray
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.executeBankingChip(faq.first)
                                                    showFaqDialog = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier
                                                    .align(Alignment.End)
                                                    .height(28.dp)
                                            ) {
                                                Text("Preguntar a Gemini ⚡", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { showFaqDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Entendido", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BankingActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(RoyalBlue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.dp, RoyalBlue.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalBlue
        )
    }
}
