package com.pokowaka.mettasphere.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokowaka.mettasphere.data.DataRepository
import com.pokowaka.mettasphere.data.Preset
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.pokowaka.mettasphere.theme.*

private val SOUNDS = listOf("No sound", "Singing Bowl", "Burmese Bell", "Double Chime", "Woodblock", "Gong")
private val VISUALS = listOf(
    "Morning Metta" to listOf(Color(0xFFCBE9DB), Color(0xFFFDE7D3)),
    "Quick Rest" to listOf(Color(0xFFCEE6F2), Color(0xFFCEE6F2).copy(alpha = 0.5f)),
    "Deep Stillness" to listOf(Color(0xFFEFEEE7), Color(0xFFE3E3DB))
)

private var previewPlayer: MediaPlayer? = null


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetFormScreen(
    repository: DataRepository,
    presetId: Int?,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            try {
                previewPlayer?.stop()
                previewPlayer?.release()
            } catch (e: Exception) {
                // Ignore
            }
            previewPlayer = null
        }
    }

    var name by remember { mutableStateOf("New Stillness") }
    var visual by remember { mutableStateOf("Morning Metta") }
    var delay by remember { mutableStateOf(0f) }
    var interval by remember { mutableStateOf(5f) }
    var duration by remember { mutableStateOf(20f) }

    var startSound by remember { mutableStateOf("Singing Bowl") }
    var intervalSound by remember { mutableStateOf("Double Chime") }
    var endSound by remember { mutableStateOf("Burmese Bell") }
    var showDetailedReflection by remember { mutableStateOf(true) }

    // Fetch existing preset details if editing
    LaunchedEffect(presetId) {
        if (presetId != null) {
            val presetList = repository.presets.first()
            val preset = presetList.find { it.id == presetId }
            if (preset != null) {
                name = preset.name
                visual = preset.visual
                delay = preset.delaySeconds.toFloat()
                interval = preset.intervalMinutes.toFloat()
                duration = preset.totalMinutes.toFloat()
                startSound = preset.startSound
                intervalSound = preset.intervalSound
                endSound = preset.endSound
                showDetailedReflection = preset.showDetailedReflection
            }
        }
    }

    fun handleSave() {
        coroutineScope.launch {
            val newPreset = Preset(
                id = presetId,
                name = name,
                visual = visual,
                delaySeconds = delay.toInt(),
                intervalMinutes = interval.toInt(),
                totalMinutes = duration.toDouble(),
                startSound = startSound,
                intervalSound = intervalSound,
                endSound = endSound,
                showDetailedReflection = showDetailedReflection
            )
            if (presetId != null) {
                repository.updatePreset(newPreset)
            } else {
                repository.addPreset(newPreset)
            }
            onSave()
        }
    }

    fun handleDelete() {
        if (presetId != null) {
            coroutineScope.launch {
                repository.deletePreset(presetId)
                onSave()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SandstoneBg)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkCharcoalText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (presetId != null) "Edit Preset" else "Create Preset",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoalText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Name
        Text("PRESET NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = name,
            onValueChange = { name = it },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = WarmSand,
                unfocusedContainerColor = WarmSand,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = DarkCharcoalText
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Sliders & Dropdowns
        
        // 1. Delay before start
        SoundSelectorRow(
            icon = Icons.Default.HourglassTop,
            label = "Delay Before Start",
            valueText = "${delay.toInt()} sec",
            sliderValue = delay,
            onSliderChange = { delay = it },
            valueRange = 0f..60f,
            selectedSound = startSound,
            onSoundChange = { startSound = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Interval Chimes
        SoundSelectorRow(
            icon = Icons.Default.Timer,
            label = "Interval Chimes",
            valueText = "${interval.toInt()} min",
            sliderValue = interval,
            onSliderChange = { interval = it },
            valueRange = 0f..30f,
            selectedSound = intervalSound,
            onSoundChange = { intervalSound = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Total Duration
        SoundSelectorRow(
            icon = Icons.Default.Schedule,
            label = "Total Duration",
            valueText = "${duration.toInt()} min",
            sliderValue = duration,
            onSliderChange = { duration = it },
            valueRange = 5f..120f,
            selectedSound = endSound,
            onSoundChange = { endSound = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Reflection Settings
        Text("REFLECTION SETTINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(WarmSand)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                 Text(
                    text = "Detailed 6R Reflection",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoalText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reflect on Release, Relax, and Re-smile after sittings.",
                    fontSize = 11.sp,
                    color = MutedClayText,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = showDetailedReflection,
                onCheckedChange = { showDetailedReflection = it },
                 colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = TerracottaPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = TrackGrey,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Footer Actions
         Button(
            onClick = ::handleSave,
            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(if (presetId != null) "Update Preset" else "Save Preset", fontWeight = FontWeight.Bold)
        }

        if (presetId != null) {
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = ::handleDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFA746F))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete Preset", color = Color(0xFFFA746F), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun playPreviewSound(context: Context, soundName: String) {
    try {
        previewPlayer?.stop()
        previewPlayer?.release()
    } catch (e: Exception) {
        // Ignored
    }
    previewPlayer = null

    if (soundName == "No sound") return

    val fileName = when (soundName) {
        "Singing Bowl" -> "bowl.wav"
        "Burmese Bell" -> "burmese_bell.wav"
        "Double Chime" -> "bell.wav"
        "Woodblock" -> "woodblock.wav"
        "Gong" -> "gong.wav"
        else -> return
    }
    try {
        val afd = context.assets.openFd("sounds/$fileName")
        val mp = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
        }
        previewPlayer = mp
        mp.setOnCompletionListener {
            it.release()
            if (previewPlayer == mp) {
                previewPlayer = null
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun SoundSelectorRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    valueText: String,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    selectedSound: String,
    onSoundChange: (String) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
             Column {
                Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = label, tint = TerracottaPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(valueText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TerracottaPrimary)
                }
            }

            // Simple Dropdown
            Box {
                 Button(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.buttonColors(containerColor = WarmSand, contentColor = MutedClayText),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(selectedSound, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    SOUNDS.forEach { sound ->
                        DropdownMenuItem(
                            text = { Text(sound) },
                            onClick = {
                                onSoundChange(sound)
                                playPreviewSound(context, sound)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
         Slider(
            value = sliderValue,
            onValueChange = onSliderChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                activeTrackColor = TerracottaPrimary,
                inactiveTrackColor = TrackGrey,
                thumbColor = TerracottaPrimary
            )
        )
    }
}
