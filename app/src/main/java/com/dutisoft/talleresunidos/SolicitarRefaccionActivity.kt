package com.dutisoft.talleresunidos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.*

class SolicitarRefaccionActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
                // Permiso denegado, manejar el caso
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            TalleresUnidosTheme {
                val nombreTaller = intent.getStringExtra("nombreTaller") ?: "Taller Desconocido"

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SolicitarRefaccionScreen(nombreTaller = nombreTaller)
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
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val ubicacionActual = LatLng(location.latitude, location.longitude)
                    setContent {
                        TalleresUnidosTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                SolicitarRefaccionScreen(nombreTaller = "Taller Desconocido", ubicacionActual = ubicacionActual)
                            }
                        }
                    }
                }
            }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarRefaccionScreen(nombreTaller: String, ubicacionActual: LatLng? = null) {
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
    var active by remember { mutableStateOf(false) } // Estado para controlar si el SearchBar está activo
    val refaccionesFiltradas = remember(searchQuery) {
        refacciones.filter {
            it.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Solicitar refacción",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                active = true // Activar el SearchBar cuando se escribe
            },
            onSearch = {
                active = false // Desactivar el SearchBar al realizar la búsqueda
            },
            active = active, // Controlar si el SearchBar está activo
            onActiveChange = {
                active = it // Actualizar el estado de activación
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Batería, Amortiguador...") }, // Placeholder
            leadingIcon = { // Icono de búsqueda
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar"
                )
            }
        ) {
            // Limitar la altura del área de resultados
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Altura máxima del área de resultados
            ) {
                // Mostrar resultados de búsqueda
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
                                        searchQuery = refaccion // Seleccionar la pieza
                                        active = false // Desactivar el SearchBar después de seleccionar
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
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        val ubicacionTaller = ubicacionActual ?: LatLng(19.4326, -99.1332)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacionTaller, 15f)
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState
        )

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