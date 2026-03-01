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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexuslab.spacextracker.presentation.viewmodel.ThemeViewModel

/**
 * Pantalla de configuración - Generado por Nexus Platform
 * 
 * Día 8 del curso: Dark Mode + Theming SpaceX
 * 
 * Permite al usuario:
 * - Cambiar entre modo oscuro/claro
 * - Seguir el tema del sistema
 * - Activar colores dinámicos (Material You)
 * - Ver información sobre la app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val themeState by themeViewModel.themeState.collectAsState(
        initial = ThemeViewModel.ThemeState(
            isDarkTheme = true,
            isDynamicColorEnabled = false,
            isFollowingSystem = true,
            userPreferredTheme = null
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = "🎨 Configuración",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Sección de tema
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🌙 Apariencia",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                // Seguir tema del sistema
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Seguir tema del sistema",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Cambia automáticamente entre claro/oscuro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = themeState.isFollowingSystem,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                themeViewModel.followSystemTheme()
                            } else {
                                // Si no sigue el sistema, usar el tema actual
                                val currentDarkTheme = themeViewModel.shouldUseDarkTheme()
                                themeViewModel.setTheme(currentDarkTheme)
                            }
                        }
                    )
                }

                // Selector de tema manual (solo si no sigue el sistema)
                if (!themeState.isFollowingSystem) {
                    Divider()
                    
                    Text(
                        text = "Tema manual",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Botón tema claro
                        OutlinedButton(
                            onClick = { themeViewModel.setTheme(false) },
                            modifier = Modifier.weight(1f),
                            colors = if (themeState.userPreferredTheme == false) {
                                ButtonDefaults.buttonColors()
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Icon(Icons.Default.LightMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Claro")
                        }

                        // Botón tema oscuro
                        OutlinedButton(
                            onClick = { themeViewModel.setTheme(true) },
                            modifier = Modifier.weight(1f),
                            colors = if (themeState.userPreferredTheme == true) {
                                ButtonDefaults.buttonColors()
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Icon(Icons.Default.DarkMode, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Oscuro")
                        }
                    }
                }

                Divider()

                // Colores dinámicos (Material You)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Colores dinámicos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Usa colores de tu fondo de pantalla (Android 12+)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = themeState.isDynamicColorEnabled,
                        onCheckedChange = { themeViewModel.toggleDynamicColor() }
                    )
                }
            }
        }

        // Información SpaceX Theme
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🚀 Tema SpaceX",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "Esta app usa la identidad visual oficial de SpaceX con:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("• Negro espacial profundo", style = MaterialTheme.typography.bodySmall)
                    Text("• Azul cohete vibrante", style = MaterialTheme.typography.bodySmall)
                    Text("• Tipografía moderna", style = MaterialTheme.typography.bodySmall)
                    Text("• Diseño minimalista", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Información de la aplicación
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "ℹ️ Acerca de",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "SpaceX Tracker v1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Desarrollado como parte del curso Nexus Lab",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Powered by Nexus Platform 🤖",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Botón de toggle rápido (para demostración)
        Button(
            onClick = { themeViewModel.toggleTheme() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (themeViewModel.shouldUseDarkTheme()) {
                    Icons.Default.LightMode
                } else {
                    Icons.Default.DarkMode
                },
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (themeViewModel.shouldUseDarkTheme()) {
                    "Cambiar a tema claro"
                } else {
                    "Cambiar a tema oscuro"
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}