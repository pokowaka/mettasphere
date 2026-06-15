package com.pokowaka.mettasphere.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokowaka.mettasphere.data.Preset
import com.pokowaka.mettasphere.data.DataRepository
import com.pokowaka.mettasphere.data.Reflection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date
import com.pokowaka.mettasphere.theme.*

private data class Hindrance(val id: String, val name: String, val desc: String)
private data class SelectionItem(val level: Int, val label: String, val desc: String)
private data class RadioOption(val id: String, val label: String, val desc: String)

private val HINDRANCES = listOf(
    Hindrance("desire", "Sensual Desire", "Pulling in, wanting the present moment to last longer"),
    Hindrance("aversion", "Ill-Will / Aversion", "Pushing away or resisting the present moment"),
    Hindrance("sloth", "Sloth & Torpor", "Heaviness, sleepiness, or sluggishness"),
    Hindrance("restless", "Restlessness", "Spinning, worry, or the planning mind"),
    Hindrance("doubt", "Doubt", "\"Is this working? Am I doing it right?\"")
)

@Composable
fun ReflectScreen(
    repository: DataRepository,
    presetId: Int?,
    onSave: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var step by remember { mutableStateOf(0) }
    var preset by remember { mutableStateOf<Preset?>(null) }
    var showDetailed by remember { mutableStateOf(true) }

    LaunchedEffect(presetId) {
        if (presetId != null) {
            val presets = repository.presets.first()
            val found = presets.find { it.id == presetId }
            preset = found
            if (found != null) {
                showDetailed = found.showDetailedReflection
            }
        }
    }
    val totalSteps = if (showDetailed) 6 else 3

    // Questionnaire States
    var radiance by remember { mutableStateOf(1) }
    var selectedHindrances by remember { mutableStateOf(setOf<String>()) }
    var recognitionLevel by remember { mutableStateOf(1) }
    var releaseType by remember { mutableStateOf("6r") }
    var relaxationLevel by remember { mutableStateOf(1) }
    var smileQuality by remember { mutableStateOf(1) }

    val scrollState = rememberScrollState()

    LaunchedEffect(step) {
        scrollState.scrollTo(0)
    }

    fun handleSave() {
        coroutineScope.launch {
            repository.addReflection(
                Reflection(
                    timestamp = System.currentTimeMillis(),
                    mettaRadiance = radiance,
                    hindrances = selectedHindrances.toList(),
                    recognitionLevel = recognitionLevel,
                    releaseType = releaseType,
                    relaxationLevel = relaxationLevel,
                    smileQuality = smileQuality,
                    smileDuration = "vanished",
                    physicalSensations = emptyList(),
                    mentalStates = emptyList()
                )
            )
            onSave()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SandstoneBg)
            .padding(24.dp)
    ) {
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(TrackGrey)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = (step + 1).toFloat() / totalSteps.toFloat())
                    .background(TerracottaPrimary)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Multi-step Content with animated transition
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "reflectStep"
            ) { targetStep ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    when (targetStep) {
                        0 -> RadianceStep(radiance, onRadianceChange = { radiance = it })
                        1 -> HindrancesStep(selectedHindrances, onToggle = { id ->
                            selectedHindrances = if (selectedHindrances.contains(id)) {
                                selectedHindrances - id
                            } else {
                                selectedHindrances + id
                            }
                        })
                        2 -> RecognitionStep(recognitionLevel, onSelect = { recognitionLevel = it })
                        3 -> ReleaseStep(releaseType, onSelect = { releaseType = it })
                        4 -> RelaxationStep(relaxationLevel, onSelect = { relaxationLevel = it })
                        5 -> SmileStep(
                            quality = smileQuality,
                            onQualitySelect = { smileQuality = it }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Actions
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (step > 0) {
                    Button(
                        onClick = { step -= 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9E8E1), contentColor = Color(0xFF31332E)),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) {
                        Text("Previous", fontWeight = FontWeight.Bold)
                    }
                }

                val isLast = step == totalSteps - 1
                Button(
                    onClick = { if (isLast) handleSave() else step += 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.weight(if (step > 0) 2f else 1f).height(56.dp)
                ) {
                    Text(
                        text = if (isLast) "Save Reflection" else "Next",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (step == 0) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Skip for now", color = MutedClayText, fontSize = 13.sp)
                }
            }
        }
    }
}

