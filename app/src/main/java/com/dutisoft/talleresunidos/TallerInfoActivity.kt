package com.dutisoft.talleresunidos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.dutisoft.talleresunidos.ui.theme.TalleresUnidosTheme
import kotlin.random.Random

class TallerInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TalleresUnidosTheme {
                val nombre = intent.getStringExtra("nombre")
                val direccion = intent.getStringExtra("direccion")
                val numRefacciones = intent.getIntExtra("numRefacciones", 0)
                val imageUrl = intent.getStringExtra("imageUrl")

                // Generar una lista de refacciones aleatorias
                val refacciones = generarRefaccionesAleatorias(numRefacciones)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = nombre ?: "",
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Logo del taller",
                        modifier = Modifier.size(128.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Refacciones en stock: $numRefacciones", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Dirección: $direccion", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Lista de Refacciones:",
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(refacciones) { refaccion ->
                            RefaccionItem(refaccion)
                        }
                    }
                }
            }
        }
    }

    private fun generarRefaccionesAleatorias(numRefacciones: Int): List<String> {
        val refaccionesDisponibles = listOf(
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

        return List(numRefacciones) {
            refaccionesDisponibles[Random.nextInt(refaccionesDisponibles.size)]
        }
    }
}

@Composable
fun RefaccionItem(refaccion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = refaccion,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            // Si deseas agregar una imagen para cada refacción, puedes hacerlo aquí
            Image(
                painter = rememberAsyncImagePainter("https://via.placeholder.com/64"),
                contentDescription = "Imagen de refacción",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}