package com.dutisoft.talleresunidos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.dutisoft.talleresunidos.ui.theme.TalleresUnidosTheme

class TallerInfoActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalleresUnidosTheme {
                // Obtén datos del intent con valores por defecto
                val nombre = intent.getStringExtra("nombre") ?: "Sin Nombre"
                val direccion = intent.getStringExtra("direccion") ?: "Dirección no disponible"
                val numRefacciones = intent.getIntExtra("numRefacciones", 0)
                val imageUrl = intent.getStringExtra("imageUrl") ?: ""
                val refacciones = generarRefacciones(numRefacciones)
                val context = LocalContext.current

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Información del Taller") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                val intent = Intent(context, SolicitarRefaccionActivity::class.java).apply {
                                    putExtra("nombreTaller", nombre)
                                    putExtra("direccion", direccion)
                                }
                                context.startActivity(intent)
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "+",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Logo del taller, mostrado en forma circular
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Logo del taller",
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape)  // Aquí se recorta la imagen en forma circular
                                .padding(4.dp),
                            contentScale = ContentScale.Crop
                        )
                        // Nombre del taller debajo de la imagen
                        Text(
                            text = nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        // Datos del taller
                        Text(
                            text = "Refacciones en stock: $numRefacciones",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Dirección: $direccion",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        // Título de lista
                        Text(
                            text = "Lista de Refacciones",
                            style = MaterialTheme.typography.titleMedium
                        )
                        // Lista de refacciones en una LazyColumn
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(refacciones) { refaccion ->
                                RefaccionItem(refaccion)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generarRefacciones(numRefacciones: Int): List<String> {
        val refaccionesDisponibles = listOf(
            "Filtro de aire", "Pastillas de freno", "Batería", "Aceite de motor", "Bujías",
            "Amortiguadores", "Correa de distribución", "Radiador", "Alternador", "Lámparas de faro"
        )
        return List(numRefacciones) {
            refaccionesDisponibles[it]
        }
    }
}

@Composable
fun RefaccionItem(refaccion: String) {
    // Tarjeta elevada para cada refacción
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto de la refacción
            Text(
                text = refaccion,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Imagen de placeholder
            Image(
                painter = rememberAsyncImagePainter("https://via.placeholder.com/64"),
                contentDescription = "Imagen de refacción",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}
