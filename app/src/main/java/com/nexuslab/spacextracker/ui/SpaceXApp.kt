package com.nexuslab.spacextracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.nexuslab.spacextracker.ui.screens.*

/**
 * SpaceXApp - Aplicación principal con navegación
 * 
 * Generada automáticamente por Nexus Platform el 22/02/2026
 * 
 * Características implementadas:
 * - Navegación inferior con 4 pantallas principales
 * - Estado de navegación reactivo
 * - Iconografía Material Icons
 * - Transiciones fluidas entre pantallas
 */

// Definición de rutas de navegación
enum class SpaceXScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Default.Home),
    LAUNCHES("launches", "Launches", Icons.Default.RocketLaunch),
    ROCKETS("rockets", "Rockets", Icons.Default.Rocket),
    ABOUT("about", "About", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceXApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                SpaceXScreen.values().forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            ) 
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route 
                        } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SpaceXScreen.HOME.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(SpaceXScreen.HOME.route) {
                HomeScreen()
            }
            composable(SpaceXScreen.LAUNCHES.route) {
                LaunchesScreen()
            }
            composable(SpaceXScreen.ROCKETS.route) {
                RocketsScreen()
            }
            composable(SpaceXScreen.ABOUT.route) {
                AboutScreen()
            }
        }
    }
}