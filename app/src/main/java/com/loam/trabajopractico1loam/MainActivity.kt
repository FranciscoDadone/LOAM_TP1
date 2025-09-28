package com.loam.trabajopractico1loam

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.loam.trabajopractico1loam.ui.screens.HomeScreen
import com.loam.trabajopractico1loam.ui.screens.PreciosReferenciaScreen
import com.loam.trabajopractico1loam.ui.theme.TrabajoPractico1LOAMTheme

class MainActivity : ComponentActivity() {
    private val db = Firebase.firestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabajoPractico1LOAMTheme {
                var currentScreen by remember { mutableStateOf("home") }

                when (currentScreen) {
                    "home" -> HomeScreen(
                        onNavigateToPrecios = { currentScreen = "precios" },
                        onNavigateToDemo = { currentScreen = "demo" },
                        onNavigateToSection3 = { currentScreen = "section3" },
                        onNavigateToSection4 = { currentScreen = "section4" }
                    )
                    "precios" -> PreciosReferenciaScreen()
                    "demo" -> FirestoreDemo(db = db)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(db: com.google.firebase.firestore.FirebaseFirestore) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (selectedTab == 0) "Demo Firebase" else "Precios de Referencia"
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Demo") },
                    label = { Text("Demo") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Precios") },
                    label = { Text("Precios") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> FirestoreDemo(
                db = db,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> PreciosReferenciaScreen()
        }
    }
}

@Composable
fun FirestoreDemo(
    db: com.google.firebase.firestore.FirebaseFirestore,
    modifier: Modifier = Modifier
) {
    var message by remember { mutableStateOf("Firebase configurado correctamente") }
    var inputText by remember { mutableStateOf("") }
    var savedData by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Firebase Firestore Demo",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Text(text = message)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input para guardar datos
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Texto a guardar") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Botón para guardar datos
        Button(
            onClick = {
                if (inputText.isNotEmpty()) {
                    saveDataToFirestore(db, inputText) { success ->
                        message = if (success) {
                            "Datos guardados exitosamente"
                        } else {
                            "Error al guardar datos"
                        }
                    }
                }
            }
        ) {
            Text("Guardar en Firestore")
        }
        
        // Botón para leer datos
        Button(
            onClick = {
                readDataFromFirestore(db) { data ->
                    savedData = data ?: "No hay datos guardados"
                    message = "Datos leídos de Firestore"
                }
            }
        ) {
            Text("Leer de Firestore")
        }
        
        if (savedData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Último dato guardado:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = savedData,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

// Función para guardar datos en Firestore
private fun saveDataToFirestore(
    db: com.google.firebase.firestore.FirebaseFirestore,
    text: String,
    onComplete: (Boolean) -> Unit
) {
    val data = hashMapOf(
        "text" to text,
        "timestamp" to com.google.firebase.Timestamp.now()
    )
    
    db.collection("demo_data")
        .add(data)
        .addOnSuccessListener { documentReference ->
            Log.d("Firestore", "Documento creado con ID: ${documentReference.id}")
            onComplete(true)
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error al agregar documento", e)
            onComplete(false)
        }
}

// Función para leer datos de Firestore
private fun readDataFromFirestore(
    db: com.google.firebase.firestore.FirebaseFirestore,
    onComplete: (String?) -> Unit
) {
    db.collection("demo_data")
        .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val document = documents.first()
                val text = document.getString("text")
                Log.d("Firestore", "Datos leídos: $text")
                onComplete(text)
            } else {
                Log.d("Firestore", "No hay documentos")
                onComplete(null)
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Firestore", "Error al obtener documentos", exception)
            onComplete(null)
        }
}

@Preview(showBackground = true)
@Composable
fun FirestoreDemoPreview() {
    TrabajoPractico1LOAMTheme {
        // Preview con mock de Firebase
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Firebase Firestore Demo",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Firebase configurado correctamente")
            OutlinedTextField(
                value = "",
                onValueChange = { },
                label = { Text("Texto a guardar") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { }) {
                Text("Guardar en Firestore")
            }
            Button(onClick = { }) {
                Text("Leer de Firestore")
            }
        }
    }
}