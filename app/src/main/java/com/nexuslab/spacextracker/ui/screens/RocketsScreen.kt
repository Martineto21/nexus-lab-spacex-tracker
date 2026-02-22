package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * RocketsScreen - Pantalla de cohetes SpaceX
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * Características implementadas:
 * - Lista de cohetes SpaceX con especificaciones técnicas
 * - Datos reales de Falcon 9, Falcon Heavy, Starship
 * - Información detallada: altura, masa, capacidad de carga
 * - Estado activo/retirado de cada cohete
 * - Próximamente: conexión con SpaceX API real
 */

// Modelo de datos para cohetes
data class Rocket(
    val id: String,
    val name: String,
    val description: String,
    val height: Double, // metros
    val mass: Double,   // toneladas
    val payloadToLEO: Double, // kg
    val active: Boolean,
    val firstFlight: String,
    val successRate: Double, // porcentaje
    val costPerLaunch: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RocketsScreen(
    modifier: Modifier = Modifier
) {
    val rockets = getSampleRockets()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Text(
            text = "🚗 Cohetes SpaceX",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Información general
        InfoCard()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de cohetes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(rockets) { rocket ->
                RocketCard(rocket = rocket)
            }
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "ℹ️ Sobre los Cohetes SpaceX",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "SpaceX ha revolucionado la industria espacial con cohetes reutilizables " +
                      "que reducen dramáticamente el costo de acceso al espacio.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.9f)
            )
        }
    }
}

@Composable
private fun RocketCard(
    rocket: Rocket,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header del cohete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rocket.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Column {
                        Text(
                            text = rocket.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Primer vuelo: ${rocket.firstFlight}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.alpha(0.8f)
                        )
                    }
                }
                
                // Estado activo
                RocketStatusBadge(active = rocket.active)
            }
            
            // Descripción
            Text(
                text = rocket.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.alpha(0.9f)
            )
            
            // Especificaciones técnicas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SpecItem(
                    icon = Icons.Default.Height,
                    label = "Altura",
                    value = "${rocket.height} m",
                    modifier = Modifier.weight(1f)
                )
                
                SpecItem(
                    icon = Icons.Default.MonitorWeight,
                    label = "Masa",
                    value = "${rocket.mass.toInt()} t",
                    modifier = Modifier.weight(1f)
                )
                
                SpecItem(
                    icon = Icons.Default.Speed,
                    label = "Carga LEO",
                    value = "${rocket.payloadToLEO.toInt()} kg",
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Información adicional
            Divider(modifier = Modifier.alpha(0.3f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tasa de éxito",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = "${rocket.successRate}%",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = when {
                            rocket.successRate >= 95.0 -> MaterialTheme.colorScheme.primary
                            rocket.successRate >= 85.0 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Costo por lanzamiento",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.alpha(0.7f)
                    )
                    Text(
                        text = rocket.costPerLaunch,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.7f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RocketStatusBadge(active: Boolean) {
    val (color, text) = if (active) {
        MaterialTheme.colorScheme.primary to "Activo"
    } else {
        MaterialTheme.colorScheme.outline to "Retirado"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Datos de ejemplo basados en información real de SpaceX
private fun getSampleRockets(): List<Rocket> {
    return listOf(
        Rocket(
            id = "falcon9",
            name = "Falcon 9",
            description = "Cohete de dos etapas reutilizable diseñado y fabricado por SpaceX. " +
                    "Es el caballo de batalla de la flota SpaceX, utilizado para misiones Starlink, " +
                    "Crew Dragon y cargas comerciales.",
            height = 70.0,
            mass = 549.0,
            payloadToLEO = 22800.0,
            active = true,
            firstFlight = "2010",
            successRate = 98.9,
            costPerLaunch = "$67M",
            emoji = "🚀"
        ),
        Rocket(
            id = "falconheavy",
            name = "Falcon Heavy",
            description = "Cohete súper pesado de tres núcleos basado en el Falcon 9. " +
                    "Actualmente el cohete operacional más potente del mundo por un factor de dos, " +
                    "capaz de llevar cargas masivas a órbita.",
            height = 70.0,
            mass = 1420.0,
            payloadToLEO = 63800.0,
            active = true,
            firstFlight = "2018",
            successRate = 100.0,
            costPerLaunch = "$97M",
            emoji = "🚗"
        ),
        Rocket(
            id = "starship",
            name = "Starship",
            description = "El próximo sistema de transporte espacial de SpaceX, completamente reutilizable. " +
                    "Diseñado para llevar hasta 100 personas a Marte y revolucionar los viajes espaciales.",
            height = 120.0,
            mass = 5000.0,
            payloadToLEO = 150000.0,
            active = false, // En desarrollo
            firstFlight = "2023",
            successRate = 33.3, // Aún en pruebas
            costPerLaunch = "$10M*",
            emoji = "🛸"
        ),
        Rocket(
            id = "falcon1",
            name = "Falcon 1",
            description = "El primer cohete desarrollado por SpaceX. Pequeño y expendable, " +
                    "fue utilizado para demostrar las capacidades iniciales de la empresa " +
                    "antes del desarrollo del Falcon 9.",
            height = 22.3,
            mass = 38.6,
            payloadToLEO = 670.0,
            active = false,
            firstFlight = "2006",
            successRate = 40.0,
            costPerLaunch = "$7M",
            emoji = "🎯"
        )
    )
}