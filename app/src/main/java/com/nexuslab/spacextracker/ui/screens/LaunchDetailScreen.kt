package com.nexuslab.spacextracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.datetime.*
import com.nexuslab.spacextracker.data.model.Launch
import com.nexuslab.spacextracker.presentation.viewmodel.LaunchDetailViewModel

/**
 * LaunchDetailScreen - Pantalla de detalle de lanzamiento SpaceX
 * 
 * Generada para Día 4 del curso Nexus Lab - 25/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * 🚀 CARACTERÍSTICAS DÍA 4 - PANTALLA DETALLE:
 * - Hero image con parallax scroll
 * - Información técnica completa del lanzamiento
 * - Galería de imágenes del cohete
 * - Enlaces externos (YouTube, Wikipedia, artículos)
 * - Datos del cohete y launchpad
 * - Timeline de la misión
 * - Botón de compartir
 * - Navegación fluida desde lista
 * - Carga de datos específicos del lanzamiento
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchDetailScreen(
    launchId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LaunchDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    
    // Cargar detalle del lanzamiento
    LaunchedEffect(launchId) {
        viewModel.loadLaunchDetail(launchId)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LaunchDetailLoadingState()
            }
            uiState.error != null -> {
                LaunchDetailErrorState(
                    error = uiState.error,
                    onRetry = { viewModel.loadLaunchDetail(launchId) },
                    onNavigateBack = onNavigateBack
                )
            }
            uiState.launch != null -> {
                LaunchDetailContent(
                    launch = uiState.launch,
                    scrollState = scrollState,
                    onLinkClick = { url -> 
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }
                )
            }
        }
        
        // Barra superior flotante
        LaunchDetailTopBar(
            title = uiState.launch?.name ?: "Cargando...",
            onNavigateBack = onNavigateBack,
            scrollProgress = (scrollState.value / 300f).coerceIn(0f, 1f)
        )
    }
}

@Composable
private fun LaunchDetailContent(
    launch: Launch,
    scrollState: ScrollState,
    onLinkClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Section con imagen
        LaunchHeroSection(launch = launch, scrollState = scrollState)
        
        // Contenido principal
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Información básica
            LaunchBasicInfo(launch = launch)
            
            // Descripción
            launch.details?.let { details ->
                LaunchDescription(details = details)
            }
            
            // Datos técnicos
            LaunchTechnicalData(launch = launch)
            
            // Timeline de la misión
            LaunchTimeline(launch = launch)
            
            // Galería de imágenes
            launch.links.flickr.original.takeIf { it.isNotEmpty() }?.let { images ->
                LaunchImageGallery(images = images)
            }
            
            // Enlaces externos
            LaunchExternalLinks(
                launch = launch,
                onLinkClick = onLinkClick
            )
            
            // Espaciado final para la barra de navegación
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun LaunchHeroSection(
    launch: Launch,
    scrollState: ScrollState
) {
    val parallaxOffset = (scrollState.value * 0.5f).toInt()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Imagen de fondo (parche o placeholder)
        val heroImage = launch.links.patch?.large 
            ?: launch.links.flickr.original.firstOrNull()
            
        if (heroImage != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(heroImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Imagen de ${launch.name}",
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-parallaxOffset).dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // Gradiente placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E),
                                Color(0xFF0F3460)
                            )
                        )
                    )
            )
        }
        
        // Overlay oscuro
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )
        
        // Información superpuesta
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Badge de estado
            LaunchStatusChip(launch = launch)
            
            // Nombre de la misión
            Text(
                text = launch.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            // Fecha y cohete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = formatLaunchDate(launch.dateUtc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
                
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🚗 ${launch.rocketId}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchBasicInfo(launch: Launch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "📊 Información General",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoChip(
                    icon = Icons.Default.FlightTakeoff,
                    label = "Número de vuelo",
                    value = launch.flightNumber.toString()
                )
                
                InfoChip(
                    icon = Icons.Default.DateRange,
                    label = "Año",
                    value = formatYear(launch.dateUtc)
                )
                
                InfoChip(
                    icon = if (launch.success == true) Icons.Default.CheckCircle 
                           else if (launch.success == false) Icons.Default.Cancel
                           else Icons.Default.Schedule,
                    label = "Estado",
                    value = when {
                        launch.upcoming -> "Próximo"
                        launch.success == true -> "Exitoso"
                        launch.success == false -> "Fallido"
                        else -> "N/A"
                    }
                )
            }
        }
    }
}

@Composable
private fun LaunchDescription(details: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Descripción",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Descripción de la Misión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = details,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
            )
        }
    }
}

@Composable
private fun LaunchTechnicalData(launch: Launch) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Engineering,
                    contentDescription = "Datos técnicos",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Datos Técnicos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TechnicalDataRow(
                    label = "ID del Lanzamiento",
                    value = launch.id
                )
                
                TechnicalDataRow(
                    label = "Cohete",
                    value = launch.rocketId
                )
                
                TechnicalDataRow(
                    label = "Número de vuelo",
                    value = "#${launch.flightNumber}"
                )
                
                TechnicalDataRow(
                    label = "Fecha UTC",
                    value = formatLaunchDateUTC(launch.dateUtc)
                )
                
                TechnicalDataRow(
                    label = "Ventana de lanzamiento",
                    value = launch.autoUpdate?.toString() ?: "No especificada"
                )
                
                TechnicalDataRow(
                    label = "Actualización automática",
                    value = if (launch.autoUpdate == true) "Activa" else "Inactiva"
                )
            }
        }
    }
}

@Composable
private fun LaunchTimeline(launch: Launch) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = "Timeline",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Timeline de la Misión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimelineItem(
                    title = "Fecha programada",
                    description = formatLaunchDate(launch.dateUtc),
                    isCompleted = !launch.upcoming
                )
                
                if (launch.upcoming) {
                    TimelineItem(
                        title = "Estado actual",
                        description = "Preparándose para el lanzamiento",
                        isCompleted = false
                    )
                } else {
                    TimelineItem(
                        title = "Lanzamiento ejecutado",
                        description = when (launch.success) {
                            true -> "Misión completada exitosamente"
                            false -> "Fallo en la misión"
                            null -> "Resultado pendiente"
                        },
                        isCompleted = launch.success == true
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchImageGallery(images: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Galería",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Galería de Imágenes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(images.take(10)) { imageUrl ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del lanzamiento",
                        modifier = Modifier
                            .width(200.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Composable
private fun LaunchExternalLinks(
    launch: Launch,
    onLinkClick: (String) -> Unit
) {
    val availableLinks = mutableListOf<Pair<String, String>>()
    
    launch.links.video?.let { availableLinks.add("Ver Video" to it) }
    launch.links.wikipedia?.let { availableLinks.add("Wikipedia" to it) }
    launch.links.article?.let { availableLinks.add("Artículo" to it) }
    
    if (availableLinks.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Enlaces",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Enlaces Externos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableLinks.forEach { (label, url) ->
                        OutlinedButton(
                            onClick = { onLinkClick(url) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = when {
                                    label.contains("Video") -> Icons.Default.PlayArrow
                                    label.contains("Wikipedia") -> Icons.Default.Info
                                    else -> Icons.Default.Article
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            }
        }
    }
}

// Componentes auxiliares
@Composable
private fun LaunchDetailTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    scrollProgress: Float
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(scrollProgress),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = if (scrollProgress > 0.5f) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = { /* TODO: Compartir */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Compartir",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun LaunchStatusChip(launch: Launch) {
    val (color, text) = when {
        launch.upcoming -> MaterialTheme.colorScheme.tertiary to "PRÓXIMO"
        launch.success == true -> Color(0xFF26A641) to "EXITOSO"
        launch.success == false -> MaterialTheme.colorScheme.error to "FALLIDO"
        else -> MaterialTheme.colorScheme.outline to "DESCONOCIDO"
    }
    
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alpha(0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TechnicalDataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.8f),
            fontWeight = FontWeight.Medium
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimelineItem(
    title: String,
    description: String,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Schedule,
            contentDescription = null,
            tint = if (isCompleted) Color(0xFF26A641) else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.8f)
            )
        }
    }
}

@Composable
private fun LaunchDetailLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "🚀 Cargando detalle del lanzamiento...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LaunchDetailErrorState(
    error: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Error al cargar el detalle",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onNavigateBack) {
                Text("Volver")
            }
            
            Button(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}

// Funciones auxiliares
private fun formatYear(dateUtc: String): String {
    return try {
        val instant = Instant.parse(dateUtc)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        localDateTime.year.toString()
    } catch (e: Exception) {
        "N/A"
    }
}

private fun formatLaunchDateUTC(dateUtc: String): String {
    return try {
        val instant = Instant.parse(dateUtc)
        val localDateTime = instant.toLocalDateTime(TimeZone.UTC)
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val year = localDateTime.year
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        
        "$day/$month/$year $hour:$minute UTC"
    } catch (e: Exception) {
        dateUtc
    }
}

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
        dateUtc.take(10).replace("-", "/")
    }
}