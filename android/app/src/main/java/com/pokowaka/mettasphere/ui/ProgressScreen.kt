package com.pokowaka.mettasphere.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokowaka.mettasphere.data.DataRepository
import com.pokowaka.mettasphere.data.Reflection
import com.pokowaka.mettasphere.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier
) {
    val realReflections by repository.reflections.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    val displayReflections = remember(realReflections) {
        realReflections.takeLast(10)
    }

    // 14-day consistency calculation
    val streakDays = remember(realReflections) {
        val days = BooleanArray(14)
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        for (i in 0 until 14) {
            val targetDayStart = startOfToday - (13 - i) * dayMillis
            val targetDayEnd = targetDayStart + dayMillis
            days[i] = realReflections.any { it.timestamp in targetDayStart until targetDayEnd }
        }
        days
    }

    val streakCount = remember(streakDays) {
        var current = 0
        for (i in 13 downTo 0) {
            if (streakDays[i]) current++ else break
        }
        current
    }

    // Hindrance frequencies
    val hindranceFrequencies = remember(displayReflections) {
        val map = mutableMapOf("desire" to 0, "aversion" to 0, "sloth" to 0, "restless" to 0, "doubt" to 0)
        displayReflections.forEach { ref ->
            ref.hindrances.forEach { h ->
                if (map.containsKey(h)) {
                    map[h] = map[h]!! + 1
                }
            }
        }
        map
    }

    val dateFormat = remember { SimpleDateFormat("MM/dd", Locale.US) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SandstoneBg)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Insights",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "The habit is deepening.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = DarkCharcoalText,
            lineHeight = 40.sp
        )
        Text(
            text = "Awareness finds its own space.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            fontStyle = FontStyle.Italic,
            color = TerracottaPrimary,
            lineHeight = 40.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Consistency Streak Row
        Text("PRACTICE CONSISTENCY (14 DAYS)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(WarmSand)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (streakCount > 0) "$streakCount-Day Streak" else "No active streak",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoalText
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // The 14 circular indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    streakDays.forEach { isSat ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isSat) TerracottaPrimary else TrackGrey)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("14d ago", fontSize = 10.sp, color = MutedClayText)
                    Text("Today", fontSize = 10.sp, color = MutedClayText, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bento Sits Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(TerracottaPrimary, SoftRose)))
                    .padding(20.dp)
                    .height(100.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL SITTINGS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                    Column {
                        Text(realReflections.size.toString(), fontSize = 32.sp, fontWeight = FontWeight.Light, color = Color.White)
                        Text("Completed sessions", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Graph 1: Metta Radiance
        Text("METTA RADIANCE TREND", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(WarmSand)
                .padding(20.dp)
        ) {
            Column {
                Text("Initial Feeling", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoalText)
                Text("Chest glow rating on scale of 1 to 5.", fontSize = 12.sp, color = MutedClayText)
                Spacer(modifier = Modifier.height(24.dp))

                if (displayReflections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log a session to view your trend.",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = MutedClayText
                        )
                    }
                } else {
                    val radiancePoints = displayReflections.map { it.mettaRadiance.toFloat() }
                    val dateLabels = displayReflections.map { dateFormat.format(Date(it.timestamp)) }

                    BezierTrendGraph(
                        points = radiancePoints,
                        minY = 1f,
                        maxY = 5f,
                        lineColor = TerracottaPrimary,
                        fillColor = TerracottaPrimary.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dateLabels.forEach { label ->
                            Text(label, fontSize = 10.sp, color = MutedClayText)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Graph 2: Recognition Speed
        Text("RECOGNITION SPEED TREND", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(WarmSand)
                .padding(20.dp)
        ) {
            Column {
                Text("Recognition Speed", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkCharcoalText)
                Text("Proactive detection of distractions on a scale of 1 to 5.", fontSize = 12.sp, color = MutedClayText)
                Spacer(modifier = Modifier.height(24.dp))

                if (displayReflections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Log a session to view your trend.",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = MutedClayText
                        )
                    }
                } else {
                    val recognitionPoints = displayReflections.map { it.recognitionLevel.toFloat() }
                    val dateLabels = displayReflections.map { dateFormat.format(Date(it.timestamp)) }

                    BezierTrendGraph(
                        points = recognitionPoints,
                        minY = 1f,
                        maxY = 5f,
                        lineColor = MutedClayText,
                        fillColor = MutedClayText.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dateLabels.forEach { label ->
                            Text(label, fontSize = 10.sp, color = MutedClayText)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Graph 3: Sorted Hindrances List
        Text("HINDRANCES FREQUENCY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedClayText)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(WarmSand)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Distraction Patterns",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoalText
                )
                Text("Relative frequency of distractions, sorted from most common.", fontSize = 12.sp, color = MutedClayText)
                Spacer(modifier = Modifier.height(24.dp))

                if (displayReflections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No distractions logged yet.",
                            fontSize = 13.sp,
                            fontStyle = FontStyle.Italic,
                            color = MutedClayText
                        )
                    }
                } else {
                    val maxCount = hindranceFrequencies.values.maxOrNull()?.coerceAtLeast(1) ?: 1
                    val sortedFrequencies = hindranceFrequencies.toList().sortedByDescending { it.second }

                    sortedFrequencies.forEach { (hId, count) ->
                        val label = when (hId) {
                            "desire" -> "Sensual Desire"
                            "aversion" -> "Ill-Will / Aversion"
                            "sloth" -> "Sloth & Torpor"
                            "restless" -> "Restlessness"
                            "doubt" -> "Doubt"
                            else -> hId
                        }
                        val percentage = if (displayReflections.isNotEmpty()) {
                            (count.toFloat() / displayReflections.size * 100).toInt()
                        } else 0

                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, fontSize = 14.sp, color = DarkCharcoalText, fontWeight = FontWeight.Bold)
                                Text("$percentage% of sittings", fontSize = 12.sp, color = MutedClayText)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(TrackGrey)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(fraction = count.toFloat() / maxCount.toFloat())
                                        .background(TerracottaPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun BezierTrendGraph(
    points: List<Float>,
    minY: Float,
    maxY: Float,
    lineColor: Color,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        if (points.isEmpty()) return@Canvas

        val pointSpacing = if (points.size > 1) width / (points.size - 1) else width
        val valueRange = if (maxY > minY) maxY - minY else 1f

        val coordinates = points.mapIndexed { index, value ->
            val x = index * pointSpacing
            val y = height - ((value - minY) / valueRange) * height
            Offset(x, y)
        }

        // Draw horizontal grid lines
        val gridLineCount = 5
        for (i in 0 until gridLineCount) {
            val y = height - (i.toFloat() / (gridLineCount - 1)) * height
            drawLine(
                color = TrackGrey,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (points.size < 2) {
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = Offset(width / 2f, coordinates[0].y)
            )
            return@Canvas
        }

        // Create Bezier path
        val strokePath = Path().apply {
            moveTo(coordinates[0].x, coordinates[0].y)
            for (i in 0 until coordinates.size - 1) {
                val p1 = coordinates[i]
                val p2 = coordinates[i + 1]

                val cp1X = p1.x + (p2.x - p1.x) / 2f
                val cp1Y = p1.y
                val cp2X = p1.x + (p2.x - p1.x) / 2f
                val cp2Y = p2.y

                cubicTo(cp1X, cp1Y, cp2X, cp2Y, p2.x, p2.y)
            }
        }

        // Fill Path
        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(coordinates.last().x, height)
            lineTo(coordinates.first().x, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(fillColor, Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points
        coordinates.forEach { point ->
            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = point
            )
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
}
