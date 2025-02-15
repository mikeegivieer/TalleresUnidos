package com.dutisoft.talleresunidos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

    // Estado mutable para almacenar la ubicación actual
    private val ubicacionActualState = mutableStateOf<LatLng?>(null)

    // Solicitud de permisos
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
                // Permiso denegado, manejar el caso según se requiera
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtén el nombre del taller del intent
        val nombreTaller = intent.getStringExtra("nombreTaller") ?: "Taller Desconocido"

        // Configura la UI una sola vez con un TopAppBar que incluye la flecha de retroceso
        setContent {
            TalleresUnidosTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Solicitar Refacción") },
                            navigationIcon = {
                                // Usamos LocalActivity en lugar de convertir el contexto
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

        // Solicitar permisos o, si ya están concedidos, obtener la ubicación
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
    // Lista de refacciones
    val refacciones = listOf(
        "Filtro de aire",
        "Pastillas de freno",
        "Batería",
        "Aceite de motor",
        "Bujías",
        "Amortiguadores",
        "Correa de distribución",
        "Radiador",
        "Alternador",
        "Lámparas de faro"
    )

    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val refaccionesFiltradas = remember(searchQuery) {
        refacciones.filter { it.contains(searchQuery, ignoreCase = true) }
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
                if (searchQuery.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(refaccionesFiltradas) { refaccion ->
                            Text(
                                text = refaccion,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground,
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

        // Información del taller
        Text(
            text = "Taller: $nombreTaller",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        val ubicacionTaller = ubicacionActual ?: LatLng(19.4326, -99.1332)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacionTaller, 15f)
        }

        // Muestra el mapa con marcador
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState
        ) {
            if (ubicacionActual != null) {
                Marker(
                    state = MarkerState(position = ubicacionActual),
                    title = "Ubicación actual",
                    snippet = "Estás aquí"
                )
            }
        }

        val fechaActual = remember {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
        Text(
            text = "Fecha: $fechaActual",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
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
            onClick = { /* Lógica para tomar una foto */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Tomar foto de evidencia")
        }
    }
}
