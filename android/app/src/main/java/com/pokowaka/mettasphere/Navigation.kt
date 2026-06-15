package com.pokowaka.mettasphere

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.pokowaka.mettasphere.data.DefaultDataRepository
import com.pokowaka.mettasphere.data.Preset
import com.pokowaka.mettasphere.ui.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.pokowaka.mettasphere.theme.*

@Composable
fun MainNavigation() {
    val context = LocalContext.current
    val repository = remember { DefaultDataRepository(context) }
    val backStack = rememberNavBackStack(Start)
    
    val currentKey = backStack.lastOrNull()
    val isTimerActive = currentKey is Timer

    Scaffold(
        bottomBar = {
            if (!isTimerActive && currentKey != null) {
                NavigationBar(
                    containerColor = SandstoneBg,
                    tonalElevation = 8.dp,
                    modifier = Modifier.height(80.dp)
                ) {
                    NavigationBarItem(
                        selected = currentKey == Start,
                        onClick = {
                            if (currentKey != Start) {
                                backStack.removeLastOrNull()
                                backStack.add(Start)
                            }
                        },
                        icon = { Icon(Icons.Default.SelfImprovement, contentDescription = "Practice") },
                        label = { Text("Practice", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TerracottaPrimary,
                            selectedTextColor = TerracottaPrimary,
                            indicatorColor = ClaySoft
                        )
                    )

                    NavigationBarItem(
                        selected = currentKey == Progress,
                        onClick = {
                            if (currentKey != Progress) {
                                backStack.removeLastOrNull()
                                backStack.add(Progress)
                            }
                        },
                        icon = { Icon(Icons.Default.AutoGraph, contentDescription = "Insights") },
                        label = { Text("Insights", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TerracottaPrimary,
                            selectedTextColor = TerracottaPrimary,
                            indicatorColor = ClaySoft
                        )
                    )

                    NavigationBarItem(
                        selected = currentKey == History,
                        onClick = {
                            if (currentKey != History) {
                                backStack.removeLastOrNull()
                                backStack.add(History)
                            }
                        },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Journal") },
                        label = { Text("Journal", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TerracottaPrimary,
                            selectedTextColor = TerracottaPrimary,
                            indicatorColor = ClaySoft
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Start> {
                    StartScreen(
                        repository = repository,
                        onStartTimer = { preset -> backStack.add(Timer(preset.id ?: 0)) },
                        onEditPreset = { preset -> backStack.add(PresetForm(preset.id)) },
                        onCreatePreset = { backStack.add(PresetForm(null)) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                entry<Timer> { key ->
                    // Find preset synchronously or asynchronously
                    var preset by remember { mutableStateOf<Preset?>(null) }
                    LaunchedEffect(key.presetId) {
                        val presets = repository.presets.first()
                        preset = presets.find { it.id == key.presetId }
                    }

                    preset?.let { p ->
                        TimerScreen(
                            preset = p,
                            onEnd = {
                                // Transition to reflection screen
                                backStack.removeLastOrNull()
                                backStack.add(Reflect(presetId = p.id))
                            }
                        )
                    }
                }

                entry<Reflect> { key ->
                    ReflectScreen(
                        repository = repository,
                        presetId = key.presetId,
                        onSave = {
                            // Go to Insights
                            backStack.removeLastOrNull()
                            backStack.add(Progress)
                        },
                        onSkip = {
                            // Go back to Start
                            backStack.removeLastOrNull()
                            backStack.add(Start)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                entry<PresetForm> { key ->
                    PresetFormScreen(
                        repository = repository,
                        presetId = key.presetId,
                        onSave = {
                            // Save and return to Start
                            backStack.removeLastOrNull()
                        },
                        onCancel = {
                            // Cancel and return to Start
                            backStack.removeLastOrNull()
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                entry<Progress> {
                    ProgressScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                entry<History> {
                    HistoryScreen(
                        repository = repository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        )
    }
}
