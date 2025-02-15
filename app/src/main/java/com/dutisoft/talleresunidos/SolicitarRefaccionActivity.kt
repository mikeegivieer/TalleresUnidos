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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    // Variable para guardar el nombre del taller encontrado
    var tallerEncontrado by remember { mutableStateOf("") }

    val refaccionesFiltradas = remember(searchQuery) {
        refacciones.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    // Se muestra el diálogo si se presionó "Enter" y no se encontraron coincidencias
    LaunchedEffect(key1 = active, key2 = searchQuery) {
        if (!active && searchQuery.isNotBlank() && refaccionesFiltradas.isEmpty()) {
            showConfirmDialog = true
        }
    }

    // Primer diálogo: preguntar si se desea comprobar disponibilidad en otros talleres
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("No disponible") },
            text = { Text("¿Desea comprobar disponibilidad entre talleres?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    // Buscar la refacción en la lista de disponibles
                    val foundIndex = refaccionesDisponibles.indexOfFirst { it.equals(searchQuery, ignoreCase = true) }
                    if (foundIndex != -1) {
                        // Usamos 1-indexación para determinar la posición
                        val piezaPos = foundIndex + 1
                        // Buscar el primer taller que tenga refacciones disponibles
                        val taller = TallerRepository.talleres.firstOrNull { piezaPos <= it.numRefacciones }
                        if (taller != null) {
                            tallerEncontrado = taller.nombre
                            showTallerDialog = true // Mostrar el diálogo con el nombre del taller
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

    // Segundo diálogo: muestra el nombre del taller con el nombre en negrita y botón "Solicitar pieza"
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

        Text(
            text = "Taller: $nombreTaller",
            fontSize = 18.sp
        )

        val markerPosition = ubicacionActual ?: LatLng(19.4326, -99.1332)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
        }

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

        val fechaActual = remember {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
        Text(
            text = "Fecha: $fechaActual",
            fontSize = 18.sp
        )

        var estado by remember { mutableStateOf("") }
        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = { /* Lógica para tomar una foto de evidencia */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Tomar foto de evidencia")
        }
    }
}
