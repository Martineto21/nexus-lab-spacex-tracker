package com.nexuslab.spacextracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexuslab.spacextracker.data.model.SpaceXStatistics
import com.nexuslab.spacextracker.data.model.YearlyStats
import com.nexuslab.spacextracker.data.model.RocketStats
import com.nexuslab.spacextracker.presentation.viewmodel.StatisticsViewModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Manejar errores con Snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header con botón de refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📊 Estadísticas SpaceX",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Datos históricos y métricas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = { viewModel.refreshStatistics() },
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualizar estadísticas"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StatisticsContent(
                statistics = uiState.statistics,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Mostrar error si existe
        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Cerrar")
                    }
                }
            ) {
                Text("Error: $error")
            }
        }
    }
}

@Composable
private fun StatisticsContent(
    statistics: SpaceXStatistics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Estadísticas generales
        GeneralStatsCard(statistics)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gráfico de éxitos vs fallos
        SuccessFailureChart(statistics)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gráfico de lanzamientos por año
        if (statistics.launchesPerYear.isNotEmpty()) {
            LaunchesPerYearChart(statistics.launchesPerYear)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Estadísticas por cohete
        if (statistics.rocketStats.isNotEmpty()) {
            RocketStatsChart(statistics.rocketStats)
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Estadísticas de boosters
        BoosterStatsCard(statistics)
    }
}

@Composable
private fun GeneralStatsCard(statistics: SpaceXStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🚀 Estadísticas Generales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = statistics.totalLaunches.toString(),
                    label = "Total Lanzamientos",
                    icon = "🚀"
                )
                StatItem(
                    value = statistics.successfulLaunches.toString(),
                    label = "Exitosos",
                    icon = "✅"
                )
                StatItem(
                    value = "${String.format("%.1f", statistics.successRate)}%",
                    label = "Tasa Éxito",
                    icon = "📈"
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SuccessFailureChart(statistics: SpaceXStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Éxitos vs Fallos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (statistics.totalLaunches > 0) {
                // Gráfico simple usando datos
                val chartModel = CartesianChartModel(
                    ColumnCartesianLayerModel.build {
                        series(
                            statistics.successfulLaunches,
                            statistics.failedLaunches
                        )
                    }
                )
                
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberColumnCartesianLayer()
                    ),
                    model = chartModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                
                // Leyenda manual
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(
                        color = MaterialTheme.colorScheme.primary,
                        label = "Exitosos (${statistics.successfulLaunches})"
                    )
                    LegendItem(
                        color = MaterialTheme.colorScheme.error,
                        label = "Fallidos (${statistics.failedLaunches})"
                    )
                }
            } else {
                Text(
                    text = "Sin datos disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LaunchesPerYearChart(yearlyStats: List<YearlyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📅 Lanzamientos por Año",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val chartModel = CartesianChartModel(
                LineCartesianLayerModel.build {
                    series(yearlyStats.map { it.launches.toFloat() })
                }
            )
            
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer()
                ),
                model = chartModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            // Mostrar años en una fila
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                yearlyStats.take(5).forEach { yearStat ->
                    Text(
                        text = "${yearStat.year}\n${yearStat.launches}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun RocketStatsChart(rocketStats: List<RocketStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🚀 Estadísticas por Cohete",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            rocketStats.take(4).forEach { rocketStat ->
                RocketStatItem(rocketStat)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun RocketStatItem(rocketStat: RocketStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(android.graphics.Color.parseColor(rocketStat.color)))
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rocketStat.rocketName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${rocketStat.launches} lanzamientos • ${rocketStat.successes} éxitos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        val successRate = if (rocketStat.launches > 0) {
            (rocketStat.successes.toFloat() / rocketStat.launches.toFloat()) * 100f
        } else 0f
        
        Text(
            text = "${String.format("%.0f", successRate)}%",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BoosterStatsCard(statistics: SpaceXStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔄 Estadísticas de Boosters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = statistics.boostersRecovered.toString(),
                    label = "Recuperados",
                    icon = "✅"
                )
                StatItem(
                    value = statistics.boostersLost.toString(),
                    label = "Perdidos",
                    icon = "❌"
                )
                
                val recoveryRate = if ((statistics.boostersRecovered + statistics.boostersLost) > 0) {
                    (statistics.boostersRecovered.toFloat() / 
                     (statistics.boostersRecovered + statistics.boostersLost).toFloat()) * 100f
                } else 0f
                
                StatItem(
                    value = "${String.format("%.1f", recoveryRate)}%",
                    label = "Tasa Recuperación",
                    icon = "🔄"
                )
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}