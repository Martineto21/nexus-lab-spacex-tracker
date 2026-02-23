package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslab.spacextracker.presentation.viewmodel.LaunchesViewModel

/**
 * HomeScreen - Pantalla principal de bienvenida
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * Características:
 * - Logo SpaceX centralizado
 * - Información de bienvenida
 * - Cards de navegación rápida
 * - Estadísticas en tiempo real (próxima actualización)
 * - Diseño responsive y accesible
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LaunchesViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logo y título principal
        WelcomeHeader()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Datos en vivo de SpaceX API
        SpaceXDataCard(uiState, onRetry = { viewModel.loadLaunches() })
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Información sobre la app
        AppInfoCard()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Cards de navegación rápida
        QuickNavigationSection()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Información de Nexus Platform
        NexusPlatformCard()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun WelcomeHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji como logo temporal (hasta añadir el logo real)
        Text(
            text = "🚀",
            fontSize = 72.sp,
        )
        
        Text(
            text = "SpaceX Tracker",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Explora el universo de SpaceX",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Composable
private fun SpaceXDataCard(
    uiState: com.nexuslab.spacextracker.presentation.viewmodel.LaunchesUiState,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📡",
                    fontSize = 20.sp
                )
                Text(
                    text = "API SpaceX en Vivo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            when {
                uiState.isLoading -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Conectando con SpaceX API...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                uiState.error != null -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "❌ Error: ${uiState.error}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🔄 Reintentar conexión")
                        }
                    }
                }
                
                uiState.launches.isNotEmpty() -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "✅ Conectado exitosamente!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatChip(
                                label = "Total Lanzamientos",
                                value = "${uiState.launches.size}",
                                emoji = "🚀"
                            )
                            
                            StatChip(
                                label = "Exitosos",
                                value = "${uiState.launches.count { it.success == true }}",
                                emoji = "✅"
                            )
                        }
                        
                        val latestLaunch = uiState.launches.maxByOrNull { it.dateUtc }
                        latestLaunch?.let { launch ->
                            Text(
                                text = "🆕 Último: ${launch.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.alpha(0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    emoji: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 12.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🎯 Características de la App",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            val features = listOf(
                "🚀 Launches - Próximos y pasados lanzamientos",
                "🚗 Rockets - Información detallada de cohetes",
                "📊 Datos en tiempo real de la API SpaceX",
                "🌙 Tema oscuro/claro automático",
                "📱 Diseño Material 3 moderno"
            )
            
            features.forEach { feature ->
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
    }
}

@Composable
private fun QuickNavigationSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "🧭 Navegación Rápida",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickNavCard(
                title = "Launches",
                emoji = "🚀",
                description = "Ver lanzamientos",
                modifier = Modifier.weight(1f)
            )
            
            QuickNavCard(
                title = "Rockets",
                emoji = "🚗",
                description = "Explorar cohetes",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    emoji: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.7f)
            )
        }
    }
}

@Composable
private fun NexusPlatformCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🤖 Creado con Nexus Platform",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Text(
                text = "Esta app fue desarrollada 100% por agentes IA usando Nexus Platform. " +
                      "Una demostración en vivo de lo que la automatización puede lograr.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.alpha(0.9f)
            )
            
            Text(
                text = "🌐 kalmiazen.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}