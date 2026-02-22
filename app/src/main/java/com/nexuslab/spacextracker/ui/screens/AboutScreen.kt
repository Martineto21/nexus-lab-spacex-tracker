package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * AboutScreen - Pantalla de información sobre la app
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * Agente responsable: Upe (Memoria Viva)
 * 
 * Características implementadas:
 * - Información sobre SpaceX Tracker App
 * - Detalles del curso Nexus Lab
 * - Información de Nexus Platform
 * - Enlaces de contacto y recursos
 * - Reconocimientos y créditos
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "ℹ️ Acerca de",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // App Info
        AppInfoSection()
        
        // Nexus Lab Course
        NexusLabSection()
        
        // Nexus Platform
        NexusPlatformSection()
        
        // Technical Details
        TechnicalDetailsSection()
        
        // Contact & Resources
        ContactSection()
        
        // Credits
        CreditsSection()
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AppInfoSection() {
    InfoCard(
        title = "🚀 SpaceX Tracker App",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Una aplicación Android moderna para explorar el universo de SpaceX. " +
                          "Desarrollada 100% utilizando agentes IA de Nexus Platform como " +
                          "demostración en vivo de las capacidades de automatización.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.9f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoChip("Versión 1.0", Icons.Default.Info)
                    InfoChip("Android 7+", Icons.Default.Android)
                    InfoChip("Material 3", Icons.Default.Palette)
                }
            }
        }
    )
}

@Composable
private fun NexusLabSection() {
    InfoCard(
        title = "🎓 Nexus Lab - Curso Gratuito",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Esta app forma parte del curso gratuito 'Construye una SpaceX Tracker App' " +
                          "donde enseñamos a crear aplicaciones completas usando IA en 10 días.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.9f)
                )
                
                val features = listOf(
                    "📅 10 días de contenido práctico",
                    "🤖 Desarrollo con agentes IA",
                    "📱 Kotlin + Jetpack Compose",
                    "🔄 Publicaciones diarias 10:00 y 18:00",
                    "💬 Soporte en grupo WhatsApp",
                    "🎯 Proyecto real y funcional"
                )
                
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🔗 Únete al curso:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "WhatsApp: Nexus Dev Community",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun NexusPlatformSection() {
    InfoCard(
        title = "🤖 Nexus Platform",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Plataforma revolucionaria que permite crear aplicaciones completas " +
                          "utilizando agentes IA. Sin necesidad de programar manualmente.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.9f)
                )
                
                val benefits = listOf(
                    "⚡ Desarrollo 10x más rápido",
                    "🧠 Agentes IA especializados",
                    "🔧 126 agentes disponibles",
                    "🛠️ 116 skills automatizadas",
                    "🌐 API completa y extensible",
                    "💰 ROI inmediato"
                )
                
                benefits.forEach { benefit ->
                    Text(
                        text = benefit,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🌐 Descubre más:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "kalmiazen.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "app.kalmiazen.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun TechnicalDetailsSection() {
    InfoCard(
        title = "⚙️ Detalles Técnicos",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tecnologías y arquitectura utilizadas:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(0.9f)
                )
                
                val technologies = mapOf(
                    "Lenguaje" to "Kotlin 1.9.22",
                    "UI Framework" to "Jetpack Compose",
                    "Arquitectura" to "MVVM",
                    "Navegación" to "Navigation Compose",
                    "Tema" to "Material 3",
                    "API" to "SpaceX REST API",
                    "Red" to "Retrofit + OkHttp",
                    "Imágenes" to "Coil",
                    "Target SDK" to "34 (Android 14)"
                )
                
                technologies.forEach { (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "• $key:",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ContactSection() {
    InfoCard(
        title = "📞 Contacto & Recursos",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ContactItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = "ramac21@gmail.com"
                )
                
                ContactItem(
                    icon = Icons.Default.Language,
                    label = "Web",
                    value = "kalmiazen.com"
                )
                
                ContactItem(
                    icon = Icons.Default.Code,
                    label = "GitHub",
                    value = "github.com/Martineto21"
                )
                
                ContactItem(
                    icon = Icons.Default.Chat,
                    label = "WhatsApp",
                    value = "+34 640 099 670"
                )
            }
        }
    )
}

@Composable
private fun CreditsSection() {
    InfoCard(
        title = "🏆 Créditos & Reconocimientos",
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Esta aplicación fue posible gracias a:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.9f)
                )
                
                val credits = listOf(
                    "🤖 Upe - Agente IA (Memoria Viva de Rafa)",
                    "🚀 SpaceX - Por la API pública y los datos",
                    "📱 Google - Por Jetpack Compose y Material 3",
                    "🧠 Anthropic - Por Claude (motor de IA)",
                    "💻 Nexus Platform - Por hacer posible la automatización",
                    "👨‍💻 Rafael Martínez - Creador de Nexus Platform"
                )
                
                credits.forEach { credit ->
                    Text(
                        text = credit,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "\"VENDEMOS PREDICANDO CON EL EJEMPLO\"",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Generado automáticamente el 22/02/2026",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.6f)
                )
            }
        }
    )
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            content()
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    icon: ImageVector
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ContactItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(0.8f)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}