package com.loam.trabajopractico1loam.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loam.trabajopractico1loam.R

@Composable
fun HomeScreen(
    onNavigateToPrecios: () -> Unit,
    onNavigateToDemo: () -> Unit,
    onNavigateToSection3: () -> Unit,
    onNavigateToSection4: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            text = "Menú Principal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
        MenuButton(
            text = "Precios de Referencia",
            color = Color(0xFF4CAF50),
            iconRes = R.drawable.ic_launcher_background,
            onClick = onNavigateToPrecios
        )
        MenuButton(
            text = "Demo Firebase",
            color = Color(0xFF2196F3),
            iconRes = R.drawable.ic_launcher_background,
            onClick = onNavigateToDemo
        )
        MenuButton(
            text = "Sección 3",
            color = Color(0xFFFFC107),
            iconRes = R.drawable.ic_launcher_background,
            onClick = onNavigateToSection3
        )
        MenuButton(
            text = "Sección 4",
            color = Color(0xFFF44336),
            iconRes = R.drawable.ic_launcher_background,
            onClick = onNavigateToSection4
        )
    }
}

@Composable
fun MenuButton(text: String, color: Color, iconRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
