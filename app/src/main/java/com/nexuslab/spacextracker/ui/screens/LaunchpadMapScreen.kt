package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nexuslab.spacextracker.BuildConfig
import com.nexuslab.spacextracker.data.model.Launchpad
import com.nexuslab.spacextracker.presentation.viewmodel.LaunchpadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchpadMapScreen(
    viewModel: LaunchpadViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val hasMapsApiKey = BuildConfig.MAPS_API_KEY.isNotBlank()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(28.5721, -80.6480), 6f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasMapsApiKey) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false)
            ) {
                val launchpads: List<Launchpad> = uiState.launchpads
                for (launchpad in launchpads) {
                    Marker(
                        state = MarkerState(position = LatLng(launchpad.latitude, launchpad.longitude)),
                        title = launchpad.name,
                        snippet = "${launchpad.locality}, ${launchpad.region}",
                        onClick = {
                            viewModel.selectLaunchpad(launchpad)
                            false
                        }
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mapa no configurado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Agrega MAPS_API_KEY en local.properties para mostrar el mapa.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Cargando launchpads...")
                    }
                }
            }
        }

        uiState.error?.let { errorMessage: String ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Button(
                        onClick = { viewModel.retryLoading() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Reintentar")
                    }
                }
            }
        }

        uiState.selectedLaunchpad?.let { launchpad: Launchpad ->
            LaunchpadInfoCard(
                launchpad = launchpad,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                onDismiss = { viewModel.selectLaunchpad(null) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaunchpadInfoCard(
    launchpad: Launchpad,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = launchpad.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${launchpad.locality}, ${launchpad.region}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Text(text = "x")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(
                    label = "Lanzamientos",
                    value = launchpad.launchAttempts.toString()
                )
                StatChip(
                    label = "Exitos",
                    value = launchpad.launchSuccesses.toString()
                )
                StatChip(
                    label = "Exito",
                    value = "${launchpad.successRate.toInt()}%"
                )
            }

            launchpad.details?.let { details ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = when (launchpad.status) {
                            "active" -> "Activo"
                            "inactive" -> "Inactivo"
                            "under_construction" -> "En construccion"
                            "lost" -> "Perdido"
                            "retired" -> "Retirado"
                            else -> launchpad.status
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
