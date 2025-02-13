package com.dutisoft.talleresunidos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dutisoft.talleresunidos.ui.theme.TalleresUnidosTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import kotlin.random.Random
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TalleresUnidosTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* Acción del FAB */ },
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar taller")
                        }
                    }
                ) { innerPadding ->
                    TalleresScreen(modifier = Modifier.padding(innerPadding)) { taller ->
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
data class Taller(val nombre: String, val direccion: String, val numRefacciones: Int, val imageUrl: String)

fun generarTalleres(): List<Taller> {
    val nombres = listOf(
        "Mecánica Rápida Express",
        "Motores y Más Taller",
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

@Composable
fun TalleresScreen(modifier: Modifier = Modifier, onTallerClick: (Taller) -> Unit) {
    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Talleres Unidos",
            fontSize = 24.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(generarTalleres()) { taller ->
                TallerItem(taller, onTallerClick)
            }
        }
    }
}

@Composable
fun TallerItem(taller: Taller, onClick: (Taller) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(taller) }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = taller.nombre, fontSize = 18.sp, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Refacciones en stock: ${taller.numRefacciones}", fontSize = 14.sp)
                Text(text = "Dirección: ${taller.direccion}", fontSize = 14.sp)
            }
            Image(
                painter = rememberAsyncImagePainter(taller.imageUrl),
                contentDescription = "Logo del taller",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


