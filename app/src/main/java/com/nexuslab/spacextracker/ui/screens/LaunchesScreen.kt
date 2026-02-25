package com.nexuslab.spacextracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.datetime.*
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.presentation.viewmodel.LaunchesViewModel

/**
 * LaunchesScreen - Pantalla de lanzamientos SpaceX
 * 
 * Actualizada para Día 3 del curso Nexus Lab - 24/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * 🚀 NUEVAS CARACTERÍSTICAS DÍA 3:
 * - Conexión REAL con API SpaceX (adiós datos mock!)
 * - Cards rediseñadas con Material 3 avanzado
 * - Imágenes de parches de misiones (Coil)
 * - Estados de carga con animaciones
 * - Mejor gestión de errores
 * - Filtros mejorados con chips interactivos
 * - Layout responsive y accesible
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchesScreen(
    onLaunchClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: LaunchesViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf(LaunchFilter.ALL) }
    val uiState by viewModel.uiState.collectAsState()
    
    // Cargar datos al inicializar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadLaunches()
    }
    
    // Filtrar lanzamientos según el filtro seleccionado
    val filteredLaunches = when (selectedFilter) {
        LaunchFilter.ALL -> uiState.launches
        LaunchFilter.UPCOMING -> uiState.launches.filter { it.upcoming }
        LaunchFilter.SUCCESS -> uiState.launches.filter { it.success == true }
        LaunchFilter.FAILED -> uiState.launches.filter { it.success == false }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header animado
        AnimatedHeader()
        
        // Estado de conexión API
        ApiConnectionBanner(
            isLoading = uiState.isLoading,
            error = uiState.error,
            totalLaunches = uiState.launches.size,
            onRetry = { viewModel.loadLaunches() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Filtros mejorados
        LaunchFilterRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            launchCounts = LaunchCounts(
                total = uiState.launches.size,
                upcoming = uiState.launches.count { it.upcoming },
                success = uiState.launches.count { it.success == true },
                failed = uiState.launches.count { it.success == false }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Contenido principal con estados
        when {
            uiState.isLoading && uiState.launches.isEmpty() -> {
                LoadingState()
            }
            uiState.error != null && uiState.launches.isEmpty() -> {
                ErrorState(
                    error = uiState.error,
                    onRetry = { viewModel.loadLaunches() }
                )
            }
            filteredLaunches.isEmpty() -> {
                EmptyLaunchesState(filter = selectedFilter)
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredLaunches, key = { it.id }) { launch ->
                        AnimatedLaunchCard(
                            launch = launch,
                            onClick = { onLaunchClick(launch.id) }
                        )
                    }
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

data class LaunchCounts(
    val total: Int,
    val upcoming: Int,
    val success: Int,
    val failed: Int
)

@Composable
private fun AnimatedHeader() {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(800, easing = EaseOutBounce)
        ) + fadeIn(animationSpec = tween(800))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🚀",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Column {
                Text(
                    text = "SpaceX Launches",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Datos en vivo desde la API oficial",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alpha(0.8f)
                )
            }
        }
    }
}

@Composable
private fun ApiConnectionBanner(
    isLoading: Boolean,
    error: String?,
    totalLaunches: Int,
    onRetry: () -> Unit
) {
    AnimatedVisibility(
        visible = isLoading || error != null || totalLaunches > 0,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    error != null -> MaterialTheme.colorScheme.errorContainer
                    isLoading -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Cargando datos de SpaceX...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    error != null -> {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Error de conexión",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Conectado",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "✨ $totalLaunches lanzamientos cargados desde SpaceX API",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchFilterRow(
    selectedFilter: LaunchFilter,
    onFilterSelected: (LaunchFilter) -> Unit,
    launchCounts: LaunchCounts
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LaunchFilter.values().forEach { filter ->
            val count = when (filter) {
                LaunchFilter.ALL -> launchCounts.total
                LaunchFilter.UPCOMING -> launchCounts.upcoming
                LaunchFilter.SUCCESS -> launchCounts.success
                LaunchFilter.FAILED -> launchCounts.failed
            }
            
            FilterChip(
                onClick = { onFilterSelected(filter) },
                label = { 
                    Text("${filter.label} ($count)")
                },
                selected = selectedFilter == filter,
                leadingIcon = if (selectedFilter == filter) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        
        Text(
            text = "🚀 Conectando con SpaceX...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = "Descargando datos de lanzamientos en tiempo real",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayMedium
        )
        
        Text(
            text = "Error de conexión",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f)
        )
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar conexión")
        }
    }
}

@Composable
private fun AnimatedLaunchCard(
    launch: Launch,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(launch.id) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 4 }
        ) + fadeIn(animationSpec = tween(300)),
        modifier = modifier
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header con imagen de parche
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Imagen del parche de la misión
                    launch.links.patch?.small?.let { patchUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(patchUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Parche de ${launch.name}",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } ?: run {
                        // Placeholder cuando no hay parche
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🚀",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                    
                    // Información principal
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = launch.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        // Rocket chip
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "🚗 ${launch.rocketId}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                    
                    // Estado del lanzamiento
                    LaunchStatusBadge(launch = launch)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Descripción (si existe)
                launch.details?.let { details ->
                    Text(
                        text = details,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                // Información de fecha y enlaces
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fecha
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Fecha",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            text = formatLaunchDate(launch.dateUtc),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // Enlaces disponibles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        launch.links.video?.let {
                            IconButton(
                                onClick = { /* TODO: Abrir video */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Ver video",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        
                        launch.links.wikipedia?.let {
                            IconButton(
                                onClick = { /* TODO: Abrir Wikipedia */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Ver en Wikipedia",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
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
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
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
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val year = localDateTime.year
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        
        "$day/$month/$year $hour:$minute"
    } catch (e: Exception) {
        // Fallback para fechas con formato incorrecto
        dateUtc.take(10).replace("-", "/")
    }
}