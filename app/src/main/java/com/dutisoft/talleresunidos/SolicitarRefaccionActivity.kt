package com.dutisoft.talleresunidos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dutisoft.talleresunidos.ui.theme.TalleresUnidosTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.*


class SolicitarRefaccionActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val ubicacionActualState = mutableStateOf<LatLng?>(null)

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                obtenerUbicacionActual()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                obtenerUbicacionActual()
            }
            else -> {
                // Permiso denegado
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val nombreTaller = intent.getStringExtra("nombreTaller") ?: "Taller Desconocido"

        setContent {
            TalleresUnidosTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Solicitar Refacción") },
                            navigationIcon = {
                                val activity = LocalActivity.current
                                IconButton(onClick = { activity?.finish() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    SolicitarRefaccionScreen(
                        nombreTaller = nombreTaller,
                        ubicacionActual = ubicacionActualState.value,
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    )
                }
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            obtenerUbicacionActual()
        }
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                ubicacionActualState.value = LatLng(location.latitude, location.longitude)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarRefaccionScreen(
    nombreTaller: String,
    ubicacionActual: LatLng? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Listas de refacciones (sin cambios)
    val refacciones = listOf(
        "Bloque del motor",
        "Culata de cilindros",
        "Árbol de levas",
        "Cigueñal",
        "Pistones forjados",
        "Bielas reforzadas",
        "Bomba de aceite",
        "Bomba de agua",
        "Carter del motor",
        "Intercooler",
        "Turbo compresor",
        "Supercargador",
        "Sistema de inyección electrónica",
        "Cámara de combustión",
        "Junta de culata",
        "Culata del motor",
        "Válvula de admisión",
        "Válvula de escape",
        "Muelles de válvula",
        "Guías de válvula",
        "Tapa de árbol de levas",
        "Cojinetes del cigueñal",
        "Sistema de lubricación",
        "Carter de aceite",
        "Bomba de combustible eléctrica",
        "Regulador de presión de combustible",
        "Sensor de oxígeno",
        "Sensor de temperatura del motor",
        "Sensor de presión del aceite",
        "Módulo ECU",
        "Cuerpo de acelerador",
        "Sensor de posición del cigüeñal",
        "Sensor de detonación",
        "Inyector de alta presión",
        "Boquilla de inyección",
        "Radiador de aceite",
        "Ventilador del motor",
        "Sistema de escape completo",
        "Catalizador",
        "Silenciador",
        "Tubo de escape",
        "Colector de escape",
        "Sensor de velocidad del motor",
        "Módulo de encendido",
        "Bobina de encendido",
        "Distribuidor de encendido",
        "Conjunto de juntas del motor",
        "Sistema de admisión de aire",
        "Tapa de motor"
    )

    val refaccionesDisponibles = listOf(
        "Filtro de aire", "Pastillas de freno", "Bateria", "Aceite de motor", "Bujias",
        "Amortiguadores", "Correa de distribucion", "Radiador", "Alternador", "Lamparas de faro"
    )

    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showTallerDialog by remember { mutableStateOf(false) }
    var tallerEncontrado by remember { mutableStateOf("") }

    val refaccionesFiltradas = remember(searchQuery) {
        refacciones.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(key1 = active, key2 = searchQuery) {
        if (!active && searchQuery.isNotBlank() && refaccionesFiltradas.isEmpty()) {
            showConfirmDialog = true
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("No disponible") },
            text = { Text("¿Desea comprobar disponibilidad entre talleres?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    val foundIndex = refaccionesDisponibles.indexOfFirst { it.equals(searchQuery, ignoreCase = true) }
                    if (foundIndex != -1) {
                        val piezaPos = foundIndex + 1
                        val taller = TallerRepository.talleres.firstOrNull { piezaPos <= it.numRefacciones }
                        if (taller != null) {
                            tallerEncontrado = taller.nombre
                            showTallerDialog = true
                        }
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showTallerDialog) {
        AlertDialog(
            onDismissRequest = { showTallerDialog = false },
            title = { Text("Pieza disponible") },
            text = {
                Text(
                    buildAnnotatedString {
                        append("Pieza disponible en: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(tallerEncontrado)
                        }
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    Toast.makeText(context, "Solicitud realizada con éxito", Toast.LENGTH_SHORT).show()
                    showTallerDialog = false
                }) {
                    Text("Solicitar pieza")
                }
            }
        )
    }

    // Variables para el menú desplegable de status
    val opciones = listOf("Instalado", "No instalado")
    var expanded by remember { mutableStateOf(false) }
    var estadoSeleccionado by remember { mutableStateOf("") }

    // Variables para la cámara y la imagen capturada
    var capturedImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Launcher para tomar foto (obtiene un Bitmap pequeño)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                capturedImage = bitmap
            }
        }
    )

    // Launcher para solicitar permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                cameraLauncher.launch()
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Posición del mapa y fecha actual
    val markerPosition = ubicacionActual ?: LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }
    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SearchBar para buscar refacciones
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                active = true
            },
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Batería, Amortiguador...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                if (searchQuery.isNotEmpty() && refaccionesFiltradas.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(refaccionesFiltradas) { refaccion ->
                            Text(
                                text = refaccion,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = refaccion
                                        active = false
                                    }
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        // Nombre del taller
        Text(
            text = "Taller: $nombreTaller",
            fontSize = 18.sp
        )
        // Fecha actual
        Text(
            text = "Fecha: $fechaActual",
            fontSize = 18.sp
        )
        // Mapa con la ubicación
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = markerPosition),
                title = "Ubicación actual",
                snippet = "Estás aquí"
            )
        }
        // Menú desplegable para el status
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado,
                onValueChange = { estadoSeleccionado = it },
                readOnly = true,
                label = { Text("Status") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            estadoSeleccionado = opcion
                            expanded = false
                        }
                    )
                }
            }
        }
        // Se muestra la imagen capturada (si existe)
        capturedImage?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de evidencia",
                modifier = Modifier
                    .size(200.dp)
                    .padding(vertical = 8.dp)
            )
        }
        // Botón para tomar foto de evidencia (solo se muestra si aún no se ha tomado la foto)
        if (capturedImage == null) {
            Button(
                onClick = {
                    // Verifica si se cuenta con el permiso de cámara
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        cameraLauncher.launch()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Tomar foto de evidencia")
            }
        }
    }
}

