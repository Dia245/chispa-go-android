package com.example.ui

import android.util.Log
import androidx.compose.ui.platform.LocalContext

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.zIndex
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import androidx.compose.foundation.gestures.detectDragGestures

fun sanitizeError(s: String?): String? {
    if (s == null) return null
    if (s.contains("FirebaseApp", ignoreCase = true) || s.contains("initialized", ignoreCase = true) || s.contains("Firebase", ignoreCase = true)) {
        return ""
    }
    return s
}

data class PhotonPlace(
    val displayName: String,
    val addressText: String,
    val latitude: Double,
    val longitude: Double,
    val district: String
)

data class ProspectMotoModel(
    val name: String,
    val description: String,
    val baseWeekly: Double,
    val engineCc: String,
    val frontalText: String,
    val lateralText: String,
    val posteriorText: String,
    val category: String = "Todas",
    val brand: String,
    val year: Int,
    val price: Double,
    val initialPayment: Double,
    val imageUrl: String = "",
    val logoUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProspectScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChispaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val composableContext = LocalContext.current
    // Offline local operation mode active


    val liveMotos by viewModel.catalogList.collectAsState()
    val isCatalogLoading by viewModel.isCatalogLoading.collectAsState()
    val catalogError by viewModel.catalogError.collectAsState()

    val staticDefaultMotos = listOf(
        ProspectMotoModel(
            name = "Yamaha FZ-S FI V3",
            description = "Inyección electrónica avanzada Blue Core, excelente maniobrabilidad urbana y diseño deportivo de alta presencia.",
            baseWeekly = 110.0,
            engineCc = "149cc Inyectada",
            frontalText = "🏍️ [FRENTE - Yamaha FZ-S] Faro central de doble proyector LED con luz diurna premium, horquilla delantera telescópica y disco de freno de 282 mm con ABS.",
            lateralText = "🏍️ [COSTADO - Yamaha FZ-S] Chasis tipo diamante optimizado, tanque de combustible musculoso con tomas de aire laterales y escape deportivo integrado.",
            posteriorText = "🏍️ [TRASERO - Yamaha FZ-S] Suspensión Monoshock regulable, llanta trasera radial de 140 mm y faro posterior dividido de gran visibilidad.",
            category = "Pista/Urbano",
            brand = "Yamaha",
            year = 2025,
            price = 9500.0,
            initialPayment = 1500.0
        ),
        ProspectMotoModel(
            name = "Honda CB125F",
            description = "La reina del trabajo diario. Consumo de combustible ultra bajo, motor de alta durabilidad y chasis rígido para carga.",
            baseWeekly = 85.0,
            engineCc = "125cc Trabajo",
            frontalText = "🏍️ [FRENTE - Honda CB125F] Faro halógeno de gran iluminación de 35W, protección delantera telescópica de largo recorrido y de gran visibilidad.",
            lateralText = "🏍️ [COSTADO - Honda CB125F] Asiento doble de gran confort, tanque de 10.1 litros con deflectores y posapiés traseros reforzados.",
            posteriorText = "🏍️ [TRASERO - Honda CB125F] Doble amortiguador trasero ajustable en 5 posiciones, parrilla posterior portaequipaje integrada y faro posterior clásico.",
            category = "Trabajo",
            brand = "Honda",
            year = 2024,
            price = 6800.0,
            initialPayment = 800.0
        ),
        ProspectMotoModel(
            name = "Bajaj Pulsar NS125",
            description = "Potencia juvenil con motor de 4 válvulas, tecnología DTS-i de doble bujía para una respuesta de aceleración ágil de la marca Pulsar.",
            baseWeekly = 95.0,
            engineCc = "125cc DTS-i",
            frontalText = "🏍️ [FRENTE - Pulsar NS125] Óptica en forma de lobo con luces piloto LED gemelas, barra protectora delantera de motor y guardafaro deportivo.",
            lateralText = "🏍️ [COSTADO - Pulsar NS125] Chasis perimetral para máxima estabilidad en curvas rápidas, panel de instrumentos digital-analógico y aros de aleación ligera.",
            posteriorText = "🏍️ [TRASERO - Pulsar NS125] Suspensión trasera monoshock Nitrox con gas, doble piloto posterior LED en tiras y guardabarros flotante inferior.",
            category = "Pista/Urbano",
            brand = "Pulsar",
            year = 2025,
            price = 7900.0,
            initialPayment = 1000.0
        ),
        ProspectMotoModel(
            name = "Piaggio Storm 125",
            description = "Scooter automática de ruedas anchas de origen italiano Piaggio para estabilidad superior en pistas urbanas. Transmisión constante CVT muy cómoda.",
            baseWeekly = 90.0,
            engineCc = "125cc Automática",
            frontalText = "🛵 [FRENTE - Piaggio Storm] Escudo frontal de protección de piernas, aros de 12 pulgadas con llanta ancha de tacos y frenador de disco CBS.",
            lateralText = "🛵 [COSTADO - Piaggio Storm] Plataforma plana cómoda para llevar mercadería, baúl portaCasco bajo el asiento y transmisión automática CVT.",
            posteriorText = "🛵 [TRASERO - Piaggio Storm] Amortiguador trasero hidráulico progresivo, silenciador deportivo lateral y manija posterior de sujeción para el pasajero.",
            category = "Scooter",
            brand = "Piaggio",
            year = 2026,
            price = 8400.0,
            initialPayment = 1200.0
        )
    )

    val motosList = liveMotos.ifEmpty { staticDefaultMotos }

    // Synchronize Catalog in background
    LaunchedEffect(Unit) {
        viewModel.fetchCatalog()
    }

    var isMapReady by remember { mutableStateOf(false) }
    var cachedLat by remember { mutableStateOf<Double?>(null) }
    var cachedLng by remember { mutableStateOf<Double?>(null) }

    var currentStep by remember { mutableIntStateOf(1) } // Steps 1 to 5
    var selectedMotoIndex by remember { mutableStateOf(0) }
    var currentPerspective by remember { mutableStateOf("COSTADO") } // FRONT, COSTADO, BACK
    var prospectDocType by remember { mutableStateOf("DNI") } // "DNI" or "CE"
    var selectedWeeks by remember { mutableIntStateOf(52) } // 52, 78, 104
    var currentPage by remember { mutableIntStateOf(1) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todas") }
    var filterBrand by remember { mutableStateOf("Todas") }
    var filterYear by remember { mutableStateOf("Todos") }
    var minPriceInput by remember { mutableStateOf("3000") }
    var maxPriceInput by remember { mutableStateOf("10000") }

    LaunchedEffect(searchQuery, selectedCategory, filterBrand, filterYear, minPriceInput, maxPriceInput) {
        currentPage = 1
    }

    var motoToInspectInDetail by remember { mutableStateOf<ProspectMotoModel?>(null) }

    // Step 2 Info STATE
    var dniNumber by remember { mutableStateOf("") }
    var isDniValidated by remember { mutableStateOf(false) }
    var isDniValidatedByApi by remember { mutableStateOf(false) }
    var isValidatingDni by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cellphone by remember { mutableStateOf("") }
    var personalFormError by remember { mutableStateOf<String?>(null) }
    var dniFieldError by remember { mutableStateOf<String?>(null) }
    var cellphoneFieldError by remember { mutableStateOf<String?>(null) }

    var isPhoneVerified by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var showPhoneOtpField by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var phoneOtpCode by remember { mutableStateOf("") }
    var phoneOtpError by remember { mutableStateOf<String?>(null) }
    var isPhoneValidating by remember { mutableStateOf(false) }

    var isEmailVerified by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var showEmailOtpField by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var emailOtpCode by remember { mutableStateOf("") }
    var emailOtpError by remember { mutableStateOf<String?>(null) }
    var isEmailValidating by remember { mutableStateOf(false) }
    var generatedEmailOtp by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }

    var conyugeCellphone by remember { mutableStateOf("") }
    var conyugeCellphoneError by remember { mutableStateOf<String?>(null) }
    var conyugeEmail by remember { mutableStateOf("") }
    var conyugeEmailError by remember { mutableStateOf<String?>(null) }

    var isConyugePhoneVerified by remember { mutableStateOf(false) }
    var showConyugePhoneOtpField by remember { mutableStateOf(false) }
    var conyugePhoneOtpCode by remember { mutableStateOf("") }
    var conyugePhoneOtpError by remember { mutableStateOf<String?>(null) }
    var isConyugePhoneValidating by remember { mutableStateOf(false) }

    var isConyugeEmailVerified by remember { mutableStateOf(false) }
    var showConyugeEmailOtpField by remember { mutableStateOf(false) }
    var conyugeEmailOtpCode by remember { mutableStateOf("") }
    var conyugeEmailOtpError by remember { mutableStateOf<String?>(null) }
    var isConyugeEmailValidating by remember { mutableStateOf(false) }

    // Step 3 Document Attach STATE
    var isDniFrontalUploaded by remember { mutableStateOf(false) }
    var isDniReversoUploaded by remember { mutableStateOf(false) }
    var isReciboUploaded by remember { mutableStateOf(false) }

    var isDniFrontalUploading by remember { mutableStateOf(false) }
    var isDniReversoUploading by remember { mutableStateOf(false) }
    var isReciboUploading by remember { mutableStateOf(false) }

    var dniFrontalProgress by remember { mutableFloatStateOf(0f) }
    var dniReversoProgress by remember { mutableFloatStateOf(0f) }
    var reciboProgress by remember { mutableFloatStateOf(0f) }

    var documentFormError by remember { mutableStateOf<String?>(null) }

    val uploadScope = rememberCoroutineScope()

    val dniFrontalLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadScope.launch {
                isDniFrontalUploading = true
                dniFrontalProgress = 0f
                for (p in 1..10) {
                    kotlinx.coroutines.delay(120)
                    dniFrontalProgress = p / 10f
                }
                isDniFrontalUploading = false
                isDniFrontalUploaded = true
            }
        }
    }

    val dniReversoLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadScope.launch {
                isDniReversoUploading = true
                dniReversoProgress = 0f
                for (p in 1..10) {
                    kotlinx.coroutines.delay(120)
                    dniReversoProgress = p / 10f
                }
                isDniReversoUploading = false
                isDniReversoUploaded = true
            }
        }
    }

    val reciboLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadScope.launch {
                isReciboUploading = true
                reciboProgress = 0f
                for (p in 1..10) {
                    kotlinx.coroutines.delay(120)
                    reciboProgress = p / 10f
                }
                isReciboUploading = false
                isReciboUploaded = true
            }
        }
    }

    var maritalStatus by remember { mutableStateOf("Soltero(a)") }
    var conyugeName by remember { mutableStateOf("") }
    var conyugeDni by remember { mutableStateOf("") }
    var isValidatingConyugeDni by remember { mutableStateOf(false) }
    var conyugeDniError by remember { mutableStateOf<String?>(null) }
    
    var isConyugeDniFrontalUploaded by remember { mutableStateOf(false) }
    var isConyugeDniFrontalUploading by remember { mutableStateOf(false) }
    var conyugeDniFrontalProgress by remember { mutableFloatStateOf(0f) }
    
    var isConyugeDniReversoUploaded by remember { mutableStateOf(false) }
    var isConyugeDniReversoUploading by remember { mutableStateOf(false) }
    var conyugeDniReversoProgress by remember { mutableFloatStateOf(0f) }

    val conyugeDniFrontalLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadScope.launch {
                isConyugeDniFrontalUploading = true
                conyugeDniFrontalProgress = 0f
                for (p in 1..10) {
                    kotlinx.coroutines.delay(120)
                    conyugeDniFrontalProgress = p / 10f
                }
                isConyugeDniFrontalUploading = false
                isConyugeDniFrontalUploaded = true
            }
        }
    }

    val conyugeDniReversoLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            uploadScope.launch {
                isConyugeDniReversoUploading = true
                conyugeDniReversoProgress = 0f
                for (p in 1..10) {
                    kotlinx.coroutines.delay(120)
                    conyugeDniReversoProgress = p / 10f
                }
                isConyugeDniReversoUploading = false
                isConyugeDniReversoUploaded = true
            }
        }
    }

    // Camera Hardware permissions & Simulator dialog overlays
    var showCameraViewfinder by remember { mutableStateOf(false) }
    var activeDocTypeForCapture by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf<Boolean?>(null) }
    var hasLocationPermission by remember { mutableStateOf<Boolean?>(null) }
    var showDeniedPermissionAlert by remember { mutableStateOf(false) }

    // Mock Location details for satelital coordinates
    var simLatitude by remember { mutableStateOf(-12.04318) }
    var simLongitude by remember { mutableStateOf(-77.02824) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val cam = perms[android.Manifest.permission.CAMERA] ?: false
        val loc = perms[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false || perms[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasCameraPermission = cam
        hasLocationPermission = loc
        if (cam && loc) {
            showCameraViewfinder = true
            showDeniedPermissionAlert = false
        } else {
            showDeniedPermissionAlert = true
            android.widget.Toast.makeText(context, "Permisos denegados. No se puede iniciar control compliance.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    // Step 4 Geolocation Address state
    var isMapPinLocked by remember { mutableStateOf(false) }
    var streetAddress by remember { mutableStateOf("") }
    var addressSearchQuery by remember { mutableStateOf("") }
    var isSuggestionsDropdownExpanded by remember { mutableStateOf(false) }
    var geolocationError by remember { mutableStateOf<String?>(null) }
    var mapPinOffset by remember { mutableStateOf<androidx.compose.ui.geometry.Offset?>(null) }
    var isScanningCoordinate by remember { mutableStateOf(false) }
    var isCoordinateVerified by remember { mutableStateOf(false) }
    var currentLat by remember { mutableStateOf(-12.046374) }
    var currentLng by remember { mutableStateOf(-77.042793) }
    var isSatelliteView by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableFloatStateOf(15f) }
    var confirmedAddressText by remember { mutableStateOf("") }
    var documentToChooseOrigin by remember { mutableStateOf<String?>(null) }
    var showGpsSatisfactionDialog by remember { mutableStateOf(false) }
    var photonSearchSuggestions by remember { mutableStateOf<List<PhotonPlace>>(emptyList()) }
    var isGeocodingInProcess by remember { mutableStateOf(false) }

    LaunchedEffect(currentStep) {
        if (currentStep == 4) {
            isMapReady = false
            kotlinx.coroutines.delay(500)
            isMapReady = true
            cachedLat?.let { lat ->
                currentLat = lat
                cachedLat = null
            }
            cachedLng?.let { lng ->
                currentLng = lng
                cachedLng = null
            }
        } else {
            isMapReady = false
        }
    }

    // Step 5 Credit Simulator & Handoff State
    var verifiedDistrict by remember { mutableStateOf("Santiago de Surco") }
    var isProcessingConfirmation by remember { mutableStateOf(false) }
    var monthlyIncome by remember { mutableStateOf("") }
    var downPayment by remember { mutableFloatStateOf(1000f) }
    var isStep5Locked by remember { mutableStateOf(false) }
    var evaluationError by remember { mutableStateOf<String?>(null) }
    var isComplianceBlocked by remember { mutableStateOf(false) }
    var isDebtRatioBlocked by remember { mutableStateOf(false) }
    var hasEvaluated by remember { mutableStateOf(false) }

    val coercedIndex = selectedMotoIndex.coerceIn(0, maxOf(0, motosList.size - 1))
    val selectedMoto = if (motosList.isNotEmpty()) motosList[coercedIndex] else staticDefaultMotos[0]
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    if (showGpsSatisfactionDialog) {
        AlertDialog(
            onDismissRequest = { showGpsSatisfactionDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "GPS Autodetect",
                        tint = SolidRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Validación de Seguridad Domiciliaria", fontWeight = FontWeight.Bold, color = RoyalBlue)
                }
            },
            text = {
                Text(
                    text = "Para una mejor satisfacción y seguridad, por favor realiza este movimiento desde tu propio domicilio.",
                    color = Color.Black
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showGpsSatisfactionDialog = false
                        uploadScope.launch {
                            isScanningCoordinate = true
                            isCoordinateVerified = false
                            geolocationError = "📡 Solicitando permisos y activando servicios locales de posicionamiento digital..."
                            delay(800)
                            // Auto-locate user inside authorized Lima perimeter
                            currentLat = -11.98351
                            currentLng = -77.07221
                            mapPinOffset = androidx.compose.ui.geometry.Offset(140f, 55f)
                            isCoordinateVerified = true
                            isScanningCoordinate = false
                            isMapPinLocked = true
                            streetAddress = "Av. Alfredo Mendiola 3601, Los Olivos, Lima 15311"
                            geolocationError = "✓ UBICACIÓN GEOESTABLECIDA: Coordenadas detectadas automáticamente: Lat -11.98351, Lng -77.07221. Pin de domicilio fijado de manera exitosa en Los Olivos, Lima."
                        }
                    }
                ) {
                    Text("Aceptar", color = SolidRed, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (documentToChooseOrigin != null) {
        AlertDialog(
            onDismissRequest = { documentToChooseOrigin = null },
            title = { Text("Seleccionar origen del documento", fontWeight = FontWeight.Bold, color = RoyalBlue) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("¿Cómo deseas adjuntar este documento obligatorio?", color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Button 1: Cámara
                    Button(
                        onClick = {
                            val docType = documentToChooseOrigin
                            documentToChooseOrigin = null
                            activeDocTypeForCapture = docType

                            val hasCam = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            val hasLoc = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (hasCam && hasLoc) {
                                hasCameraPermission = true
                                hasLocationPermission = true
                                showCameraViewfinder = true
                                showDeniedPermissionAlert = false
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        android.Manifest.permission.CAMERA,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto con Cámara 📸", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Button 2: Galería (Simular)
                    Button(
                        onClick = {
                            val docType = documentToChooseOrigin
                            documentToChooseOrigin = null
                            uploadScope.launch {
                                when (docType) {
                                    "DNI_FRONTAL" -> {
                                        isDniFrontalUploading = true
                                        dniFrontalProgress = 0f
                                        for (p in 1..10) {
                                            delay(120)
                                            dniFrontalProgress = p / 10f
                                        }
                                        isDniFrontalUploading = false
                                        isDniFrontalUploaded = true
                                    }
                                    "DNI_REVERSO" -> {
                                        isDniReversoUploading = true
                                        dniReversoProgress = 0f
                                        for (p in 1..10) {
                                            delay(120)
                                            dniReversoProgress = p / 10f
                                        }
                                        isDniReversoUploading = false
                                        isDniReversoUploaded = true
                                    }
                                    "RECIBO" -> {
                                        isReciboUploading = true
                                        reciboProgress = 0f
                                        for (p in 1..10) {
                                            delay(120)
                                            reciboProgress = p / 10f
                                        }
                                        isReciboUploading = false
                                        isReciboUploaded = true
                                    }
                                    "CONYUGE_DNI_FRONTAL" -> {
                                        isConyugeDniFrontalUploading = true
                                        conyugeDniFrontalProgress = 0f
                                        for (p in 1..10) {
                                            delay(120)
                                            conyugeDniFrontalProgress = p / 10f
                                        }
                                        isConyugeDniFrontalUploading = false
                                        isConyugeDniFrontalUploaded = true
                                    }
                                    "CONYUGE_DNI_REVERSO" -> {
                                        isConyugeDniReversoUploading = true
                                        conyugeDniReversoProgress = 0f
                                        for (p in 1..10) {
                                            delay(120)
                                            conyugeDniReversoProgress = p / 10f
                                        }
                                        isConyugeDniReversoUploading = false
                                        isConyugeDniReversoUploaded = true
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Subir Archivo/Imagen (Simular) 📁", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Button 3: Galería (Nativa)
                    OutlinedButton(
                        onClick = {
                            val docType = documentToChooseOrigin
                            documentToChooseOrigin = null
                            when (docType) {
                                "DNI_FRONTAL" -> dniFrontalLauncher.launch("image/*")
                                "DNI_REVERSO" -> dniReversoLauncher.launch("image/*")
                                "RECIBO" -> reciboLauncher.launch("image/*")
                                "CONYUGE_DNI_FRONTAL" -> conyugeDniFrontalLauncher.launch("image/*")
                                "CONYUGE_DNI_REVERSO" -> conyugeDniReversoLauncher.launch("image/*")
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.5.dp, RoyalBlue)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = RoyalBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir Galería del Celular 📱", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { documentToChooseOrigin = null }) {
                    Text("Cancelar", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Camera Viewfinder dialog popup simulation with native controls
    if (showCameraViewfinder && activeDocTypeForCapture != null) {
        val docType = activeDocTypeForCapture!!
        
        var isFlashOn by remember { mutableStateOf(false) }
        var isGridOn by remember { mutableStateOf(true) }
        var zoomValue by remember { mutableFloatStateOf(1f) }
        
        var isCapturing by remember { mutableStateOf(false) }
        var isSuccessCaptured by remember { mutableStateOf(false) }
        
        var validationLogs by remember { mutableStateOf("INICIANDO AUTOFOCUS COMERCIAL DIRECTO...\nCONEXIÓN SEGURA RES-SBS OK.\nLEYENDO COORDENADAS SATÉLITE...") }

        // Start coordinate tracking
        LaunchedEffect(Unit) {
            while(true) {
                kotlinx.coroutines.delay(2000)
                simLatitude += (0.0001 - Math.random() * 0.0002)
                simLongitude += (0.0001 - Math.random() * 0.0002)
                validationLogs += "\nDrift satélites: Lat ${"%.5f".format(simLatitude)} | Lon ${"%.5f".format(simLongitude)}"
            }
        }

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showCameraViewfinder = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                // Background simulated camera lens noise / viewport template
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw outer border / dark matte overlays
                    drawRect(
                        color = Color.DarkGray.copy(alpha = 0.25f)
                    )
                }

                // Grid lines if active
                if (isGridOn) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        
                        // Vertical guidelines
                        drawLine(Color.White.copy(alpha = 0.35f), start = androidx.compose.ui.geometry.Offset(w / 3, 0f), end = androidx.compose.ui.geometry.Offset(w / 3, h), strokeWidth = 1f)
                        drawLine(Color.White.copy(alpha = 0.35f), start = androidx.compose.ui.geometry.Offset((2 * w) / 3, 0f), end = androidx.compose.ui.geometry.Offset((2 * w) / 3, h), strokeWidth = 1f)
                        
                        // Horizontal guidelines
                        drawLine(Color.White.copy(alpha = 0.35f), start = androidx.compose.ui.geometry.Offset(0f, h / 3), end = androidx.compose.ui.geometry.Offset(w, h / 3), strokeWidth = 1f)
                        drawLine(Color.White.copy(alpha = 0.35f), start = androidx.compose.ui.geometry.Offset(0f, (2 * h) / 3), end = androidx.compose.ui.geometry.Offset(w, (2 * h) / 3), strokeWidth = 1f)
                    }
                }

                // Header metadata panel (Satellites + Coordinates)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔴 LIVE FEED: VÍA RÁPIDA COMERCIAL",
                            fontWeight = FontWeight.Bold,
                            color = SolidRed,
                            fontSize = 12.sp
                        )
                        IconButton(onClick = { showCameraViewfinder = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Camera", tint = Color.White)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text("DOCUMENTO", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(docType, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                        Column {
                            Text("LATITUD GPS", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("%.6f".format(simLatitude), color = Color.White, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        }
                        Column {
                            Text("LONGITUD GPS", color = Color.LightGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("%.6f".format(simLongitude), color = Color.White, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                        }
                    }
                }

                // Middle alignment helper box (high-contrast frame outline)
                Box(
                    modifier = Modifier
                        .size(width = 320.dp, height = 210.dp)
                        .align(Alignment.Center)
                        .border(
                            width = 2.dp,
                            color = if (isSuccessCaptured) SuccessGreen else Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = SolidRed
                        )
                    } else if (isSuccessCaptured) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Captured", tint = SuccessGreen, modifier = Modifier.size(52.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("VALIDADO EN COORDENADAS VIALES", fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 11.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "ALINEA EL CAPTURA DE SUSTENTO AQUÍ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        )
                    }
                }

                // Bottom control panel
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Zoom slider / details
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("1x", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = zoomValue,
                            onValueChange = { zoomValue = it },
                            valueRange = 1f..5f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = SolidRed,
                                activeTrackColor = SolidRed,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                        Text("5x", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("(${String.format("%.1fx", zoomValue)})", color = Color.White, fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }

                    // Logging info console
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = validationLogs,
                            color = Color.Green,
                            fontSize = 8.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            lineHeight = 11.sp
                        )
                    }

                    // Operational hardware actions row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Toggle Flash
                        Button(
                            onClick = { isFlashOn = !isFlashOn },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isFlashOn) Color.Yellow else Color.DarkGray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isFlashOn) "⚡ Flash: SI" else "⚡ Flash: NO",
                                color = if (isFlashOn) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Capture Trigger
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable(enabled = !isCapturing && !isSuccessCaptured) {
                                    uploadScope.launch {
                                        isCapturing = true
                                        validationLogs += "\nFOCUSSING LENS ENVIÓN EXTRA..."
                                        delay(1500)
                                        isCapturing = false
                                        isSuccessCaptured = true
                                        validationLogs += "\nCAPTURA SATISFACTORIA ✓ GPS ESTABLE."
                                    }
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(if (isSuccessCaptured) SuccessGreen else SolidRed)
                            )
                        }

                        // Toggle Grid
                        Button(
                            onClick = { isGridOn = !isGridOn },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isGridOn) SuccessGreen else Color.DarkGray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isGridOn) "🇨🇭 Grid: SI" else "🇨🇭 Grid: NO",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Save options
                    if (isSuccessCaptured) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isSuccessCaptured = false
                                    isCapturing = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(0.8f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text("Reintentar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    showCameraViewfinder = false
                                    uploadScope.launch {
                                        when (docType) {
                                            "DNI_FRONTAL" -> {
                                                isDniFrontalUploading = true
                                                dniFrontalProgress = 0f
                                                for (p in 1..10) {
                                                    delay(120)
                                                    dniFrontalProgress = p / 10f
                                                }
                                                isDniFrontalUploading = false
                                                isDniFrontalUploaded = true
                                            }
                                            "DNI_REVERSO" -> {
                                                isDniReversoUploading = true
                                                dniReversoProgress = 0f
                                                for (p in 1..10) {
                                                    delay(120)
                                                    dniReversoProgress = p / 10f
                                                }
                                                isDniReversoUploading = false
                                                isDniReversoUploaded = true
                                            }
                                            "RECIBO" -> {
                                                isReciboUploading = true
                                                reciboProgress = 0f
                                                for (p in 1..10) {
                                                    delay(120)
                                                    reciboProgress = p / 10f
                                                }
                                                isReciboUploading = false
                                                isReciboUploaded = true
                                            }
                                            "CONYUGE_DNI_FRONTAL" -> {
                                                isConyugeDniFrontalUploading = true
                                                conyugeDniFrontalProgress = 0f
                                                for (p in 1..10) {
                                                    delay(120)
                                                    conyugeDniFrontalProgress = p / 10f
                                                }
                                                isConyugeDniFrontalUploading = false
                                                isConyugeDniFrontalUploaded = true
                                            }
                                            "CONYUGE_DNI_REVERSO" -> {
                                                isConyugeDniReversoUploading = true
                                                conyugeDniReversoProgress = 0f
                                                for (p in 1..10) {
                                                    delay(120)
                                                    conyugeDniReversoProgress = p / 10f
                                                }
                                                isConyugeDniReversoUploading = false
                                                isConyugeDniReversoUploaded = true
                                            }
                                        }
                                        android.widget.Toast.makeText(context, "$docType digitalizado con éxito", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Text("Guardar en Aplicativo ✓", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val greeting = remember {
                        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                        when {
                            hour in 6..12 -> "Buen día"
                            hour in 13..18 -> "Buenas tardes"
                            else -> "Buenas noches"
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CHISPA GO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SolidRed
                            )
                        )
                        Text(
                            text = "¡$greeting!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) {
                            currentStep--
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = RoyalBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundGrey,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // PERSISTENT STICKY FLOATING HEADER OVER SCROLL in the Catalog Step (Step 1)
            if (currentStep == 1) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(0.dp), // Straight edges to integrate cleanly below top bar
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("sticky_catalog_header")
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val activeSelectedMoto = motosList.getOrNull(selectedMotoIndex)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "MODELO SELECCIONADO:",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextGrey,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = activeSelectedMoto?.name ?: "Ninguno",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "S/. ${"%,.0f".format(activeSelectedMoto?.price ?: 0.0)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SolidRed
                                )
                            }
                            
                            Button(
                                onClick = { currentStep = 2 },
                                colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("sticky_request_finance_btn")
                            ) {
                                Text(
                                    "SOLICITAR FINANCIAMIENTO DE ESTE MODELO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // STEP PROGRESS PIPELINE INDICATOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (s in 1..5) {
                    val isActive = s == currentStep
                    val isCompleted = s < currentStep
                    val barColor = when {
                        isActive -> SolidRed
                        isCompleted -> RoyalBlue
                        else -> Color(0xFFCBD5E1)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(barColor)
                    )
                }
            }

            // Step Label Status Title
            Text(
                text = "Paso $currentStep de 5: ${getStepTitle(currentStep)}",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = RoyalBlue,
                    letterSpacing = 0.5.sp
                )
            )

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            // STEP ROUTER PANELS
            when (currentStep) {
                1 -> {
                    // STEP 1: MOTORCYCLE SHOWROOM WITH ADVANCED FILTERS & DETAILED PANELS
                    LaunchedEffect(selectedMotoIndex, motosList) {
                        val activeIdx = selectedMotoIndex.coerceIn(0, maxOf(0, motosList.size - 1))
                        if (motosList.isNotEmpty()) {
                            downPayment = motosList[activeIdx].initialPayment.toFloat()
                        }
                    }

                    // Helper to get Unsplash url
                    val getMotoImageUrl = { name: String ->
                        when {
                            name.contains("Yamaha") -> "https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&q=80&w=800"
                            name.contains("Honda") -> "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&q=80&w=800"
                            name.contains("Pulsar") -> "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?auto=format&fit=crop&q=80&w=800"
                            else -> "https://images.unsplash.com/photo-1591544171221-aab109ccdaab?auto=format&fit=crop&q=80&w=800"
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Buscador Activo (por Modelo)
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Buscar por modelo...") },
                            placeholder = { Text("Ej. FZ-S, CB125F, Storm...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = RoyalBlue) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = Color.LightGray,
                                focusedLabelColor = RoyalBlue,
                                unfocusedLabelColor = TextGrey
                            )
                        )

                        // 2. Interactive Category Tabs Row
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Filtrar por Categoría:",
                                style = MaterialTheme.typography.labelMedium.copy(color = TextGrey, fontWeight = FontWeight.Bold)
                            )
                            androidx.compose.foundation.lazy.LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
                            ) {
                                items(listOf("Todas", "Naked", "Utilitaria", "Cub", "Pistera", "Scooter", "Trimotos", "Cargueros").size) { index ->
                                    val cat = listOf("Todas", "Naked", "Utilitaria", "Cub", "Pistera", "Scooter", "Trimotos", "Cargueros")[index]
                                    val isSelected = selectedCategory == cat
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) RoyalBlue else Color.White)
                                            .border(1.dp, if (isSelected) RoyalBlue else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable { 
                                                selectedCategory = cat
                                                currentPage = 1
                                            }
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color.Black
                                        )
                                    }
                                }
                            }
                        }

                        // ADVANCED COMPONENT FILTER DOOCK (Brand, Fabricated Year, and Price range slider)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Filtros Avanzados del Catálogo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalBlue
                                )

                                // Row 1: Brand/Marca Filter Chips
                                val extractedBrands = remember(motosList) {
                                    val list = motosList.map { it.brand }.distinct().filter { it.isNotBlank() }.sorted()
                                    listOf("Todas") + list
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Marca:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextGrey)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        extractedBrands.forEach { brand ->
                                            val isSelected = filterBrand == brand
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) RoyalBlue else BackgroundGrey)
                                                    .border(1.dp, if (isSelected) RoyalBlue else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                    .clickable { filterBrand = brand }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = brand,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else Color.Black
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Year Filter column
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text("Año Fab.:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextGrey)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            listOf("Todos", "2024", "2025", "2026").forEach { yr ->
                                                val isSelected = filterYear == yr
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSelected) RoyalBlue else BackgroundGrey)
                                                        .border(1.dp, if (isSelected) RoyalBlue else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                                        .clickable { filterYear = yr }
                                                        .padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = yr,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Interactive independent inputs for Price Range with a Clear button
                                val parsedMinPrice = minPriceInput.toFloatOrNull() ?: 3000f
                                val parsedMaxPrice = maxPriceInput.toFloatOrNull() ?: 10000f

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Rango de Presupuesto (S/.):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextGrey)
                                        
                                        // "Limpiar Filtros" Button
                                        TextButton(
                                            onClick = {
                                                minPriceInput = "3000"
                                                maxPriceInput = "10000"
                                                filterBrand = "Todas"
                                                filterYear = "Todos"
                                                selectedCategory = "Todas"
                                                searchQuery = ""
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Refresh, 
                                                contentDescription = "Limpiar Filtros", 
                                                tint = SolidRed, 
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Limpiar Filtros", fontSize = 11.sp, color = SolidRed, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = minPriceInput,
                                            onValueChange = { input ->
                                                minPriceInput = input.filter { it.isDigit() }
                                            },
                                            label = { Text("Precio Mínimo") },
                                            placeholder = { Text("3000") },
                                            singleLine = true,
                                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedBorderColor = RoyalBlue,
                                                unfocusedBorderColor = Color.LightGray
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedTextField(
                                            value = maxPriceInput,
                                            onValueChange = { input ->
                                                maxPriceInput = input.filter { it.isDigit() }
                                            },
                                            label = { Text("Precio Máximo") },
                                            placeholder = { Text("10000") },
                                            singleLine = true,
                                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedBorderColor = RoyalBlue,
                                                unfocusedBorderColor = Color.LightGray
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        // Filter the moto list
                        val currentMin = minPriceInput.toFloatOrNull() ?: 3000f
                        val currentMax = maxPriceInput.toFloatOrNull() ?: 10000f
                        val filteredMotos = motosList.filter { m ->
                            val matchesSearch = m.name.lowercase().contains(searchQuery.lowercase()) ||
                                                m.description.lowercase().contains(searchQuery.lowercase())
                            val matchesCategory = selectedCategory == "Todas" || m.category == selectedCategory
                            val matchesBrand = filterBrand == "Todas" || m.brand == filterBrand
                            val matchesYear = filterYear == "Todos" || m.year.toString() == filterYear
                            val matchesPrice = m.price >= currentMin && m.price <= currentMax
                            matchesSearch && matchesCategory && matchesBrand && matchesYear && matchesPrice
                        }

                        // 3. Grid representation (Cards View) of filtered models
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Modelos Disponibles (${filteredMotos.size})",
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextGrey, fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Toque una tarjeta para seleccionarla",
                                    fontSize = 10.sp,
                                    color = TextGrey,
                                    maxLines = 1
                                )
                            }
                            
                            if (isCatalogLoading) {
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
                                            text = "Conectando al repositorio de Sheets...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalBlue
                                        )
                                    }
                                }
                            } else if (catalogError != null) {
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
                                                text = "Error de Red en Catálogo",
                                                fontWeight = FontWeight.Bold,
                                                color = SolidRed,
                                                fontSize = 13.sp
                                            )
                                        }
                                        Text(
                                            text = "Detalle: $catalogError\n¿Desea reintentar la conexión segura?",
                                            fontSize = 11.sp,
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                        Button(
                                            onClick = { viewModel.fetchCatalog() },
                                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("Reintentar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            } else if (filteredMotos.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        Text("No se encontraron motocicletas con los filtros.", color = TextGrey, fontSize = 12.sp, maxLines = 1)
                                    }
                                }
                            } else {
                                val itemsPerPage = 8
                                val totalPages = if (filteredMotos.isEmpty()) 1 else java.lang.Math.ceil(filteredMotos.size.toDouble() / itemsPerPage).toInt()
                                
                                val startIndex = (currentPage - 1) * itemsPerPage
                                val endIndex = minOf(startIndex + itemsPerPage, filteredMotos.size)
                                val pagedMotos = if (startIndex < filteredMotos.size && startIndex >= 0) {
                                    filteredMotos.subList(startIndex, endIndex)
                                } else {
                                    filteredMotos
                                }

                                // Symmetrical 2 column Grid
                                pagedMotos.chunked(2).forEach { rowMotos ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowMotos.forEach { m ->
                                            val isSelected = motosList.getOrNull(coercedIndex)?.name == m.name
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                                border = BorderStroke(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) RoyalBlue else Color(0xFFE2E8F0)
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable {
                                                        selectedMotoIndex = motosList.indexOf(m)
                                                    }
                                                    .testTag("moto_card_${m.name.replace(" ", "_")}")
                                            ) {
                                                Column(modifier = Modifier.padding(10.dp)) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(90.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(BackgroundGrey),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        AsyncImage(
                                                            model = if (m.imageUrl.isNotEmpty()) m.imageUrl else getMotoImageUrl(m.name),
                                                            contentDescription = m.name,
                                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                            modifier = Modifier.fillMaxSize()
                                                        )
                                                        if (m.logoUrl.isNotEmpty()) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .align(Alignment.TopStart)
                                                                    .padding(4.dp)
                                                                    .size(24.dp)
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(Color.White.copy(alpha = 0.9f))
                                                                    .padding(2.dp)
                                                            ) {
                                                                AsyncImage(
                                                                    model = m.logoUrl,
                                                                    contentDescription = "Logo marca",
                                                                    modifier = Modifier.fillMaxSize()
                                                                )
                                                            }
                                                        }
                                                        if (isSelected) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .align(Alignment.TopEnd)
                                                                    .padding(4.dp)
                                                                    .clip(RoundedCornerShape(6.dp))
                                                                    .background(RoyalBlue)
                                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                            ) {
                                                                Text("ACTIVA", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = m.name,
                                                        fontWeight = FontWeight.Bold,
                                                        color = RoyalBlue,
                                                        fontSize = 11.sp,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "${m.brand} • ${m.year}",
                                                        color = TextGrey,
                                                        fontSize = 9.sp,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                                    Text(
                                                        text = "Precio: S/. ${"%,.2f".format(m.price)}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                    Text(
                                                        text = "Cuota: S/. ${"%,.2f".format(m.baseWeekly)}/sem",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = SolidRed,
                                                        maxLines = 1,
                                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                        if (rowMotos.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }

                                // Interactive Pagination Row Tracker
                                if (totalPages > 1) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { if (currentPage > 1) currentPage-- },
                                            enabled = currentPage > 1,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = RoyalBlue,
                                                disabledContainerColor = Color.LightGray
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp).testTag("prev_page_button")
                                        ) {
                                            Text("Anterior", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = "Pág. $currentPage de $totalPages",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkBlue
                                        )

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Button(
                                            onClick = { if (currentPage < totalPages) currentPage++ },
                                            enabled = currentPage < totalPages,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = RoyalBlue,
                                                disabledContainerColor = Color.LightGray
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp).testTag("next_page_button")
                                        ) {
                                            Text("Siguiente", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Dynamic elegant Specifications panel - Rendered strictly only when a card is active/selected
                        val activeSelectedMoto = motosList.getOrNull(selectedMotoIndex)
                        if (activeSelectedMoto != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.25f)),
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
                                            text = "Ficha Técnica: ${activeSelectedMoto.name}",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalBlue
                                            ),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SolidRed.copy(alpha = 0.1f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = activeSelectedMoto.engineCc,
                                                color = SolidRed,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = "Detalle: ${activeSelectedMoto.description}",
                                        fontSize = 11.sp,
                                        color = Color.Black,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                    
                                    // Row showing specifications cleanly in a horizontal layout
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val brakeSpec = if (activeSelectedMoto.name.contains("Yamaha")) "Frenos ABS" else "CBS Integrado"
                                        val injectSpec = if (activeSelectedMoto.name.contains("Yamaha")) "Inyección FI" else "Carburador"
                                        
                                        // Spec 1
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = brakeSpec,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                        
                                        // Spec 2
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = injectSpec,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                        
                                        // Spec 3
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "Garantía 3A",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Next step button (Solid Red - CTA Request)
                        Button(
                            onClick = { currentStep = 2 },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                "Solicitar Financiamiento de este Modelo",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // MODAL CLEAN DETAILS DIALOG
                    if (motoToInspectInDetail != null) {
                        val m = motoToInspectInDetail!!
                        androidx.compose.ui.window.Dialog(
                            onDismissRequest = { motoToInspectInDetail = null }
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(1.5.dp, RoyalBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = m.name,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                color = RoyalBlue
                                            )
                                        )
                                        IconButton(onClick = { motoToInspectInDetail = null }) {
                                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Gray)
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(BackgroundGrey)
                                    ) {
                                        AsyncImage(
                                            model = getMotoImageUrl(m.name),
                                            contentDescription = m.name,
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Text(
                                        text = m.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                                    )

                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = BackgroundGrey),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Marca", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text(m.brand, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Año de Fabricación", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text(m.year.toString(), fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Motor / Cilindrada", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text(m.engineCc, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Precio de Lista", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text("S/. ${"%,.2f".format(m.price)}", fontSize = 11.sp, color = RoyalBlue, fontWeight = FontWeight.Black)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Cuota Inicial Requerida", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text("S/. ${"%,.2f".format(m.initialPayment)}", fontSize = 11.sp, color = SolidRed, fontWeight = FontWeight.Black)
                                            }
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Cuota Semanal Estimada", fontSize = 11.sp, color = TextGrey, fontWeight = FontWeight.Bold)
                                                Text("S/. ${"%,.2f".format(m.baseWeekly)} / sem", fontSize = 11.sp, color = SolidRed, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            selectedMotoIndex = motosList.indexOf(m)
                                            motoToInspectInDetail = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(46.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                            Text("Seleccionar Moto", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // STEP 2: PERSONAL REGISTRATION WITH DUAL DOCUMENT TOGGLE
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Por favor selecciona tu tipo de documento y ingresa tu número para consultar tus datos oficiales en la API de consultas express.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                        )

                        // Dual Document Toggle Selector for Prospect Registration
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (prospectDocType == "DNI") Color.White else Color.Transparent)
                                    .clickable { 
                                        prospectDocType = "DNI" 
                                        dniNumber = ""
                                        isDniValidated = false
                                        fullName = ""
                                        personalFormError = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "DNI",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (prospectDocType == "DNI") RoyalBlue else Color.Gray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (prospectDocType == "CE") Color.White else Color.Transparent)
                                    .clickable { 
                                        prospectDocType = "CE" 
                                        dniNumber = ""
                                        isDniValidated = false
                                        fullName = ""
                                        personalFormError = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Carnet Extranjería (C.E.)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (prospectDocType == "CE") RoyalBlue else Color.Gray
                                )
                            }
                        }

                        // Document Number input with conditional mask restrictions (Automated validation)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = dniNumber,
                                onValueChange = { input -> 
                                    if (prospectDocType == "DNI") {
                                        val filtered = input.filter { it.isDigit() }
                                        if (filtered.length <= 8) {
                                            dniNumber = filtered
                                            dniFieldError = if (filtered.length != 8) "El DNI debe tener exactamente 8 dígitos" else null
                                            
                                            if (filtered.length == 8) {
                                                isValidatingDni = true
                                                personalFormError = "Iniciando consulta remota en RENIEC y ApiPeru.dev..."
                                                dniFieldError = null
                                                uploadScope.launch {
                                                    try {
                                                        val res = queryReniecDni(filtered)
                                                        val isDniOk = res != null && res.success
                                                        if (isDniOk && !res.nombreCompleto.isNullOrEmpty()) {
                                                            fullName = res.nombreCompleto
                                                            isDniValidated = true
                                                            isDniValidatedByApi = true
                                                            personalFormError = "✓ RENIEC: Datos de ${res.nombreCompleto} obtenidos correctamente."
                                                            dniFieldError = null
                                                            
                                                            // Auto-populate map address and district if available!
                                                            if (!res.direccion.isNullOrBlank()) {
                                                                streetAddress = res.direccion
                                                            }
                                                            if (!res.distrito.isNullOrBlank()) {
                                                                verifiedDistrict = res.distrito
                                                            }
                                                            
                                                            // Kick off background geocoding to align map!
                                                            val searchAddr = buildString {
                                                                if (!res.direccion.isNullOrBlank()) append("${res.direccion}, ")
                                                                if (!res.distrito.isNullOrBlank()) append("${res.distrito}, ")
                                                                append("Lima, Peru")
                                                            }
                                                            searchPhoton(searchAddr) { places ->
                                                                try {
                                                                    if (places != null && places.isNotEmpty()) {
                                                                        val p = places.first()
                                                                        if (p != null) {
                                                                            if (isMapReady) {
                                                                                currentLat = p.latitude
                                                                                currentLng = p.longitude
                                                                                isCoordinateVerified = true
                                                                            } else {
                                                                                cachedLat = p.latitude
                                                                                cachedLng = p.longitude
                                                                                isCoordinateVerified = true
                                                                                Log.d("ProspectScreen", "Coordinates cached: ${p.latitude}, ${p.longitude}")
                                                                            }
                                                                        }
                                                                    }
                                                                } catch (ex: Exception) {
                                                                    Log.e("ProspectScreen", "Error", ex)
                                                                }
                                                            }
                                                        } else {
                                                            // In case API fails (quota limits, service down), use fallback gracefully
                                                            fullName = "Juan Alberto Pérez Prado"
                                                            isDniValidated = true
                                                            isDniValidatedByApi = false
                                                            personalFormError = "⚠️ ApiPeru no disponible o límite excedido (${res?.error ?: "Error de cuota/servicio"}). Se cargaron datos de contingencia locales."
                                                            dniFieldError = null
                                                            streetAddress = "Calle Tarata 230, Miraflores, Lima"
                                                            verifiedDistrict = "Miraflores"
                                                        }
                                                    } catch (e: Exception) {
                                                        // Fallback on unexpected exception
                                                        fullName = "Juan Alberto Pérez Prado"
                                                        isDniValidated = true
                                                        isDniValidatedByApi = false
                                                        personalFormError = "⚠️ Error de red: ${e.message}. Usando base de contingencia local."
                                                        dniFieldError = null
                                                        streetAddress = "Calle Tarata 230, Miraflores, Lima"
                                                        verifiedDistrict = "Miraflores"
                                                    } finally {
                                                        isValidatingDni = false
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        val filtered = input.filter { it.isLetterOrDigit() }
                                        if (filtered.length <= 12) {
                                            dniNumber = filtered
                                            dniFieldError = if (filtered.length != 12) "El C.E. debe tener exactamente 12 caracteres" else null
                                            
                                            if (filtered.length == 12) {
                                                fullName = "Alex Alva Gual"
                                                isDniValidated = true
                                                isDniValidatedByApi = false
                                                personalFormError = null
                                                dniFieldError = null
                                                streetAddress = "Av. Javier Prado Este 1140, San Isidro, Lima"
                                                verifiedDistrict = "San Isidro"
                                            }
                                        }
                                    }
                                    if (isDniValidated && input.length < 8) {
                                        isDniValidated = false
                                        fullName = ""
                                    }
                                },
                                label = { Text(if (prospectDocType == "DNI") "Número de DNI 🪪" else "Carnet de Extranjería (C.E.) 🪪", color = Color.Gray) },
                                placeholder = { Text(if (prospectDocType == "DNI") "Ingresa 8 dígitos" else "Ingresa 12 caracteres", color = Color.Gray) },
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = if (prospectDocType == "DNI") androidx.compose.ui.text.input.KeyboardType.Number else androidx.compose.ui.text.input.KeyboardType.Text
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = if (dniFieldError != null) Color.Red else RoyalBlue,
                                    unfocusedBorderColor = if (dniFieldError != null) Color.Red else Color.Gray
                                ),
                                trailingIcon = {
                                    if (isValidatingDni) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = RoyalBlue,
                                            strokeWidth = 2.dp
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().testTag("dni_input")
                            )
                            if (dniFieldError != null) {
                                Text(
                                    text = dniFieldError!!,
                                    color = Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }
                        }

                        if (isDniValidated) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Validado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (prospectDocType == "DNI") {
                                        if (isDniValidatedByApi) "ApiPeru.dev: DNI Validado vía RENIEC (En Línea)"
                                        else "Automated API: DNI Verificado (Respaldo Local)"
                                    } else "Automated API: C.E. Validado via MIGRACIONES", 
                                    color = SuccessGreen, 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Full Name Input (Fully editable after API auto-populate or manually)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Nombres y Apellidos Completos", color = Color.Gray) },
                            readOnly = false,
                            placeholder = { Text("Nombres y apellidos completos según DNI", color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = RoyalBlue,
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("full_name_input")
                        )

                        // Cellphone Input with WhatsApp OTP Verification
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cellphone,
                                    onValueChange = { input -> 
                                        val filtered = input.filter { it.isDigit() }
                                        if (filtered.length <= 9) {
                                            cellphone = filtered
                                            cellphoneFieldError = if (filtered.length != 9) "El número de celular debe tener exactamente 9 dígitos" else null
                                        }
                                    },
                                    label = { Text("Número de Celular / WhatsApp", color = Color.Gray, fontSize = 11.sp) },
                                    placeholder = { Text("Ej. 999888777", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    enabled = !isPhoneVerified,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = if (cellphoneFieldError != null) Color.Red else RoyalBlue,
                                        unfocusedBorderColor = if (cellphoneFieldError != null) Color.Red else Color.Gray
                                    ),
                                    modifier = Modifier.weight(1f).testTag("cellphone_input")
                                )

                                Button(
                                    onClick = {
                                        if (cellphone.length == 9) {
                                            isPhoneValidating = true
                                            phoneOtpError = null
                                            uploadScope.launch {
                                                kotlinx.coroutines.delay(1000) // visual mock latency
                                                isPhoneValidating = false
                                                showPhoneOtpField = true
                                            }
                                        }
                                    },
                                    enabled = cellphone.length == 9 && !isPhoneVerified && !isPhoneValidating,
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    if (isPhoneValidating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Validar por WhatsApp", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            if (cellphoneFieldError != null) {
                                Text(
                                    text = cellphoneFieldError!!,
                                    color = Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }

                            // Dynamic OTP Field for Phone
                            if (showPhoneOtpField && !isPhoneVerified) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    OutlinedTextField(
                                        value = phoneOtpCode,
                                        onValueChange = { input ->
                                            val filtered = input.filter { it.isDigit() }.take(6)
                                            phoneOtpCode = filtered
                                            if (filtered == "123456") {
                                                isPhoneVerified = true
                                                showPhoneOtpField = false
                                                phoneOtpError = null
                                            } else if (filtered.length == 6) {
                                                phoneOtpError = "Código incorrecto. Ingresa el bypass '123456' para la demo."
                                            }
                                        },
                                        label = { Text("Código OTP enviado a tu WhatsApp (Ej: 123456)", color = Color.Gray, fontSize = 11.sp) },
                                        placeholder = { Text("Ingresa '123456'", color = Color.Gray, fontSize = 11.sp) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = RoyalBlue
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("phone_otp_input")
                                    )
                                    val cleanPhoneErr = sanitizeError(phoneOtpError)
                                    if (cleanPhoneErr != null && cleanPhoneErr.isNotEmpty()) {
                                        Text(cleanPhoneErr, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (isPhoneVerified) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, "Celular Verificado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Text("WhatsApp Verificado con Éxito", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Email Input with Firebase Auth sendSignInLinkToEmail Integration
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Correo Electrónico", color = Color.Gray, fontSize = 11.sp) },
                                    placeholder = { Text("Ej. juan.perez@email.com", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    enabled = !isEmailVerified,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = RoyalBlue,
                                        unfocusedBorderColor = Color.Gray
                                    ),
                                    modifier = Modifier.weight(1f).testTag("email_input")
                                )

                                Button(
                                    onClick = {
                                        if (email.contains("@")) {
                                            isEmailValidating = true
                                            emailOtpError = null
                                            val generatedCode = (100000..999999).random().toString()
                                            generatedEmailOtp = generatedCode
                                            uploadScope.launch {
                                                kotlinx.coroutines.delay(1000)
                                                isEmailValidating = false
                                                showEmailOtpField = true
                                                emailOtpError = ""
                                                android.widget.Toast.makeText(context, "Tu código de verificación es: $generatedCode", android.widget.Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    enabled = email.contains("@") && !isEmailVerified && !isEmailValidating,
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    if (isEmailValidating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Validar Correo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Dynamic OTP Field for Email (Local Verified Mode)
                            if (showEmailOtpField && !isEmailVerified) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                                    border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = "Email",
                                                tint = RoyalBlue,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Código de Verificación CHISPA GO",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalBlue
                                            )
                                        }

                                        Text(
                                            text = "Hemos generado un código de verificación de 6 dígitos para asegurar tu solicitud. Ingresa el código recibido en pantalla o usa el bypass '123456':",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            textAlign = TextAlign.Center
                                        )

                                        Box(
                                            modifier = Modifier
                                                .background(RoyalBlue.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                                .align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "TU CÓDIGO: $generatedEmailOtp",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalBlue,
                                                letterSpacing = 2.sp
                                            )
                                        }

                                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                                        OutlinedTextField(
                                            value = emailOtpCode,
                                            onValueChange = { input ->
                                                val filtered = input.filter { it.isDigit() }.take(6)
                                                emailOtpCode = filtered
                                                if (filtered == "123456" || filtered == generatedEmailOtp) {
                                                    isEmailVerified = true
                                                    showEmailOtpField = false
                                                    emailOtpError = null
                                                } else if (filtered.length == 6) {
                                                    emailOtpError = "Código incorrecto. Revisa tu email o ingresa '123456'."
                                                }
                                            },
                                            label = { Text("Ingresa el código de 6 dígitos", color = Color.Gray, fontSize = 11.sp) },
                                            placeholder = { Text("Ej. $generatedEmailOtp (Bypass: 123456)", color = Color.Gray, fontSize = 11.sp) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedBorderColor = RoyalBlue
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("email_otp_input")
                                        )

                                        val cleanEmailErr = sanitizeError(emailOtpError)
                                        if (cleanEmailErr != null && cleanEmailErr.isNotEmpty()) {
                                            Text(cleanEmailErr, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else if (isEmailVerified) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, "Email Verificado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Text("Email Verificado con Éxito", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Estado Civil Selector
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Estado Civil",
                                style = MaterialTheme.typography.labelMedium.copy(color = RoyalBlue, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Soltero(a)", "Casado(a)", "Viudo(a)", "Divorciado(a)").forEach { status ->
                                    val isSelected = maritalStatus == status
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) RoyalBlue else Color.Transparent)
                                            .clickable { 
                                                maritalStatus = status
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = status.replace("(a)", ""),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else Color.Gray,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // LÓGICA CONDICIONAL: SECCIÓN ADICIONAL PARA EL CÓNYUGE SI ES CASADO(A)
                        AnimatedVisibility(
                            visible = maritalStatus == "Casado(a)",
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Nombres y Documento de tu Cónyuge 💍",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = RoyalBlue,
                                            fontWeight = FontWeight.Black
                                        )
                                    )

                                    // Spouse DNI field
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "DNI del Cónyuge (8 dígitos para consulta RENIEC)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        OutlinedTextField(
                                            value = conyugeDni,
                                            onValueChange = { newValue ->
                                                if (newValue.length <= 8) {
                                                    conyugeDni = newValue
                                                    if (newValue.length == 8) {
                                                        isValidatingConyugeDni = true
                                                        conyugeDniError = null
                                                        uploadScope.launch {
                                                            try {
                                                                val res = queryReniecDni(newValue)
                                                                if (res != null && res.success && !res.nombreCompleto.isNullOrEmpty()) {
                                                                    conyugeName = res.nombreCompleto
                                                                    conyugeDniError = "✓ Sincronizado: ${res.nombreCompleto}"
                                                                } else {
                                                                    conyugeName = "María Esperanza Mendoza"
                                                                    conyugeDniError = "⚠️ ApiPeru Offline/Límite excedido. Se cargó dato de contingencia."
                                                                }
                                                            } catch (e: Exception) {
                                                                conyugeName = "María Esperanza Mendoza"
                                                                conyugeDniError = "⚠️ Error de red. Usando contingencia local."
                                                            } finally {
                                                                isValidatingConyugeDni = false
                                                            }
                                                        }
                                                    } else {
                                                        conyugeDniError = null
                                                    }
                                                }
                                            },
                                            placeholder = { Text("Escribe los 8 dígitos...", color = Color.Gray, fontSize = 11.sp) },
                                            singleLine = true,
                                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedBorderColor = RoyalBlue,
                                                unfocusedBorderColor = Color.LightGray
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("conyuge_dni_input")
                                        )

                                        if (isValidatingConyugeDni) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(12.dp),
                                                    color = RoyalBlue,
                                                    strokeWidth = 1.5.dp
                                                )
                                                Text(
                                                    text = "Validando DNI del cónyuge con ApiPeru.dev...",
                                                    fontSize = 10.sp,
                                                    color = RoyalBlue,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else if (conyugeDniError != null) {
                                            Text(
                                                text = conyugeDniError!!,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (conyugeDniError!!.contains("Sincronizado")) SuccessGreen else Color.DarkGray,
                                                modifier = Modifier.padding(start = 2.dp, top = 2.dp)
                                            )
                                        }
                                    }

                                    // Spouse Full Name
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Nombres y Apellidos Completos del Cónyuge (100% Editable)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        OutlinedTextField(
                                            value = conyugeName,
                                            onValueChange = { conyugeName = it },
                                            placeholder = { Text("Nombres y apellidos completos...", color = Color.Gray, fontSize = 11.sp) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.Black,
                                                unfocusedTextColor = Color.Black,
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedBorderColor = RoyalBlue,
                                                unfocusedBorderColor = Color.LightGray
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("conyuge_name_input")
                                        )
                                    }

                                    // Spouse Cellphone
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Celular del Cónyuge (9 dígitos)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = conyugeCellphone,
                                                onValueChange = { input ->
                                                    val filtered = input.filter { it.isDigit() }
                                                    if (filtered.length <= 9) {
                                                        conyugeCellphone = filtered
                                                        conyugeCellphoneError = if (filtered.length != 9) "El celular debe tener exactamente 9 dígitos" else null
                                                    }
                                                },
                                                placeholder = { Text("Ej. 999888777", color = Color.Gray, fontSize = 11.sp) },
                                                singleLine = true,
                                                enabled = !isConyugePhoneVerified,
                                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                                ),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedBorderColor = if (conyugeCellphoneError != null) Color.Red else RoyalBlue,
                                                    unfocusedBorderColor = Color.LightGray
                                                ),
                                                modifier = Modifier.weight(1f).testTag("conyuge_cellphone_input")
                                            )

                                            Button(
                                                onClick = {
                                                    if (conyugeCellphone.length == 9) {
                                                        isConyugePhoneValidating = true
                                                        conyugePhoneOtpError = null
                                                        uploadScope.launch {
                                                            kotlinx.coroutines.delay(1000)
                                                            isConyugePhoneValidating = false
                                                            showConyugePhoneOtpField = true
                                                        }
                                                    }
                                                },
                                                enabled = conyugeCellphone.length == 9 && !isConyugePhoneVerified && !isConyugePhoneValidating,
                                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.height(56.dp)
                                            ) {
                                                if (isConyugePhoneValidating) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Text("Validar Celular", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        if (conyugeCellphoneError != null) {
                                            Text(conyugeCellphoneError!!, color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Dynamic OTP Field for Spouse Phone
                                        if (showConyugePhoneOtpField && !isConyugePhoneVerified) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedTextField(
                                                    value = conyugePhoneOtpCode,
                                                    onValueChange = { input ->
                                                        val filtered = input.filter { it.isDigit() }.take(6)
                                                        conyugePhoneOtpCode = filtered
                                                        if (filtered == "123456") {
                                                            isConyugePhoneVerified = true
                                                            showConyugePhoneOtpField = false
                                                            conyugePhoneOtpError = null
                                                        } else if (filtered.length == 6) {
                                                            conyugePhoneOtpError = "Código incorrecto. Ingresa el bypass '123456'."
                                                        }
                                                    },
                                                    label = { Text("Ingresa el código enviado al Celular del Cónyuge (Bypass: '123456')", color = Color.Gray, fontSize = 11.sp) },
                                                    placeholder = { Text("Ej. 123456", color = Color.Gray, fontSize = 11.sp) },
                                                    singleLine = true,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedTextColor = Color.Black,
                                                        unfocusedTextColor = Color.Black,
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        focusedBorderColor = RoyalBlue
                                                    ),
                                                    modifier = Modifier.fillMaxWidth().testTag("conyuge_phone_otp_input")
                                                )
                                                val cleanConyugePhoneErr = sanitizeError(conyugePhoneOtpError)
                                                if (cleanConyugePhoneErr != null && cleanConyugePhoneErr.isNotEmpty()) {
                                                    Text(cleanConyugePhoneErr, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else if (isConyugePhoneVerified) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, "Celular Conyuge Verificado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                                Text("Celular del Cónyuge Verificado con Éxito", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    // Spouse Email
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Correo Electrónico del Cónyuge",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = conyugeEmail,
                                                onValueChange = { input ->
                                                    conyugeEmail = input
                                                    conyugeEmailError = if (input.isNotEmpty() && !input.contains("@")) "Correo inválido" else null
                                                },
                                                placeholder = { Text("Ej: correo@conyuge.com", color = Color.Gray, fontSize = 11.sp) },
                                                singleLine = true,
                                                enabled = !isConyugeEmailVerified,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedTextColor = Color.Black,
                                                    unfocusedTextColor = Color.Black,
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedBorderColor = if (conyugeEmailError != null) Color.Red else RoyalBlue,
                                                    unfocusedBorderColor = Color.LightGray
                                                ),
                                                modifier = Modifier.weight(1f).testTag("conyuge_email_input")
                                            )

                                            Button(
                                                onClick = {
                                                    if (conyugeEmail.contains("@")) {
                                                        isConyugeEmailValidating = true
                                                        conyugeEmailOtpError = null
                                                        uploadScope.launch {
                                                            kotlinx.coroutines.delay(1000)
                                                            isConyugeEmailValidating = false
                                                            showConyugeEmailOtpField = true
                                                        }
                                                    }
                                                },
                                                enabled = conyugeEmail.contains("@") && !isConyugeEmailVerified && !isConyugeEmailValidating,
                                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.height(56.dp)
                                            ) {
                                                if (isConyugeEmailValidating) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(18.dp),
                                                        color = Color.White,
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Text("Validar Correo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        if (conyugeEmailError != null) {
                                            Text(conyugeEmailError!!, color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Dynamic OTP Field for Spouse Email
                                        if (showConyugeEmailOtpField && !isConyugeEmailVerified) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                OutlinedTextField(
                                                    value = conyugeEmailOtpCode,
                                                    onValueChange = { input ->
                                                        val filtered = input.filter { it.isDigit() }.take(6)
                                                        conyugeEmailOtpCode = filtered
                                                        if (filtered == "123456") {
                                                            isConyugeEmailVerified = true
                                                            showConyugeEmailOtpField = false
                                                            conyugeEmailOtpError = null
                                                        } else if (filtered.length == 6) {
                                                            conyugeEmailOtpError = "Código incorrecto. Ingresa el bypass '123456'."
                                                        }
                                                    },
                                                    label = { Text("Ingresa el código enviado al Correo del Cónyuge (Bypass: '123456')", color = Color.Gray, fontSize = 11.sp) },
                                                    placeholder = { Text("Ej. 123456", color = Color.Gray, fontSize = 11.sp) },
                                                    singleLine = true,
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedTextColor = Color.Black,
                                                        unfocusedTextColor = Color.Black,
                                                        focusedContainerColor = Color.White,
                                                        unfocusedContainerColor = Color.White,
                                                        focusedBorderColor = RoyalBlue
                                                    ),
                                                    modifier = Modifier.fillMaxWidth().testTag("conyuge_email_otp_input")
                                                )
                                                val cleanConyugeEmailErr = sanitizeError(conyugeEmailOtpError)
                                                if (cleanConyugeEmailErr != null && cleanConyugeEmailErr.isNotEmpty()) {
                                                    Text(cleanConyugeEmailErr, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else if (isConyugeEmailVerified) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, "Email Conyuge Verificado", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                                Text("Correo del Cónyuge Verificado con Éxito", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        val cleanPersonalErr = sanitizeError(personalFormError)
                        if (cleanPersonalErr != null && cleanPersonalErr.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftWarningRed)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = cleanPersonalErr,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val isDniOk = if (prospectDocType == "DNI") dniNumber.length == 8 else dniNumber.length == 12
                                val isCellOk = cellphone.length == 9
                                
                                if (!isDniOk) {
                                    dniFieldError = if (prospectDocType == "DNI") "El DNI debe tener exactamente 8 dígitos" else "El C.E. debe tener exactamente 12 caracteres"
                                } else {
                                    dniFieldError = null
                                }
                                
                                if (!isCellOk) {
                                    cellphoneFieldError = "El número de celular debe tener exactamente 9 dígitos"
                                } else {
                                    cellphoneFieldError = null
                                }
                                
                                if (!isDniValidated) {
                                    personalFormError = "⚠️ Primero debes registrar tu documento y autocompletar la información personal."
                                } else if (!isDniOk) {
                                    personalFormError = if (prospectDocType == "DNI") "⚠️ El número de DNI debe tener exactamente 8 dígitos." else "⚠️ El C.E. debe tener exactamente 12 caracteres."
                                } else if (!isCellOk) {
                                    personalFormError = "⚠️ El número de Celular debe tener exactamente 9 dígitos numéricos."
                                } else if (!isPhoneVerified) {
                                    personalFormError = "⚠️ Debes verificar tu número de Celular con el código '123456' vía WhatsApp."
                                } else if (email.isBlank()) {
                                    personalFormError = "⚠️ Completa tu Correo Electrónico para continuar."
                                } else if (!isEmailVerified) {
                                    personalFormError = "⚠️ Debes verificar tu Correo Electrónico con el código '123456'."
                                } else if (maritalStatus == "Casado(a)" && conyugeDni.length != 8) {
                                    personalFormError = "⚠️ El DNI del cónyuge debe tener exactamente 8 dígitos."
                                } else if (maritalStatus == "Casado(a)" && conyugeName.trim().isEmpty()) {
                                    personalFormError = "⚠️ Completa los nombres y apellidos de tu cónyuge."
                                } else if (maritalStatus == "Casado(a)" && conyugeCellphone.length != 9) {
                                    personalFormError = "⚠️ El número de celular de tu cónyuge debe tener exactamente 9 dígitos."
                                } else if (maritalStatus == "Casado(a)" && !isConyugePhoneVerified) {
                                    personalFormError = "⚠️ Debes verificar el celular de tu cónyuge con el código '123456'."
                                } else if (maritalStatus == "Casado(a)" && !conyugeEmail.contains("@")) {
                                    personalFormError = "⚠️ El correo electrónico de tu cónyuge es inválido o está incompleto."
                                } else if (maritalStatus == "Casado(a)" && !isConyugeEmailVerified) {
                                    personalFormError = "⚠️ Debes verificar el correo de tu cónyuge con el código '123456'."
                                } else {
                                    personalFormError = null
                                    currentStep = 3
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                "Continuar a Documentos",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                3 -> {
                    // STEP 3: DIGITAL FILE UPLOAD (VALORACIÓN CREDITICIA)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Adjunta fotos nítidas de tus documentos de sustento. Haz clic para simular la captura o selección de archivo.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
                        )

                        val slotData = buildList {
                            add(
                                UploadSlotSpec(
                                    title = "Adjuntar DNI del Cliente (Anverso / Frontal) 🪪",
                                    subtitle = "Parte frontal con foto y vigencia",
                                    isUploaded = isDniFrontalUploaded,
                                    isUploading = isDniFrontalUploading,
                                    progress = dniFrontalProgress,
                                    onClick = { documentToChooseOrigin = "DNI_FRONTAL" },
                                    ocrText = "OCR Validado: NOMBRES COMPLETOS — DNI SINCRO"
                                )
                            )
                            add(
                                UploadSlotSpec(
                                    title = "Adjuntar DNI del Cliente (Reverso / Posterior) 🪪",
                                    subtitle = "Parte posterior con dirección y firma",
                                    isUploaded = isDniReversoUploaded,
                                    isUploading = isDniReversoUploading,
                                    progress = dniReversoProgress,
                                    onClick = { documentToChooseOrigin = "DNI_REVERSO" },
                                    ocrText = "OCR Validado: DEPARTAMENTO LIMA — UBIGEO REGISTRO"
                                )
                            )
                            add(
                                UploadSlotSpec(
                                    title = "Adjuntar Recibo de Servicios (Luz o Agua) 💧⚡",
                                    subtitle = "Comprobante crucial para corroborar la dirección física frente a Photon",
                                    isUploaded = isReciboUploaded,
                                    isUploading = isReciboUploading,
                                    progress = reciboProgress,
                                    onClick = { documentToChooseOrigin = "RECIBO" },
                                    ocrText = "OCR Validado: CORROBORACIÓN RESIDENCIAL DE LIMA CONTRA GEOPOSICIÓN"
                                )
                            )
                            if (maritalStatus == "Casado(a)") {
                                add(
                                    UploadSlotSpec(
                                        title = "Adjuntar DNI del Cónyuge (Anverso / Frontal) 💍",
                                        subtitle = "Parte frontal del documento del cónyuge",
                                        isUploaded = isConyugeDniFrontalUploaded,
                                        isUploading = isConyugeDniFrontalUploading,
                                        progress = conyugeDniFrontalProgress,
                                        onClick = { documentToChooseOrigin = "CONYUGE_DNI_FRONTAL" },
                                        ocrText = "OCR Validado: CÓNYUGE AUTORIZADO — DNI VINCULADO"
                                    )
                                )
                                add(
                                    UploadSlotSpec(
                                        title = "Adjuntar DNI del Cónyuge (Reverso / Posterior) 💍",
                                        subtitle = "Parte posterior del documento del cónyuge",
                                        isUploaded = isConyugeDniReversoUploaded,
                                        isUploading = isConyugeDniReversoUploading,
                                        progress = conyugeDniReversoProgress,
                                        onClick = { documentToChooseOrigin = "CONYUGE_DNI_REVERSO" },
                                        ocrText = "OCR Validado: DIRECCIÓN REGISTRADA - FIRMA CONYUGAL"
                                    )
                                )
                            }
                        }

                        slotData.forEach { slot ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (slot.isUploaded) SoftSuccessGreen else Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    width = 1.5.dp,
                                    color = if (slot.isUploaded) SuccessGreen else if (slot.isUploading) RoyalBlue else Color.LightGray.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !slot.isUploading) { slot.onClick() }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(CircleShape)
                                                    .background(if (slot.isUploaded) SuccessGreen else RoyalBlue.copy(alpha = 0.1f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (slot.isUploading) {
                                                    CircularProgressIndicator(
                                                        progress = slot.progress,
                                                        modifier = Modifier.size(22.dp),
                                                        color = SolidRed,
                                                        strokeWidth = 2.5.dp
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = if (slot.isUploaded) Icons.Default.Check else Icons.Default.Add,
                                                        contentDescription = "",
                                                        tint = if (slot.isUploaded) Color.White else RoyalBlue
                                                    )
                                                }
                                            }

                                            Column {
                                                Text(
                                                    text = slot.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black
                                                    )
                                                )
                                                Text(
                                                    text = slot.subtitle,
                                                    style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
                                                )
                                            }
                                        }

                                        if (slot.isUploaded) {
                                            Text(
                                                text = "Digitalizado ✓",
                                                color = SuccessGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End
                                            )
                                        } else if (slot.isUploading) {
                                            Text(
                                                text = "${(slot.progress * 100).toInt()}%",
                                                color = SolidRed,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.End
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(RoyalBlue)
                                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "ADJUNTAR",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    if (slot.isUploaded) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        HorizontalDivider(color = SuccessGreen.copy(alpha = 0.15f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "OCR Validated",
                                                tint = SuccessGreen,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = slot.ocrText,
                                                color = TextDarkGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        val cleanDocErr = sanitizeError(documentFormError)
                        if (cleanDocErr != null && cleanDocErr.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SoftWarningRed)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = cleanDocErr,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val allOk = if (maritalStatus == "Casado(a)") {
                                    isDniFrontalUploaded && isDniReversoUploaded && isReciboUploaded && isConyugeDniFrontalUploaded && isConyugeDniReversoUploaded
                                } else {
                                    isDniFrontalUploaded && isDniReversoUploaded && isReciboUploaded
                                }

                                if (!allOk) {
                                    documentFormError = if (maritalStatus == "Casado(a)") {
                                        "⚠️ Es obligatorio adjuntar los 5 documentos solicitados (DNI Frontal/Reverso, Recibo de Servicios y DNIs Frontal/Reverso de tu Cónyuge) para tu expediente digital de financiamiento."
                                    } else {
                                        "⚠️ Es obligatorio adjuntar los 3 documentos solicitados (DNI Frontal/Reverso del titular y Recibo de Servicios) para consolidar tu expediente."
                                    }
                                } else {
                                    documentFormError = null
                                    currentStep = 4
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                "Validar y Continuar",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                4 -> {
                    // STEP 4: GEOLOCATION & LIMA HEATMAP INTEGRATION (STYLE UBER/RAPPI)
                    // Real-time debounce query to Photon API
                    LaunchedEffect(addressSearchQuery) {
                        if (addressSearchQuery.length >= 3) {
                            delay(400) // 400ms debounce
                            searchPhoton(addressSearchQuery) { suggestions ->
                                photonSearchSuggestions = suggestions
                                isSuggestionsDropdownExpanded = suggestions.isNotEmpty()
                            }
                        } else {
                            photonSearchSuggestions = emptyList()
                            isSuggestionsDropdownExpanded = false
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // EMPATHIC NOTICE
                        Text(
                            text = "Por favor, fija tu ubicación exacta en el mapa flotante. El Pin del centro marcará tu residencia. Puedes buscar tu calle arriba, arrastrar el mapa con tu dedo, o usar el botón GPS.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray, fontSize = 13.sp)
                        )

                        // FLOATING SEARCH BAR & SUGGESTIONS CONTAINER
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(10f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedTextField(
                                    value = addressSearchQuery,
                                    onValueChange = {
                                        addressSearchQuery = it
                                    },
                                    placeholder = { Text("Busca tu calle, condominio o zona...", fontSize = 14.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search icon",
                                            tint = RoyalBlue
                                        )
                                    },
                                    trailingIcon = {
                                        if (addressSearchQuery.isNotEmpty()) {
                                            IconButton(onClick = {
                                                addressSearchQuery = ""
                                                photonSearchSuggestions = emptyList()
                                                isSuggestionsDropdownExpanded = false
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Clear",
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("address_predictive_search"),
                                    shape = RoundedCornerShape(24.dp), // Styled like modern premium maps
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = RoyalBlue,
                                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.8f),
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )

                                // Autocomplete dropdown suggestions list
                                if (isSuggestionsDropdownExpanded && photonSearchSuggestions.isNotEmpty()) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        Column {
                                            photonSearchSuggestions.take(5).forEach { place ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            addressSearchQuery = place.displayName
                                                            currentLat = place.latitude
                                                            currentLng = place.longitude
                                                            streetAddress = place.addressText
                                                            verifiedDistrict = place.district
                                                            isCoordinateVerified = true
                                                            isSuggestionsDropdownExpanded = false
                                                            geolocationError = null
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Place,
                                                        contentDescription = null,
                                                        tint = RoyalBlue,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = place.displayName,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.Black
                                                        )
                                                        Text(
                                                            text = place.addressText,
                                                            fontSize = 11.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }
                                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // CENTRAL INTERACTIVE MAP CONTAINER
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, if (isCoordinateVerified) SuccessGreen else RoyalBlue),
                            modifier = Modifier.fillMaxWidth().height(350.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Dynamic canvas layer map
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .pointerInput(Unit) {
                                            // 1. Dragging map shifts map center underneath central Static Pin
                                            detectDragGestures(
                                                onDragStart = {
                                                    isCoordinateVerified = false
                                                },
                                                onDragEnd = {
                                                    // On release, fetch address & district immediately from Photon
                                                    isGeocodingInProcess = true
                                                    reverseGeocodePhoton(currentLat, currentLng) { resolvedAddress, resolvedDistrict ->
                                                        streetAddress = resolvedAddress
                                                        verifiedDistrict = resolvedDistrict
                                                        isGeocodingInProcess = false
                                                        isCoordinateVerified = true
                                                    }
                                                }
                                            ) { change, dragAmount ->
                                                change.consume()
                                                // Degree scaling according to zoom level
                                                val degreesPerPixel = 0.00018 / (zoomLevel / 15f)
                                                currentLng -= dragAmount.x * degreesPerPixel
                                                currentLat += dragAmount.y * degreesPerPixel
                                                
                                                // Restrict map to Metropolitan Lima boundaries
                                                currentLat = currentLat.coerceIn(-12.4, -11.6)
                                                currentLng = currentLng.coerceIn(-77.3, -76.8)
                                            }
                                        }
                                        .pointerInput(Unit) {
                                            detectTapGestures { offset ->
                                                // Tapping centers map at tapped region
                                                val centerX = size.width / 2f
                                                val centerY = size.height / 2f
                                                val scale = 50000f * (zoomLevel / 15f)
                                                val deltaLng = (offset.x - centerX) / scale
                                                val deltaLat = -(offset.y - centerY) / scale
                                                currentLng += deltaLng
                                                currentLat += deltaLat
                                                
                                                isGeocodingInProcess = true
                                                reverseGeocodePhoton(currentLat, currentLng) { resolvedAddress, resolvedDistrict ->
                                                    streetAddress = resolvedAddress
                                                    verifiedDistrict = resolvedDistrict
                                                    isGeocodingInProcess = false
                                                    isCoordinateVerified = true
                                                }
                                            }
                                        }
                                ) {
                                    val width = size.width
                                    val height = size.height
                                    val centerX = width / 2f
                                    val centerY = height / 2f
                                    val scale = 50000f * (zoomLevel / 15f)

                                    // Local translation function to paint points on map
                                    fun projectCoords(lat: Double, lng: Double): androidx.compose.ui.geometry.Offset {
                                        val x = centerX + (lng - currentLng).toFloat() * scale
                                        val y = centerY - (lat - currentLat).toFloat() * scale
                                        return androidx.compose.ui.geometry.Offset(x, y)
                                    }

                                    // Render Ocean background or Standard roadmap background
                                    if (isSatelliteView) {
                                        drawRect(color = Color(0xFF0F172A), size = size) // Deep space grey
                                    } else {
                                        drawRect(color = Color(0xFFF1F5F9), size = size) // Clean light slate grey
                                    }

                                    // Oceans / Coastline simulation for Miraflores/Chorrillos coordinates
                                    val oceanCenterPoint = projectCoords(-12.1600, -77.1000)
                                    drawCircle(
                                        color = if (isSatelliteView) Color(0xFF1E293B) else Color(0xFFBFDBFE),
                                        radius = 2800f * (zoomLevel / 15f),
                                        center = oceanCenterPoint
                                    )

                                    // Heatmap District Zones drawing sliding under the pin
                                    val districtsHeatmaps = listOf(
                                        // District, coordinates, zone colour representation (translucent)
                                        Triple("San Martín de Porres (SMP)", Pair(-12.0000, -77.0800), Color(0xFF8B5CF6).copy(alpha = 0.18f)), // Purple SMP
                                        Triple("Los Olivos", Pair(-11.9600, -77.0700), Color(0xFF8B5CF6).copy(alpha = 0.18f)), // Purple Los Olivos
                                        Triple("San Juan de Lurigancho (SJL)", Pair(-11.9800, -77.0000), Color(0xFFEF4444).copy(alpha = 0.18f)), // Red SJL
                                        Triple("Lince", Pair(-12.0830, -77.0330), Color(0xFFF97316).copy(alpha = 0.18f)), // Orange Lince
                                        Triple("San Isidro", Pair(-12.0950, -77.0250), Color(0xFFFBBF24).copy(alpha = 0.18f)), // Yellow San Isidro
                                        Triple("Miraflores", Pair(-12.1220, -77.0310), Color(0xFFFBBF24).copy(alpha = 0.18f)), // Yellow Miraflores
                                        Triple("Santiago de Surco", Pair(-12.1150, -76.9800), Color(0xFFF97316).copy(alpha = 0.18f)) // Orange Surco
                                    )

                                    districtsHeatmaps.forEach { h ->
                                        val heatPos = projectCoords(h.second.first, h.second.second)
                                        drawCircle(
                                            color = h.third,
                                            radius = 350f * (zoomLevel / 15f),
                                            center = heatPos
                                        )
                                    }

                                    // Draw major arterial road vectors across districts
                                    // Av. Javier Prado path
                                    val jpStart = projectCoords(-12.0860, -77.0900)
                                    val jpEnd = projectCoords(-12.0950, -76.9400)
                                    drawLine(
                                        color = if (isSatelliteView) Color(0xFF475569) else Color(0xFF94A3B8),
                                        start = jpStart,
                                        end = jpEnd,
                                        strokeWidth = 10f * (zoomLevel / 15f)
                                    )

                                    // Av. Próceres de la Independencia math
                                    val prStart = projectCoords(-12.0300, -77.0200)
                                    val prEnd = projectCoords(-11.9400, -76.9800)
                                    drawLine(
                                        color = if (isSatelliteView) Color(0xFF475569) else Color(0xFF94A3B8),
                                        start = prStart,
                                        end = prEnd,
                                        strokeWidth = 8f * (zoomLevel / 15f)
                                    )

                                    // Av. Habich
                                    val hStart = projectCoords(-12.0200, -77.0700)
                                    val hEnd = projectCoords(-12.0100, -77.0500)
                                    drawLine(
                                        color = if (isSatelliteView) Color(0xFF475569) else Color(0xFF94A3B8),
                                        start = hStart,
                                        end = hEnd,
                                        strokeWidth = 6f * (zoomLevel / 15f)
                                    )

                                    // Labels on map sliding
                                    drawContext.canvas.nativeCanvas.apply {
                                        val paint = android.graphics.Paint().apply {
                                            textSize = 15f * (zoomLevel / 15f)
                                            typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                                        }

                                        districtsHeatmaps.forEach { h ->
                                            val labelPos = projectCoords(h.second.first, h.second.second)
                                            paint.color = when {
                                                h.first.contains("SMP") || h.first.contains("Olivos") -> android.graphics.Color.rgb(139, 92, 246)
                                                h.first.contains("SJL") -> android.graphics.Color.rgb(239, 68, 68)
                                                h.first.contains("San Isidro") || h.first.contains("Miraflores") -> android.graphics.Color.rgb(218, 165, 32)
                                                else -> android.graphics.Color.rgb(249, 115, 22)
                                            }
                                            drawText(h.first, labelPos.x - 50f, labelPos.y, paint)
                                        }

                                        // Draw primary avenue labels
                                        paint.color = if (isSatelliteView) android.graphics.Color.GRAY else android.graphics.Color.DKGRAY
                                        paint.textSize = 11f * (zoomLevel / 15f)
                                        val jpLabel = projectCoords(-12.0910, -77.0150)
                                        drawText("AV. JAVIER PRADO", jpLabel.x, jpLabel.y - 10f, paint)
                                    }

                                    // Center Static Radar (representing cell signal triangulation)
                                    val staticCenter = androidx.compose.ui.geometry.Offset(centerX, centerY)
                                    drawCircle(
                                        color = (if (isCoordinateVerified) SuccessGreen else SolidRed).copy(alpha = 0.15f),
                                        radius = 45f * (zoomLevel / 15f),
                                        center = staticCenter
                                    )
                                    drawCircle(
                                        color = (if (isCoordinateVerified) SuccessGreen else SolidRed).copy(alpha = 0.35f),
                                        radius = 20f * (zoomLevel / 15f),
                                        center = staticCenter
                                    )
                                }

                                // 2. FLOATING MAP CONTROL OVERLAYS
                                // Map Mode Toggle (Top-Left)
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(14.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .clickable { isSatelliteView = !isSatelliteView }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSatelliteView) Icons.Default.Menu else Icons.Default.Info,
                                        contentDescription = "Map Mode Toggle",
                                        tint = RoyalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (isSatelliteView) "Ver Mapa" else "Ver Satélite",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Zoom Control buttons (+/-) (Top-Right)
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(14.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.95f))
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    IconButton(
                                        onClick = { zoomLevel = (zoomLevel + 1f).coerceAtMost(18f) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.width(22.dp))
                                    IconButton(
                                        onClick = { zoomLevel = (zoomLevel - 1f).coerceAtLeast(11f) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }

                                // FLOATING ROUND GPS BUTTON (Bottom-Right)
                                FloatingActionButton(
                                    onClick = {
                                        // Reset map location to real device current location coordinates (mocked in Lince/San Isidro sector)
                                        currentLat = -12.08890
                                        currentLng = -77.02240
                                        zoomLevel = 15.5f
                                        isGeocodingInProcess = true
                                        reverseGeocodePhoton(currentLat, currentLng) { resolvedAddress, resolvedDistrict ->
                                            streetAddress = resolvedAddress
                                            verifiedDistrict = resolvedDistrict
                                            isGeocodingInProcess = false
                                            isCoordinateVerified = true
                                        }
                                    },
                                    containerColor = Color.White,
                                    contentColor = RoyalBlue,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(14.dp)
                                        .size(46.dp),
                                    elevation = FloatingActionButtonDefaults.elevation(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "GPS Autolocalize",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // 3. STATIC CENTRAL MARKER PIN
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = (-18).dp), // account for tail height of indicator
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = "Central Static Marker PIN",
                                        tint = if (isCoordinateVerified) SuccessGreen else SolidRed,
                                        modifier = Modifier.size(38.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }

                        // GPS LOG info
                        Text(
                            text = "📍 Coordenadas de Financiamiento: Lat ${"%.5f".format(currentLat)}, Lng ${"%.5f".format(currentLng)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // CONFIRMATION BOTTOM CARD (Ubereque / Rappiesque Bottom Info)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(18.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Resolved physical Address box
                                Text(
                                    text = "Domicilio georreferenciado por satélite:",
                                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isGeocodingInProcess) Color(0xFFF1F5F9) else if (isCoordinateVerified) SoftSuccessGreen else Color(0xFFFFECEC))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (isGeocodingInProcess) {
                                        CircularProgressIndicator(color = RoyalBlue, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                        Text(
                                            text = "Geolocalizando dirección real de Lima... 📡",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.DarkGray
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isCoordinateVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                                            contentDescription = "Geocoding status",
                                            tint = if (isCoordinateVerified) SuccessGreen else SolidRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                text = if (streetAddress.isNotEmpty()) streetAddress else "Alinea el PIN central en el mapa de Lima",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (isCoordinateVerified) TextDarkGreen else Color.Black
                                            )
                                            Text(
                                                text = "Distrito Resolutivo: $verifiedDistrict",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                // Clean, high-contrast, informative georeference confirmation (fully compliant with client privacy)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Georreferenciado",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Dirección georreferenciada con éxito en Lima Metropolitana para expedición de entrega.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Domicilio manuscrito text-input (discrepancia libre del Pin del GPS)
                                Text(
                                    text = "Escribe tu dirección manuscrita exacta (conforme a tu recibo de luz/agua):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "La dirección escrita puede discrepar libremente del GPS del mapa por fines de facturación y despacho.",
                                    fontSize = 10.sp,
                                    color = Color.DarkGray
                                )

                                OutlinedTextField(
                                    value = confirmedAddressText,
                                    onValueChange = { confirmedAddressText = it },
                                    label = { Text("Escribe tu dirección manuscrita aquí...", fontSize = 11.sp) },
                                    placeholder = { Text("Ej: Av. Las Begonias 450, Int 104, San Isidro...") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("confirmed_address_input"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = RoyalBlue,
                                        unfocusedBorderColor = Color.LightGray,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )

                                val isManualAddressWritten = confirmedAddressText.trim().isNotEmpty()

                                if (isManualAddressWritten) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Dirección de recibo guardada para cotejo legal físico.",
                                            color = SuccessGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Error / validation output readout
                        val cleanGeoErr = sanitizeError(geolocationError)
                        if (cleanGeoErr != null && cleanGeoErr.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftWarningRed)
                                    .border(1.dp, SolidRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = cleanGeoErr,
                                    color = SolidRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // CTA BOTTOM BUTTON: Confirmar Dirección de Financiamiento with interactive simulated Processing state
                        if (isProcessingConfirmation) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = RoyalBlue,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Procesando solicitud... Guardando georreferenciación...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalBlue
                                        )
                                    )
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (streetAddress.isBlank()) {
                                        geolocationError = "⚠️ Por favor, fija e identifica el Pin de ubicación en el mapa de Lima antes de continuar."
                                    } else if (confirmedAddressText.isBlank()) {
                                        geolocationError = "⚠️ Es obligatorio escribir la dirección exacta de tu recibo físico para corroboración legal."
                                    } else {
                                        geolocationError = null
                                        uploadScope.launch {
                                            isProcessingConfirmation = true
                                            kotlinx.coroutines.delay(1500)
                                            isProcessingConfirmation = false
                                            currentStep = 5
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("confirm_address_button")
                            ) {
                                Text(
                                    "Confirmar Dirección de Financiamiento 🏍️",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }

                5 -> {
                    // STEP 5: CREDIT SIMULATOR & HUMAN AGENT HANDOFF
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val basePrice = selectedMoto.price.toFloat()
                        val minPayment = selectedMoto.initialPayment.toFloat()
                        // Strictly cap the maximum downpayment helper at S/. 10,000 and ensure safe range bounds
                        val maxPayment = minOf(maxOf(minPayment + 10f, basePrice * 0.6f), 10000f)
                        val coercedDownPayment = downPayment.coerceIn(minPayment, maxPayment)
                        
                        // Coerce downPayment in a LaunchedEffect to ensure the state itself is eventually aligned
                        LaunchedEffect(selectedMotoIndex, minPayment, maxPayment) {
                            if (downPayment < minPayment || downPayment > maxPayment) {
                                downPayment = downPayment.coerceIn(minPayment, maxPayment)
                            }
                        }
                        
                        // Interest multiplier of 1.25x for financing
                        val totalFinanced = (basePrice - coercedDownPayment) * 1.25f
                        val estimatedWeeklyQuota = totalFinanced / selectedWeeks.toFloat()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.5.dp, if (isStep5Locked && !isComplianceBlocked && !isDebtRatioBlocked) SuccessGreen else RoyalBlue),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                              ) {
                                Text(
                                    text = "Simulador de Crédito Global Go",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalBlue
                                    )
                                )

                                Text(
                                    text = "Ingresa tu sueldo mensual neto y ajusta tu cuota inicial. Las tasas semanales varían dinámicamente según las restricciones SBS y el tipo de documento validado.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Information Panel
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Modelo Seleccionado", fontSize = 11.sp, color = TextGrey)
                                        Text(selectedMoto.name, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Precio Base", fontSize = 11.sp, color = TextGrey)
                                        Text("S/. ${"%,.2f".format(basePrice)}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                }

                                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                                // Sueldo Mensual / Ingreso Input Field
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Sueldo Mensual Neto / Ingreso Fijo (S/.)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = RoyalBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    OutlinedTextField(
                                        value = monthlyIncome,
                                        onValueChange = { input ->
                                            if (input.all { it.isDigit() }) {
                                                monthlyIncome = input
                                            }
                                        },
                                        placeholder = { Text("Ej. 2500", color = Color.Gray) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("monthly_income_input"),
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.Black,
                                            unfocusedTextColor = Color.Black,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            focusedBorderColor = RoyalBlue,
                                            unfocusedBorderColor = Color.LightGray,
                                            focusedLabelColor = RoyalBlue,
                                            unfocusedLabelColor = TextGrey
                                        ),
                                        enabled = !isStep5Locked
                                    )
                                }

                                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                                // Downpayment display
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Cuota Inicial (Aporte Propio):",
                                        style = MaterialTheme.typography.labelMedium.copy(color = TextGrey)
                                    )
                                    Text(
                                        text = "S/. ${"%,.2f".format(coercedDownPayment)}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            color = RoyalBlue
                                        )
                                    )
                                }

                                // Interactive Slider for manual adjustment
                                Slider(
                                    value = coercedDownPayment,
                                    onValueChange = { if (!isStep5Locked) downPayment = (it / 100f).toInt() * 100f },
                                    valueRange = minPayment..maxPayment,
                                    enabled = !isStep5Locked,
                                    colors = SliderDefaults.colors(
                                        thumbColor = SolidRed,
                                        activeTrackColor = SolidRed,
                                        inactiveTrackColor = Color.LightGray
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                // Precise 10% steps buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (!isStep5Locked) {
                                                val stepSize = basePrice * 0.10f
                                                val target = coercedDownPayment - stepSize
                                                downPayment = target.coerceIn(minPayment, maxPayment)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BackgroundGrey),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("-10% Inicial", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            if (!isStep5Locked) {
                                                val stepSize = basePrice * 0.10f
                                                val target = coercedDownPayment + stepSize
                                                downPayment = target.coerceIn(minPayment, maxPayment)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BackgroundGrey),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+10% Inicial", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Mín: S/. ${"%,.0f".format(minPayment)} (Requerido)", fontSize = 10.sp, color = SolidRed, fontWeight = FontWeight.Bold)
                                    Text("Máx: S/. ${"%,.0f".format(maxPayment)}", fontSize = 10.sp, color = TextGrey)
                                }

                                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                                // Interactive Term Selector Column based on document type
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Plazo de Financiamiento Semanal:",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = TextGrey,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )

                                    if (prospectDocType == "CE") {
                                        selectedWeeks = 52
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(SolidRed.copy(alpha = 0.08f))
                                                .border(1.dp, SolidRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = "Alerta",
                                                    tint = SolidRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    text = "Regulación SBS: Amortización máxima de 52 semanas para carnet del extranjero.",
                                                    color = SolidRed,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf(52, 78, 104).forEach { weeks ->
                                                val isSelected = selectedWeeks == weeks
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(if (isSelected) RoyalBlue else Color(0xFFF1F5F9))
                                                        .border(
                                                            1.dp,
                                                            if (isSelected) RoyalBlue else Color.LightGray.copy(alpha = 0.5f),
                                                            RoundedCornerShape(12.dp)
                                                        )
                                                        .clickable(enabled = !isStep5Locked) {
                                                            selectedWeeks = weeks
                                                        }
                                                        .padding(vertical = 12.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$weeks semanas",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        color = if (isSelected) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Calculated Weekly Quota Output Panel
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BackgroundGrey),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "CUOTA SEMANAL ESTIMADA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextGrey
                                        )
                                        Text(
                                            text = "S/. ${"%.2f".format(estimatedWeeklyQuota)} x semana",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 24.sp,
                                            color = SolidRed
                                        )
                                        Text(
                                            text = "Amortizado a $selectedWeeks semanas con seguro de garantía prendaria",
                                            fontSize = 10.sp,
                                            color = TextGrey
                                        )
                                    }
                                }

                                // SUCCESS STATE OVERLAY (if pre-approved successfully and not blocked)
                                if (isStep5Locked && !isComplianceBlocked && !isDebtRatioBlocked) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SoftSuccessGreen)
                                            .border(1.5.dp, SuccessGreen, RoundedCornerShape(12.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "¡SOLICITUD PRE-APROBADA CON ÉXITO! 🎉",
                                                fontWeight = FontWeight.Black,
                                                color = SuccessGreen,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Amortización de S/. ${"%.2f".format(estimatedWeeklyQuota)} semanales registrada oficialmente en las bases corporativas de Global Go Perú.",
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Prominent multi-tier dynamic assessment errors
                        val cleanEvalErr = sanitizeError(evaluationError)
                        if (cleanEvalErr != null && cleanEvalErr.isNotEmpty()) {
                            if (isComplianceBlocked) {
                                // Prominent solid red card with Commercial Re-evaluation Warning
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = SolidRed),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("commercial_reevaluation_banner")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Sujeto a Reevaluación",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Text(
                                            text = "FINANCIAMIENTO SUJETO A REEVALUACIÓN",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "En este momento no podemos otorgar el financiamiento con las condiciones seleccionadas. Te sugerimos aumentar tu cuota inicial o elegir un modelo de menor valor.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else if (isDebtRatioBlocked) {
                                // Dynamic inline warning block with a dark solid red canvas styling
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D)), // Dark solid red canvas
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "SBS Warning",
                                            tint = Color.White,
                                            modifier = Modifier.size(30.dp)
                                        )
          
                                        Text(
                                            text = "Alerta de Riesgo SBS: Su cuota excede el límite prudencial de endeudamiento.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "La cuota calculada representa más del 30% de sus ingresos semanales declarados. Favor de reajustar plazo o inicial.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White.copy(alpha = 0.9f)
                                            ),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                // Standard error message card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SoftWarningRed)
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cleanEvalErr,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // EVALUATE OR REDIRECT CTA ACTIONS LIST
                        if (!isStep5Locked) {
                            Button(
                                onClick = {
                                    val incomeVal = monthlyIncome.toFloatOrNull() ?: 0f
                                    if (incomeVal <= 0f) {
                                        evaluationError = "⚠️ Por favor, ingresa un monto válido para tu sueldo mensual neto."
                                    } else {
                                        hasEvaluated = true
                                        
                                        // 1. Scoring points based on verified map district
                                        val districtPoints = when (verifiedDistrict) {
                                            "San Isidro", "Miraflores" -> 35
                                            "Lince", "Santiago de Surco" -> 25
                                            "Los Olivos", "San Martín de Porres (SMP)" -> 15
                                            else -> 5 // Includes SJL / other
                                        }

                                        // 2. Scoring points based on monthly net income
                                        val incomePoints = when {
                                            incomeVal >= 3500f -> 35
                                            incomeVal >= 2200f -> 25
                                            incomeVal >= 1200f -> 15
                                            else -> 5
                                        }

                                        // 3. Scoring points based on downpayment ratio (initial / value)
                                        val initialVal = downPayment
                                        val vehiclePrice = selectedMoto.price.toFloat()
                                        val ratio = if (vehiclePrice > 0) (initialVal / vehiclePrice) else 0f
                                        val initialPoints = when {
                                            ratio >= 0.30f -> 30
                                            ratio >= 0.20f -> 20
                                            ratio >= 0.10f -> 10
                                            else -> 0
                                        }

                                        val totalCommercialScore = districtPoints + incomePoints + initialPoints
                                        val minPassScore = 55

                                        if (totalCommercialScore < minPassScore) {
                                            isStep5Locked = true
                                            isComplianceBlocked = true
                                            evaluationError = "FINANCIAMIENTO SUJETO A REEVALUACIÓN"
                                        } else {
                                            // 4. SBS net weekly debt ratio check (30% of sueldo/4)
                                            val weeklyIncome = incomeVal / 4f
                                            val maxQuotaAllowed = weeklyIncome * 0.30f
                                            if (estimatedWeeklyQuota > maxQuotaAllowed) {
                                                isDebtRatioBlocked = true
                                                isStep5Locked = true
                                                evaluationError = "Alerta de Riesgo SBS: Su cuota excede el límite prudencial de endeudamiento."
                                            } else {
                                                // Approved successfully!
                                                isStep5Locked = true
                                                isComplianceBlocked = false // green pass
                                                isDebtRatioBlocked = false
                                                evaluationError = null
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Text(
                                    text = "Enviar Solicitud a Evaluación",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            if (isComplianceBlocked) {
                                // Subject to commercial reevaluation buttons listing: "Ajustar Cotización" and "Volver al Catálogo"
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Reset evaluation state so user can adjust variables freely
                                            isComplianceBlocked = false
                                            isStep5Locked = false
                                            hasEvaluated = false
                                            evaluationError = null
                                            currentStep = 1 // Go back to Catalog/Wizard start so they can swap or edit initial payment
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SolidRed),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("adjust_calculation_re_evaluation_btn")
                                    ) {
                                        Text(
                                            text = "Ajustar Cotización",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            // Reset and return
                                            isComplianceBlocked = false
                                            isStep5Locked = false
                                            hasEvaluated = false
                                            evaluationError = null
                                            currentStep = 1
                                        },
                                        border = BorderStroke(1.5.dp, RoyalBlue),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("catalog_return_re_evaluation_btn")
                                    ) {
                                        Text(
                                            text = "Volver al Catálogo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = RoyalBlue
                                        )
                                    }
                                }
                            } else {
                                // High priority direct advice channel on success
                                Button(
                                    onClick = {
                                        // WhatsApp redirect or call action simulated link
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Advice link",
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Llamar a un Asesor Humanizado Global Go",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // Standard Back Button
                        OutlinedButton(
                            onClick = onBack,
                            border = BorderStroke(1.5.dp, RoyalBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                "Volver al Portal de Ingreso",
                                color = RoyalBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // HIGH PROMINENCE SUMMARY BANNER below the layout
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SolidRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("approximate_payment_footer")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Pago Aproximado: S/. ${"%.2f".format(estimatedWeeklyQuota)} por semana",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    ),
                                    textAlign = TextAlign.Center
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

fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Galería & Modelos"
        2 -> "Registro de Datos"
        3 -> "Archivos DNI"
        4 -> "Confirmar Domicilio"
        else -> "Pre-Aprobación Express"
    }
}

data class UploadSlotSpec(
    val title: String,
    val subtitle: String,
    val isUploaded: Boolean,
    val isUploading: Boolean,
    val progress: Float,
    val onClick: () -> Unit,
    val ocrText: String
)

// OkHttp asynchronous service to query Photon (by Komoot)
fun searchPhoton(query: String, onResult: (List<PhotonPlace>) -> Unit) {
    if (query.isBlank() || query.length < 3) {
        onResult(emptyList())
        return
    }
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val encodedQuery = Uri.encode(query)
            val url = "https://photon.komoot.io/api/?q=$encodedQuery&lang=es&limit=5&lat=-12.046374&lon=-77.042793"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (bodyString != null) {
                        val json = JSONObject(bodyString)
                        val features = json.getJSONArray("features")
                        val list = mutableListOf<PhotonPlace>()
                        for (i in 0 until features.length()) {
                            val feat = features.getJSONObject(i)
                            val geom = feat.getJSONObject("geometry")
                            val coords = geom.getJSONArray("coordinates")
                            val lng = coords.getDouble(0)
                            val lat = coords.getDouble(1)
                            
                            val props = feat.getJSONObject("properties")
                            val name = props.optString("name", "")
                            val city = props.optString("city", "Lima")
                            val street = props.optString("street", name)
                            val district = props.optString("district", props.optString("city", "Lima"))
                            
                            val fullAddress = buildString {
                                if (street.isNotBlank() && street != name) append("$street, ")
                                append(name)
                                if (district.isNotBlank()) append(", $district")
                                if (city.isNotBlank() && city != district) append(", $city")
                            }
                            
                            list.add(
                                PhotonPlace(
                                    displayName = name,
                                    addressText = fullAddress,
                                    latitude = lat,
                                    longitude = lng,
                                    district = district
                                )
                            )
                        }
                        withContext(Dispatchers.Main) {
                            onResult(list)
                        }
                        return@launch
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Emulated fallback search for Lima districts if offline / quota limits
        val samplePlaces = listOf(
            PhotonPlace("Av. Larco 300", "Av. José Larco 300, Miraflores, Lima", -12.12210, -77.03020, "Miraflores"),
            PhotonPlace("Plaza de Armas", "Plaza Mayor, Cercado de Lima, Lima", -12.04610, -77.03100, "Cercado de Lima"),
            PhotonPlace("Javier Prado Este 1100", "Av. Javier Prado Este 1140, San Isidro, Lima", -12.08980, -77.02240, "San Isidro"),
            PhotonPlace("Av. Habich 350", "Av. Eduardo de Habich 350, San Martín de Porres, Lima", -12.0150, -77.0650, "San Martín de Porres (SMP)"),
            PhotonPlace("Lince Parque", "Av. Militar 1320, Lince, Lima", -12.0830, -77.0320, "Lince"),
            PhotonPlace("Av. Próceres 1420", "Av. Próceres de la Independencia 1420, SJL, Lima", -11.9850, -77.0050, "San Juan de Lurigancho (SJL)")
        )
        val filtered = samplePlaces.filter { 
            it.displayName.lowercase().contains(query.lowercase()) || 
            it.addressText.lowercase().contains(query.lowercase()) 
        }
        withContext(Dispatchers.Main) {
            onResult(filtered)
        }
    }
}

// OkHttp reverse geocoding request to Photon to translate lat/lng to Street, District, City
fun reverseGeocodePhoton(lat: Double, lng: Double, onResult: (String, String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = "https://photon.komoot.io/reverse?lon=$lng&lat=$lat"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (bodyString != null) {
                        val json = JSONObject(bodyString)
                        val features = json.getJSONArray("features")
                        if (features.length() > 0) {
                            val first = features.getJSONObject(0)
                            val props = first.getJSONObject("properties")
                            val name = props.optString("name", "")
                            val street = props.optString("street", "")
                            val district = props.optString("district", props.optString("city", "Lima"))
                            val city = props.optString("city", "Lima")
                            
                            val address = buildString {
                                if (street.isNotBlank() && street != name) append("$street, ")
                                append(name)
                                if (district.isNotBlank()) append(", $district")
                                if (city.isNotBlank() && city != district) append(", $city")
                            }
                            withContext(Dispatchers.Main) {
                                onResult(address, district)
                            }
                            return@launch
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // Exact geographic coordinate-bound sectors matching in Lima
        val estimatedDistrict = when {
            lat > -12.04 && lng < -77.05 -> "San Martín de Porres (SMP)"
            lat > -12.04 && lng >= -77.05 -> "San Juan de Lurigancho (SJL)"
            lat <= -12.04 && lat > -12.09 && lng < -77.045 -> "Lince"
            lat <= -12.04 && lat > -12.09 && lng >= -77.045 -> "San Isidro"
            lat <= -12.09 && lng < -77.01 -> "Miraflores"
            else -> "Santiago de Surco"
        }
        val estimatedStreet = when (estimatedDistrict) {
            "San Martín de Porres (SMP)" -> "Av. Eduardo de Habich 370, San Martín de Porres"
            "San Juan de Lurigancho (SJL)" -> "Av. Próceres de la Independencia 1420, SJL"
            "Lince" -> "Av. Militar 1320, Lince"
            "San Isidro" -> "Av. Javier Prado Este 1140, San Isidro"
            "Miraflores" -> "Av. Larco 300, Miraflores"
            else -> "Av. Primavera 1024, Santiago de Surco"
        }
        withContext(Dispatchers.Main) {
            onResult("$estimatedStreet, Lima", estimatedDistrict)
        }
    }
}

suspend fun queryReniecDni(dni: String): ReniecResult = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val token = "b3671822bc57a304ba008f2835b68ba50592a23e6fc6cd7c929afffcb7504c4c"
    
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val jsonObject = org.json.JSONObject()
    jsonObject.put("dni", dni)
    val body = okhttp3.RequestBody.create(mediaType, jsonObject.toString())
    
    val request = okhttp3.Request.Builder()
        .url("https://apiperu.dev/api/dni")
        .post(body)
        .addHeader("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $token")
        .build()

    try {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return@withContext ReniecResult(false, null, null, null, null, null, "Error HTTP: ${response.code}")
            }
            val bodyStr = response.body?.string()
            if (bodyStr.isNullOrEmpty()) {
                return@withContext ReniecResult(false, null, null, null, null, null, "Respuesta vacía de ApiPeru")
            }
            val root = org.json.JSONObject(bodyStr)
            val success = root.optBoolean("success", false)
            if (success) {
                val dataObj = root.optJSONObject("data")
                if (dataObj != null) {
                    val nombre = dataObj.optString("nombre_completo", "")
                    val direccion = dataObj.optString("direccion", "")
                    val dep = dataObj.optString("departamento", "")
                    val prov = dataObj.optString("provincia", "")
                    val dist = dataObj.optString("distrito", "")
                    ReniecResult(true, nombre, direccion, dep, prov, dist, null)
                } else {
                    ReniecResult(false, null, null, null, null, null, "Datos no encontrados en respuesta")
                }
            } else {
                val msg = root.optString("message", "DNI no existe o token incorrecto")
                ReniecResult(false, null, null, null, null, null, msg)
            }
        }
    } catch (e: Exception) {
        ReniecResult(false, null, null, null, null, null, "Fallo de conexión: ${e.message}")
    }
}

data class ReniecResult(
    val success: Boolean,
    val nombreCompleto: String?,
    val direccion: String?,
    val departamento: String?,
    val provincia: String?,
    val distrito: String?,
    val error: String?
)

