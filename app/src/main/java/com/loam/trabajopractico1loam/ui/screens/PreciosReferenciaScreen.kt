package com.loam.trabajopractico1loam.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.loam.trabajopractico1loam.model.PrecioReferencia
import com.loam.trabajopractico1loam.model.TipoPrecio
import com.loam.trabajopractico1loam.viewmodel.PreciosViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreciosReferenciaScreen(
    viewModel: PreciosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Mostrar Snackbar para errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            // Aquí podrías mostrar un Snackbar si tuvieras un SnackbarHostState
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💰 Precios de Referencia",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Valores actualizados en tiempo real",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                
                // Indicador de actualización en tiempo real
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .padding(end = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(50),
                            color = Color.Green
                        ) {}
                    }
                    Text(
                        text = "Conectado a Firebase",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        // Estado de carga
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // Lista de precios
        AnimatedVisibility(
            visible = !uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.precios) { precio ->
                    PrecioCard(
                        precio = precio,
                        onActualizarPrecio = { nuevoPrecio ->
                            viewModel.actualizarPrecio(nuevoPrecio)
                        }
                    )
                }
                
                // Mensaje si no hay precios
                if (uiState.precios.isEmpty() && !uiState.isLoading) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "📊 Inicializando precios...\nEsto puede tomar unos segundos",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        
        // Mensaje de error
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "⚠️ $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecioCard(
    precio: PrecioReferencia,
    onActualizarPrecio: (PrecioReferencia) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showEditDialog = true },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono y título
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = precio.tipo.icono,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = precio.tipo.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = precio.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Precio principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formatPrice(precio.valor, precio.moneda),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "por ${precio.unidad}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            // Fecha de actualización
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Actualizado: ${formatDate(precio.fechaActualizacion.toDate())}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
    
    // Dialog para editar precio
    if (showEditDialog) {
        EditPrecioDialog(
            precio = precio,
            onDismiss = { showEditDialog = false },
            onConfirm = { nuevoPrecio ->
                onActualizarPrecio(nuevoPrecio)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPrecioDialog(
    precio: PrecioReferencia,
    onDismiss: () -> Unit,
    onConfirm: (PrecioReferencia) -> Unit
) {
    var nuevoValor by remember { mutableStateOf(precio.valor.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Actualizar ${precio.tipo.displayName}")
        },
        text = {
            Column {
                Text(
                    text = "Ingrese el nuevo precio:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nuevoValor,
                    onValueChange = { nuevoValor = it },
                    label = { Text("Precio (${precio.moneda})") },
                    suffix = { Text("/ ${precio.unidad}") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nuevoValor.toDoubleOrNull()?.let { valor ->
                        onConfirm(precio.copy(valor = valor))
                    }
                }
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Funciones de utilidad
private fun formatPrice(precio: Double, moneda: String): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(precio).replace("$", "$$moneda ")
}

private fun formatDate(date: Date): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}