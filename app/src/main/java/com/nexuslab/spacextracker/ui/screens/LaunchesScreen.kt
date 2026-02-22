package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*

/**
 * LaunchesScreen - Pantalla de lanzamientos SpaceX
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * Características implementadas:
 * - Lista de lanzamientos con datos mock
 * - Estados: próximos, completados, fallidos
 * - Información detallada de cada misión
 * - Próximamente: conexión con SpaceX API real
 * - Filtros y búsqueda (próxima actualización)
 */

// Modelo de datos temporal (será reemplazado por API real)
data class Launch(
    val id: String,
    val name: String,
    val description: String,
    val dateUtc: String,
    val rocket: String,
    val success: Boolean?,
    val upcoming: Boolean,
    val patch: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchesScreen(
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(LaunchFilter.ALL) }
    val launches = getSampleLaunches()
    
    val filteredLaunches = when (selectedFilter) {
        LaunchFilter.ALL -> launches
        LaunchFilter.UPCOMING -> launches.filter { it.upcoming }
        LaunchFilter.SUCCESS -> launches.filter { it.success == true }
        LaunchFilter.FAILED -> launches.filter { it.success == false }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "🚀 Lanzamientos SpaceX",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Filtros
        LaunchFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de lanzamientos
        if (filteredLaunches.isEmpty()) {
            EmptyLaunchesState(filter = selectedFilter)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredLaunches) { launch ->
                    LaunchCard(launch = launch)
                }
            }
        }
    }
}

enum class LaunchFilter(val label: String) {
    ALL("Todos"),
    UPCOMING("Próximos"),
    SUCCESS("Exitosos"),
    FAILED("Fallidos")
}

@Composable
private fun LaunchFilterRow(
    selectedFilter: LaunchFilter,
    onFilterSelected: (LaunchFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LaunchFilter.values().forEach { filter ->
            FilterChip(
                onClick = { onFilterSelected(filter) },
                label = { Text(filter.label) },
                selected = selectedFilter == filter
            )
        }
    }
}

@Composable
private fun LaunchCard(
    launch: Launch,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header del launch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = launch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = launch.rocket,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                
                // Estado del launch
                LaunchStatusBadge(launch = launch)
            }
            
            // Descripción
            Text(
                text = launch.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = formatLaunchDate(launch.dateUtc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LaunchStatusBadge(launch: Launch) {
    val (icon, color, text) = when {
        launch.upcoming -> Triple(
            Icons.Default.Schedule,
            MaterialTheme.colorScheme.tertiary,
            "Próximo"
        )
        launch.success == true -> Triple(
            Icons.Default.CheckCircle,
            Color(0xFF26A641),
            "Exitoso"
        )
        launch.success == false -> Triple(
            Icons.Default.Error,
            MaterialTheme.colorScheme.error,
            "Fallido"
        )
        else -> Triple(
            Icons.Default.Schedule,
            MaterialTheme.colorScheme.outline,
            "Desconocido"
        )
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(12.dp),
                tint = color
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyLaunchesState(filter: LaunchFilter) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when (filter) {
                LaunchFilter.ALL -> "No hay lanzamientos disponibles"
                LaunchFilter.UPCOMING -> "No hay lanzamientos próximos"
                LaunchFilter.SUCCESS -> "No hay lanzamientos exitosos"
                LaunchFilter.FAILED -> "No hay lanzamientos fallidos"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Los datos se cargarán desde la API de SpaceX",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

// Funciones de utilidad
private fun formatLaunchDate(dateUtc: String): String {
    return try {
        val instant = Instant.parse(dateUtc)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}/${localDateTime.year}"
    } catch (e: Exception) {
        dateUtc.take(10)  // Fallback a formato simple
    }
}

// Datos de ejemplo (serán reemplazados por API real)
private fun getSampleLaunches(): List<Launch> {
    return listOf(
        Launch(
            id = "1",
            name = "Starship IFT-3",
            description = "Tercer vuelo de prueba integrado del sistema Starship con objetivos de transferencia de propelente.",
            dateUtc = "2024-03-14T13:25:00.000Z",
            rocket = "Starship",
            success = true,
            upcoming = false
        ),
        Launch(
            id = "2", 
            name = "Starlink Group 6-40",
            description = "Despliegue de 23 satélites Starlink v2.0 a órbita terrestre baja desde Cabo Cañaveral.",
            dateUtc = "2024-02-25T07:30:00.000Z",
            rocket = "Falcon 9",
            success = true,
            upcoming = false
        ),
        Launch(
            id = "3",
            name = "Europa Clipper",
            description = "Misión de la NASA para explorar la luna Europa de Júpiter y su océano subsuperficial.",
            dateUtc = "2024-10-10T16:06:00.000Z",
            rocket = "Falcon Heavy",
            success = null,
            upcoming = true
        ),
        Launch(
            id = "4",
            name = "Crew-8",
            description = "Octava misión comercial de tripulación a la Estación Espacial Internacional.",
            dateUtc = "2024-03-03T10:53:00.000Z",
            rocket = "Falcon 9",
            success = true,
            upcoming = false
        ),
        Launch(
            id = "5",
            name = "Artemis 3",
            description = "Primera misión tripulada a la Luna en el programa Artemis. Aterrizaje en el polo sur lunar.",
            dateUtc = "2026-12-01T00:00:00.000Z",
            rocket = "Starship HLS",
            success = null,
            upcoming = true
        )
    )
}