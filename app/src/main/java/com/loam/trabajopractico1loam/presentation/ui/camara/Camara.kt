package com.loam.trabajopractico1loam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.loam.trabajopractico1loam.utils.ManejadorCamara
import kotlin.apply

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Camara() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    // Estados
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isRecording by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isBackCamera by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    // Inicializa manejador solo cuando tengamos permisos
    val manejadorCamara = remember(context, previewView, lifecycleOwner) {
        ManejadorCamara(context, previewView, lifecycleOwner)
    }

    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            manejadorCamara.prenderCamara()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            manejadorCamara.liberarRecursos()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        manejadorCamara.toggleFlash()
                        isFlashOn = !isFlashOn
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (isFlashOn) Color(0xFF2196F3).copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Filled.KeyboardArrowUp else Icons.Filled.Add,
                        contentDescription = if (isFlashOn) "Flash activado" else "Flash desactivado",
                        tint = if (isFlashOn) Color.White else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (isRecording) {
                    Card(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFE53E3E), Color(0xFFFF6B6B))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(Color.White, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "● GRABANDO",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f),
                                Color.Black.copy(alpha = 0.95f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val lastUri = manejadorCamara.getLastSavedImageUri()
                            if (lastUri != null) {
                                // Abrir la última imagen con ACTION_VIEW
                                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(lastUri, "image/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(viewIntent)
                            } else {
                                val galleryIntent = Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                                galleryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(galleryIntent)
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50).copy(alpha = 0.3f),
                                        Color(0xFF2196F3).copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Galería",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(
                                    brush = if (isRecording) {
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFFFF4444), Color(0xFFCC0000))
                                        )
                                    } else {
                                        Brush.radialGradient(
                                            colors = listOf(Color.White, Color(0xFFF0F0F0))
                                        )
                                    },
                                    shape = CircleShape
                                )
                        )

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    color = if (isRecording) Color.White.copy(alpha = 0.9f) else Color(0xFF1976D2).copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                        )

                        IconButton(
                            onClick = {
                                if (isRecording) {
                                    manejadorCamara.detenerGrabacion()
                                    isRecording = false
                                } else {
                                    manejadorCamara.tomarFoto()
                                }
                            },
                            modifier = Modifier
                                .size(68.dp)
                                .background(
                                    color = if (isRecording) Color.Transparent else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            if (!isRecording) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color.White, Color(0xFFE0E0E0))
                                            ),
                                            shape = CircleShape
                                        )
                                        .clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFFFF4444), Color(0xFFCC0000))
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            manejadorCamara.cambiarCamara()
                            isBackCamera = !isBackCamera
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF9800).copy(alpha = 0.3f),
                                        Color(0xFFF57C00).copy(alpha = 0.2f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Cambiar cámara",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            if (!isRecording) {
                                manejadorCamara.empezarGrabacion()
                                isRecording = true
                            }
                        },
                        modifier = Modifier
                            .background(
                                brush = if (isRecording) {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFE53E3E), Color(0xFFFF6B6B))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color.Transparent)
                                    )
                                },
                                shape = RoundedCornerShape(25.dp)
                            )
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Video",
                            tint = if (isRecording) Color.White else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIDEO",
                            color = if (isRecording) Color.White else Color.White.copy(alpha = 0.8f),
                            fontSize = 15.sp,
                            fontWeight = if (isRecording) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    // Modo Foto (seleccionado por defecto)
                    TextButton(
                        onClick = { /* Ya en modo foto */ },
                        modifier = Modifier
                            .background(
                                brush = if (!isRecording) {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF4CAF50), Color(0xFF66BB6A))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, Color.Transparent)
                                    )
                                },
                                shape = RoundedCornerShape(25.dp)
                            )
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = "Foto",
                            tint = if (!isRecording) Color.White else Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FOTO",
                            color = if (!isRecording) Color.White else Color.White.copy(alpha = 0.8f),
                            fontSize = 15.sp,
                            fontWeight = if (!isRecording) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Permisos de Cámara",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se necesitan permisos de cámara y audio para usar esta función.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.RECORD_AUDIO
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Otorgar Permisos")
                        }
                    }
                }
            }
        }
    }
}