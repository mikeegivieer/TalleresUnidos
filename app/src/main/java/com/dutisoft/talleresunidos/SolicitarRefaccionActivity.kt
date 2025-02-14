package com.dutisoft.talleresunidos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                // Permiso concedido, obtener la ubicación
                obtenerUbicacionActual()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Permiso concedido, obtener la ubicación
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

        // Obtener el nombre del taller del Intent
        val nombreTaller = intent.getStringExtra("nombreTaller") ?: "Taller Desconocido"

        // Usar Jetpack Compose para la interfaz de usuario
        setContent {
            TalleresUnidosTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SolicitarRefaccionScreen(nombreTaller = nombreTaller)
                }
            }
        }

        // Solicitar permisos de ubicación
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
                    // Actualizar el estado con la ubicación actual
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título "Solicitar refacción"
        Text(
            text = "Solicitar refacción",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Input para la pieza
        var pieza by remember { mutableStateOf("") }
        OutlinedTextField(
            value = pieza,
            onValueChange = { pieza = it },
            label = { Text("Pieza") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Filtrar la lista de refacciones basado en el input
        val refaccionesFiltradas = refacciones.filter { it.contains(pieza, ignoreCase = true) }

        // Mostrar los resultados filtrados
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(refaccionesFiltradas) { refaccion ->
                Text(
                    text = refaccion,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Nombre del taller
        Text(
            text = "Taller: $nombreTaller",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Mapa de Google Maps
        val ubicacionTaller = ubicacionActual ?: LatLng(19.4326, -99.1332) // Ejemplo: Ciudad de México
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(ubicacionTaller, 15f)
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            cameraPositionState = cameraPositionState
        )

        // Fecha actual
        val fechaActual = remember {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
        Text(
            text = "Fecha: $fechaActual",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Input para el estado
        var estado by remember { mutableStateOf("") }
        OutlinedTextField(
            value = estado,
            onValueChange = { estado = it },
            label = { Text("Estado") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Botón para tomar una foto de evidencia
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