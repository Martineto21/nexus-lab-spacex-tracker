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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexuslab.spacextracker.data.model.SpaceXStatistics
import com.nexuslab.spacextracker.data.model.YearlyStats
import com.nexuslab.spacextracker.data.model.RocketStats
import com.nexuslab.spacextracker.presentation.viewmodel.StatisticsViewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
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
        // Header con boton de refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Estadisticas SpaceX",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Datos historicos y metricas",
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
                    contentDescription = "Actualizar estadisticas"
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
        GeneralStatsCard(statistics)
        Spacer(modifier = Modifier.height(16.dp))
        SuccessFailureChart(statistics)
        Spacer(modifier = Modifier.height(16.dp))
        if (statistics.launchesPerYear.isNotEmpty()) {
            LaunchesPerYearChart(statistics.launchesPerYear)
            Spacer(modifier = Modifier.height(16.dp))
        }
        if (statistics.rocketStats.isNotEmpty()) {
            RocketStatsChart(statistics.rocketStats)
            Spacer(modifier = Modifier.height(16.dp))
        }
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
                text = "Estadisticas Generales",
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
                    icon = "T"
                )
                StatItem(
                    value = statistics.successfulLaunches.toString(),
                    label = "Exitosos",
                    icon = "OK"
                )
                StatItem(
                    value = "${String.format("%.1f", statistics.successRate)}%",
                    label = "Tasa Exito",
                    icon = "%"
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
                text = "Exitos vs Fallos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (statistics.totalLaunches > 0) {
                val chartEntryModelProducer = remember { ChartEntryModelProducer() }

                LaunchedEffect(statistics.successfulLaunches, statistics.failedLaunches) {
                    chartEntryModelProducer.setEntries(
                        listOf(
                            entryOf(0, statistics.successfulLaunches),
                            entryOf(1, statistics.failedLaunches)
                        )
                    )
                }

                Chart(
                    chart = columnChart(),
                    chartModelProducer = chartEntryModelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

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
                text = "Lanzamientos por Ano",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val chartEntryModelProducer = remember { ChartEntryModelProducer() }

            LaunchedEffect(yearlyStats) {
                chartEntryModelProducer.setEntries(
                    yearlyStats.mapIndexed { index, stat ->
                        entryOf(index, stat.launches)
                    }
                )
            }

            Chart(
                chart = lineChart(),
                chartModelProducer = chartEntryModelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

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
                text = "Estadisticas por Cohete",
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
                text = "${rocketStat.launches} lanzamientos - ${rocketStat.successes} exitos",
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
                text = "Estadisticas de Boosters",
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
                    icon = "OK"
                )
                StatItem(
                    value = statistics.boostersLost.toString(),
                    label = "Perdidos",
                    icon = "X"
                )

                val recoveryRate = if ((statistics.boostersRecovered + statistics.boostersLost) > 0) {
                    (statistics.boostersRecovered.toFloat() /
                     (statistics.boostersRecovered + statistics.boostersLost).toFloat()) * 100f
                } else 0f

                StatItem(
                    value = "${String.format("%.1f", recoveryRate)}%",
                    label = "Tasa Recuperacion",
                    icon = "R"
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