// Steps Sub-Composables
private data class RadianceLevel(val level: Int, val title: String, val quote: String, val companion: String)

private val RADIANCE_LEVELS = listOf(
    RadianceLevel(1, "Subtle/None", "I don't feel it right now.", "A quiet space. Even without physical warmth, the simple intention of goodwill is planting seeds of peace in your mind. It is enough just to sit."),
    RadianceLevel(2, "Heart Glow", "I felt a soft glow in my heart.", "A gentle spark. A warm center of friendliness is settling in the heart space. Rest your awareness here."),
    RadianceLevel(3, "Body Ease", "I felt it flow through my whole body.", "Filling the vessel. The warm sensation of comfort spreads outward, soaking the limbs and torso in calm ease. It might feel like the body and limbs are starting to disappear."),
    RadianceLevel(4, "Head Center", "I felt balanced in my head, body gone.", "A quiet stillness. The warmth settles in the head space, and it feels like the physical body is not really there anymore."),
    RadianceLevel(5, "Boundless", "I felt boundless, radiating in all directions.", "Boundless presence. Goodwill radiates outward infinitely, dissolving boundaries in unlimited friendliness for all beings.")
)

@Composable
private fun RadianceStep(value: Int, onRadianceChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text("Initial Feeling", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TerracottaPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "How would you rate the warm 'glow' or feeling of Metta in the center of your chest?",
            fontSize = 14.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(36.dp))

        // 5 circular selectors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                val isSelected = value == i
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) TerracottaPrimary else WarmSand)
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else TrackGrey,
                            CircleShape
                        )
                        .clickable { onRadianceChange(i) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = i.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MutedClayText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Soft description card
        val current = remember(value) {
            RADIANCE_LEVELS.find { it.level == value } ?: RADIANCE_LEVELS[0]
        }

        Crossfade(targetState = current, label = "radianceQuote") { level ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ClaySoft)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"${level.quote}\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoalText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = level.companion,
                    fontSize = 13.sp,
                    color = TerracottaPrimary,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HindrancesStep(selected: Set<String>, onToggle: (String) -> Unit) {
    Column {
        Text(
            "Hindrances",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select any states observed. They often combine (e.g., aversion + restlessness, or desire + planning).",
            fontSize = 13.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        HINDRANCES.forEach { h ->
            val isChecked = selected.contains(h.id)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isChecked) ClaySoft else WarmSand)
                    .border(
                        1.dp,
                        if (isChecked) TerracottaPrimary else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onToggle(h.id) }
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onToggle(h.id) },
                    colors = CheckboxDefaults.colors(checkedColor = TerracottaPrimary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(h.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoalText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(h.desc, fontSize = 12.sp, color = MutedClayText)
                }
            }
        }
    }
}

