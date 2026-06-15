package com.pokowaka.mettasphere.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Refresh
import com.pokowaka.mettasphere.data.Preset
import kotlinx.coroutines.delay
import java.util.Locale
import com.pokowaka.mettasphere.theme.*

private data class SixR(val label: String, val desc: String)

private val SIX_RS = listOf(
    SixR("Recognize", "Notice when the mind has wandered."),
    SixR("Release", "Let go of the distraction; let it be."),
    SixR("Relax", "Soften any tightness in the head or body."),
    SixR("Re-smile", "Gently gladden the mind with a smile."),
    SixR("Return", "Bring your attention back to the feeling of metta."),
    SixR("Repeat", "Stay with the feeling of metta until the next movement.")
)

private fun playSoundFromAssets(context: Context, soundName: String) {
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
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
        }
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun TimerScreen(
    preset: Preset,
    onEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Phase: "delay" or "active" or "finished"
    var phase by remember { mutableStateOf(if (preset.delaySeconds > 0) "delay" else "active") }
    var timeLeft by remember { mutableStateOf(if (phase == "delay") preset.delaySeconds else (preset.totalMinutes * 60).toInt()) }
    var isRunning by remember { mutableStateOf(true) }
    var rIndex by remember { mutableStateOf(0) }

    val totalActiveSeconds = (preset.totalMinutes * 60).toInt()
    val intervalSeconds = preset.intervalMinutes * 60

    // Sound triggering flags
    var startSoundPlayed by remember { mutableStateOf(false) }

    // Timer Tick
    LaunchedEffect(isRunning, timeLeft, phase) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        } else if (timeLeft == 0) {
            if (phase == "delay") {
                phase = "active"
                timeLeft = (preset.totalMinutes * 60).toInt()
                playSoundFromAssets(context, preset.startSound)
                startSoundPlayed = true
            } else if (phase == "active") {
                phase = "finished"
                isRunning = false
                playSoundFromAssets(context, preset.endSound)
                delay(2000)
                onEnd()
            }
        }
    }

    // Play start sound immediately if delay is 0
    LaunchedEffect(Unit) {
        if (preset.delaySeconds == 0 && !startSoundPlayed) {
            playSoundFromAssets(context, preset.startSound)
            startSoundPlayed = true
        }
    }

    // Cycle 6Rs every 5 seconds during active phase
    LaunchedEffect(isRunning, phase) {
        if (isRunning && phase == "active") {
            while (true) {
                delay(5000)
                rIndex = (rIndex + 1) % SIX_RS.size
            }
        }
    }

    // Interval Chime Triggering
    if (phase == "active" && intervalSeconds > 0) {
        val elapsed = totalActiveSeconds - timeLeft
        LaunchedEffect(elapsed) {
            if (elapsed > 0 && elapsed % intervalSeconds == 0 && timeLeft > 0) {
                playSoundFromAssets(context, preset.intervalSound)
            }
        }
    }

    // Format time (MM:SS)
    val formattedTime = remember(timeLeft) {
        val mins = timeLeft / 60
        val secs = timeLeft % 60
        String.format(Locale.US, "%d:%02d", mins, secs)
    }

    // Calculate progress
    val progress = remember(timeLeft, phase) {
        if (phase == "active") {
            (totalActiveSeconds - timeLeft).toFloat() / totalActiveSeconds.toFloat()
        } else if (phase == "delay") {
            (preset.delaySeconds - timeLeft).toFloat() / preset.delaySeconds.toFloat()
        } else {
            1f
        }
    }

    // UI Colors
    val primaryColor = if (phase == "delay") MutedClayText else TerracottaPrimary
    val gradientColors = when (preset.name) {
        "Morning Metta" -> listOf(SandstoneBg, WarmSand)
        "Quick Rest" -> listOf(Color(0xFFCEE6F2).copy(alpha = 0.2f), SandstoneBg)
        else -> listOf(SandstoneBg, WarmSand)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Text(
                text = if (phase == "delay") "PREPARE" else "DEEP PRESENCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MutedClayText,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = preset.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoalText
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Progress Ring & Time
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                // Glow behind progress
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(primaryColor.copy(alpha = 0.05f), CircleShape)
                )
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background circle
                    drawCircle(
                        color = TrackGrey,
                        style = Stroke(width = 6.dp.toPx())
                    )
                    // Active progress arc
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formattedTime,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light,
                        color = DarkCharcoalText,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = if (phase == "delay") "starting soon" else "remaining",
                        fontSize = 12.sp,
                        color = MutedClayText,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(36.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause / Play
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier
                            .size(64.dp)
                            .background(primaryColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isRunning) "Pause" else "Resume",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isRunning) "Pause" else "Resume",
                        fontSize = 11.sp,
                        color = MutedClayText
                    )
                }

                // End Session
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = onEnd,
                        modifier = Modifier
                            .size(64.dp)
                            .background(TrackGrey, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh, // acts as a reset or end
                            contentDescription = "End Session",
                            tint = DarkCharcoalText
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "End Session",
                        fontSize = 11.sp,
                        color = MutedClayText
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 6R Guidance Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                     .background(WarmSand.copy(alpha = 0.6f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (phase == "active") {
                    Crossfade(targetState = rIndex, label = "sixRs") { idx ->
                        val sixR = SIX_RS[idx]
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = sixR.label,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = sixR.desc,
                                fontSize = 13.sp,
                                color = MutedClayText,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Find your posture...",
                        fontSize = 13.sp,
                        color = MutedClayText,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}
