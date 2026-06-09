package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: ChispaViewModel,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()

    // Rule 1: Dynamic empathetic greeting according to local device hour
    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when (hour) {
            in 6..11 -> "Buen día"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }

    // Color assets adapting dynamically to selected mode
    val appBg = if (isDark) Color(0xFF121212) else BackgroundGrey
    val textMain = if (isDark) Color.White else DarkBlue
    val textSub = if (isDark) Color.LightGray else TextGrey
    val cardBg = if (isDark) Color(0xFF1E1E1E) else Color.White
    val borderCol = if (isDark) Color(0xFF2D2D2D) else Color.LightGray.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(appBg)
    ) {
        // Decorative background gradient circles
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(Brush.radialGradient(listOf(RoyalBlue.copy(alpha = if (isDark) 0.15f else 0.08f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (80).dp, y = (100).dp)
                .background(Brush.radialGradient(listOf(SolidRed.copy(alpha = if (isDark) 0.15f else 0.05f), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // TOP BAR: Clean showing clean logo of CHISPA GO (No switch, no tags/codes)
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SolidRed)
                    )
                    Text(
                        text = "CHISPA GO",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = textMain,
                        letterSpacing = 1.sp
                    )
                }
            }

            // MIDDLE CONTENT: Logo, greeting, and prospect action beautifully centered and balanced
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Official Logo Branded mascot
                Image(
                    painter = painterResource(id = com.example.R.drawable.imagen_chispa),
                    contentDescription = "Chispa Go Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp) // Reduced size from 140.dp to 100.dp for compactness
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "CHISPA",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = RoyalBlue,
                        letterSpacing = (-0.5).sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SolidRed)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "GO",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Plataforma de Control Financiero Perú",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSub,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Elegant static welcome text as requested
                Text(
                    text = "Bienvenido a CHISPA GO",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textMain,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // EXSTING CUSTOMER LOGIN CARD
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    val docType by viewModel.typedDocumentType.collectAsState()
                    val docNum by viewModel.typedDocumentNumber.collectAsState()
                    val isLoggingIn by viewModel.isLoggingIn.collectAsState()
                    val loginError by viewModel.loginErrorMessage.collectAsState()

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "INGRESO DE CLIENTES",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = RoyalBlue,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Ingresa tu número de documento para gestionar tus cuotas, acceder a la guantera digital y reclamar tus beneficios.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSub,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        // Document Type Picker Slots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // DNI Slot
                            val isDniSelected = docType == "1"
                            Button(
                                onClick = { viewModel.typedDocumentType.value = "1" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDniSelected) RoyalBlue else cardBg
                                ),
                                border = BorderStroke(1.dp, if (isDniSelected) RoyalBlue else borderCol),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("doc_type_dni_chip")
                            ) {
                                Text(
                                    text = "DNI",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDniSelected) Color.White else textMain
                                )
                            }

                            // C.E. Slot
                            val isCeSelected = docType == "3"
                            Button(
                                onClick = { viewModel.typedDocumentType.value = "3" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCeSelected) RoyalBlue else cardBg
                                ),
                                border = BorderStroke(1.dp, if (isCeSelected) RoyalBlue else borderCol),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("doc_type_ce_chip")
                              ) {
                                Text(
                                    text = "C.E.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCeSelected) Color.White else textMain
                                )
                            }
                        }

                        // Document Number OutlinedTextField
                        OutlinedTextField(
                            value = docNum,
                            onValueChange = { viewModel.typedDocumentNumber.value = it.filter { c -> c.isLetterOrDigit() } },
                            label = { Text("Número de Documento", fontSize = 12.sp) },
                            placeholder = { Text(if (docType == "1") "Ej. 45871263" else "Ej. 000147820", fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = textMain,
                                unfocusedTextColor = textMain,
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = borderCol,
                                focusedLabelColor = RoyalBlue,
                                unfocusedLabelColor = textSub
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doc_number_input")
                        )

                        // Real-time explicit red alerts under the field if invalid
                        val cleanNum = docNum.trim()
                        val lengthAlert = when {
                            cleanNum.isEmpty() -> null
                            docType == "1" && cleanNum.length != 8 -> "⚠️ El DNI debe contener exactamente 8 dígitos."
                            docType == "3" && cleanNum.length > 12 -> "⚠️ El C.E. no puede exceder los 12 caracteres."
                            docType == "3" && cleanNum.length < 6 -> "⚠️ El C.E. debe contener al menos 6 caracteres."
                            else -> null
                        }

                        if (lengthAlert != null) {
                            Text(
                                text = lengthAlert,
                                color = SolidRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 4.dp),
                                textAlign = TextAlign.Start
                            )
                        }

                        if (!loginError.isNullOrEmpty()) {
                            Text(
                                text = loginError ?: "",
                                color = SolidRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Enter button
                        val buttonEnabled = cleanNum.isNotEmpty() && lengthAlert == null
                        Button(
                            onClick = { viewModel.submitLogin() },
                            enabled = buttonEnabled,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (buttonEnabled) RoyalBlue else RoyalBlue.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_btn")
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = "Ingresar a mi Cuenta",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // FIXED & HIGHLY OUTSTANDING RED BRAND BUTTON (OUTSIDE OF THE CARD AND INDEPENDENT)
                Button(
                    onClick = { viewModel.iniciarSolicitudFinanciamiento() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)), // Rojo llamativo
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(58.dp)
                        .testTag("prospect_onboard_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Solicitar Financiamiento",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¿NUEVO EN GLOBAL GO? -> Solicitar financiamiento",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // BOTTOM MARGIN PADDING
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
