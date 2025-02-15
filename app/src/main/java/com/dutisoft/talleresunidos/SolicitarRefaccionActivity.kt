package com.dutisoft.talleresunidos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
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

        // Obtener valores del intent
        val nombreTaller = intent.getStringExtra("nombreTaller") ?: "Taller Desconocido"
        val direccion = intent.getStringExtra("direccion") ?: "Sin dirección"

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
                    // Se pasa también la dirección al composable
                    SolicitarRefaccionScreen(
                        nombreTaller = nombreTaller,
                        direccion = direccion,
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
    direccion: String,
    ubicacionActual: LatLng? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    // Lista de refacciones de motor para autocompletar
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

    // Lista de refacciones disponibles a buscar
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

    // Corrección: se utiliza rememberMarkerState para el marcador del mapa
    val markerPosition = ubicacionActual ?: LatLng(19.4326, -99.1332)
    val markerState = rememberMarkerState(position = markerPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
    }
    val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SearchBar
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
            },
            content = {}
        )
        // Fila para el nombre del taller (sólo icono y valor)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Work,
                contentDescription = "Taller",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = nombreTaller, fontSize = 22.sp)
        }
        // Fila para la fecha (sólo icono y valor)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Fecha",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = fechaActual, fontSize = 18.sp)
        }
        // Fila para la dirección (sólo icono y valor)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Dirección",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = direccion, fontSize = 18.sp)
        }
        // Mapa con la ubicación
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                title = "Ubicación actual",
                snippet = "Estás aquí"
            )
        }
        // Menú desplegable para el estado
        val opciones = listOf("Instalado", "No instalado")
        var expanded by remember { mutableStateOf(false) }
        var estadoSeleccionado by remember { mutableStateOf("") }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado,
                onValueChange = { estadoSeleccionado = it },
                readOnly = true,
                label = { Text("Nueva instalación") },
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
        // Botón para tomar foto de evidencia con ícono de cámara
        Button(
            onClick = { /* Lógica para tomar foto de evidencia */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Tomar foto de evidencia",
                tint = Color.White
            )
        }
        // Botón inferior "Solicitar" en color verde
        Button(
            onClick = {
                Toast.makeText(context, "Solicitud realizada con éxito", Toast.LENGTH_SHORT).show()
                activity?.finish()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Solicitar", color = Color.White)
        }
    }
}
