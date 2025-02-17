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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.dutisoft.talleresunidos.ui.theme.TalleresUnidosTheme
import kotlin.random.Random

// Data class para representar un taller
data class Taller(
    val nombre: String,
    val direccion: String,
    val numRefacciones: Int,
    val imageUrl: String
)

// Función que genera la lista de talleres
fun generarTalleres(): List<Taller> {
    val nombres = listOf(
        "Mecánica Rápida Express",
        "Motores y Más",
        "Carrocería Perfecta",
        "Taller TurboFix",
        "Frenos y Suspensión Pro"
    )
    val direcciones = listOf(
        "Av. Revolución 123",
        "Calle Juárez 456",
        "Boulevard Industrial 789",
        "Carrera 5 #67-89",
        "Avenida del Taller 321"
    )
    val imagenes = listOf(
        "https://img.freepik.com/vector-gratis/plantilla-logotipo-centro-coches_1057-4800.jpg",
        "https://img.freepik.com/vector-gratis/plantilla-logotipo-servicio-automovil-degradado_23-2149727270.jpg",
        "https://www.zarla.com/images/zarla-mecnico-247-1x1-2400x2400-20231130-mpbb7mbmxc7jjkcxp8vr.png?crop=1:1,smart&width=250&dpr=2",
        "https://img.freepik.com/vector-gratis/diseno-logotipo-ingenieria-mecanica_23-2150045612.jpg?semt=ais_hybrid",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVahWekt_UGwZ6-TOSW67pR9td2U3TylV2Og&s"
    )
    return nombres.zip(direcciones).zip(imagenes).map { (data, imageUrl) ->
        val (nombre, direccion) = data
        Taller(nombre, direccion, Random.nextInt(1, 10), imageUrl)
    }
}

// Repositorio global de talleres
object TallerRepository {
    val talleres: List<Taller> by lazy { generarTalleres() }
}

@Composable
fun TalleresScreen(
    modifier: Modifier = Modifier,
    onTallerClick: (Taller) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TallerRepository.talleres) { taller ->
            TallerItem(taller) {
                onTallerClick(it)
            }
        }
    }
}

@Composable
fun TallerItem(taller: Taller, onClick: (Taller) -> Unit) {
    ElevatedCard(
        onClick = { onClick(taller) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Fila para el nombre del taller con icono
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = "Taller",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = taller.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Fila para refacciones en stock con icono Build
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Refacciones",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Refacciones en stock: ${taller.numRefacciones}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                // Fila para la dirección con icono
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Dirección",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = taller.direccion,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = rememberAsyncImagePainter(taller.imageUrl),
                contentDescription = "Logo del taller",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalleresUnidosTheme {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Talleres Unidos") }
                        )
                    }
                ) { innerPadding ->
                    TalleresScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) { taller ->
                        // Creamos el intent sin pasar la lista, pues ésta se obtiene globalmente.
                        val intent = Intent(this, TallerInfoActivity::class.java).apply {
                            putExtra("nombre", taller.nombre)
                            putExtra("direccion", taller.direccion)
                            putExtra("numRefacciones", taller.numRefacciones)
                            putExtra("imageUrl", taller.imageUrl)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TalleresScreenPreview() {
    TalleresUnidosTheme {
        TalleresScreen { }
    }
}