@Composable
private fun RecognitionStep(selectedLevel: Int, onSelect: (Int) -> Unit) {
    val options = listOf(
        SelectionItem(1, "Post-event", "I was lost for a while before I noticed"),
        SelectionItem(2, "During the event", "I caught the thought while it was active"),
        SelectionItem(3, "At the start", "I caught the thought as soon as it began"),
        SelectionItem(4, "Pre-thought", "I felt the \"tightness\" before the thought formed"),
        SelectionItem(5, "The Flicker", "I saw the intention to move before any tension arose")
    )
    Column {
        Text(
            "Recognition",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "At what stage did you catch the distraction before returning to Metta?",
            fontSize = 14.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { opt ->
            val isSelected = selectedLevel == opt.level
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) ClaySoft else WarmSand)
                    .border(
                        1.dp,
                        if (isSelected) TerracottaPrimary else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(opt.level) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "LEVEL ${opt.level}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TerracottaPrimary else MutedClayText
                    )
                    Text(
                        opt.label,
                        fontSize = 11.sp,
                        color = MutedClayText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(opt.desc, fontSize = 14.sp, color = DarkCharcoalText, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun ReleaseStep(selectedValue: String, onSelect: (String) -> Unit) {
    val options = listOf(
        RadioOption("suppression", "Suppression (The Push)", "I tried to \"kick\" the thought out because it was \"bad\"."),
        RadioOption("analytical", "Analytical (The Story)", "I started thinking about why the distraction happened."),
        RadioOption("passive", "Passive (The Drift)", "I let the thought stay and just sat with it."),
        RadioOption("6r", "The 6R Release (The Let-Go)", "I allowed the thought to be there, but stopped \"giving it fuel\".")
    )
    Column {
        Text(
            "The Release",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "How did you handle the distraction once you recognized it?",
            fontSize = 14.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { opt ->
            val isSelected = selectedValue == opt.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) ClaySoft else WarmSand)
                    .border(
                        1.dp,
                        if (isSelected) TerracottaPrimary else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(opt.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onSelect(opt.id) },
                    colors = RadioButtonDefaults.colors(selectedColor = TerracottaPrimary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(opt.label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkCharcoalText)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(opt.desc, fontSize = 12.sp, color = MutedClayText)
                }
            }
        }
    }
}

@Composable
private fun RelaxationStep(selectedValue: Int, onSelect: (Int) -> Unit) {
    val options = listOf(
        SelectionItem(1, "No Relaxation", "I forgot to relax"),
        SelectionItem(2, "Surface", "I relaxed my shoulders, jaw, or brow"),
        SelectionItem(3, "Deep", "I felt a release of 'pressure' inside my head"),
        SelectionItem(4, "Total", "The whole body felt dropped into profound ease")
    )
    Column {
        Text(
            "Relaxation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "When you recognized a distraction, how did you \"Relax\"?",
            fontSize = 14.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        options.forEach { opt ->
            val isSelected = selectedValue == opt.level
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) ClaySoft else WarmSand)
                    .border(
                        1.dp,
                        if (isSelected) TerracottaPrimary else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(opt.level) }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "LEVEL ${opt.level}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) TerracottaPrimary else MutedClayText
                    )
                    Text(opt.label, fontSize = 11.sp, color = MutedClayText)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(opt.desc, fontSize = 14.sp, color = DarkCharcoalText, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

@Composable
private fun SmileStep(
    quality: Int,
    onQualitySelect: (Int) -> Unit
) {
    val qualities = listOf(
        SelectionItem(1, "Mechanical", "Lips moved, but felt 'fake'; mood unchanged."),
        SelectionItem(2, "Friendly", "Felt like seeing a friend; meditation felt lighter."),
        SelectionItem(3, "Radiant", "Eyes felt like they were smiling; mind became sunny."),
        SelectionItem(4, "Vibrant", "Triggered bubbling happiness, lightness, or warm energy.")
    )
    Column {
        Text(
            "The Smile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "What was the quality of your \"Re-smile\"?",
            fontSize = 14.sp,
            color = MutedClayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        qualities.forEach { opt ->
            val isSelected = quality == opt.level
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) ClaySoft else WarmSand)
                    .border(
                        1.dp,
                        if (isSelected) TerracottaPrimary else Color.Transparent,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onQualitySelect(opt.level) }
                    .padding(16.dp)
            ) {
                Text(
                    "LEVEL ${opt.level}: ${opt.label}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TerracottaPrimary else MutedClayText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(opt.desc, fontSize = 13.sp, color = DarkCharcoalText)
            }
        }


    }
}



