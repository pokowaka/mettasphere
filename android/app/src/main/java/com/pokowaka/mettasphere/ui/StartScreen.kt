package com.pokowaka.mettasphere.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.pokowaka.mettasphere.data.DataRepository
import com.pokowaka.mettasphere.data.Preset
import kotlinx.coroutines.flow.map
import com.pokowaka.mettasphere.theme.*


@Composable
fun StartScreen(
    repository: DataRepository,
    onStartTimer: (Preset) -> Unit,
    onEditPreset: (Preset) -> Unit,
    onCreatePreset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val presets by repository.presets.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SandstoneBg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Hero Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quick Start",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedClayText,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Find your",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = DarkCharcoalText
                )
                Text(
                    text = "stillness.",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = TerracottaPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select an atmosphere for your practice or design a new space.",
                    fontSize = 14.sp,
                    color = MutedClayText,
                    lineHeight = 20.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Soft glow background matching terracotta theme glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                        .background(TerracottaPrimary.copy(alpha = 0.2f), CircleShape)
                )
                // Real GIMP App Logo
                Image(
                    painter = painterResource(id = com.pokowaka.mettasphere.R.drawable.app_logo),
                    contentDescription = "MettaSphere Logo",
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, TerracottaPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bento Grid of Presets
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(presets) { index, preset ->
                PresetCard(
                    preset = preset,
                    isLarge = false,
                    onPlay = { onStartTimer(preset) },
                    onEdit = { onEditPreset(preset) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Create New Preset Button Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(2.dp, Color(0xFFB2B2AB).copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .clickable { onCreatePreset() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFF5F4ED), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFF4A655A)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Create New",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoalText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Design custom session",
                            fontSize = 11.sp,
                            color = MutedClayText
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: Preset,
    isLarge: Boolean,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = WarmSand

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(cardColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Edit Preset",
                        tint = DarkCharcoalText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${preset.totalMinutes.toInt()} MIN",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoalText
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoalText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${preset.totalMinutes.toInt()}m sit with ${if (preset.intervalMinutes > 0) "${preset.intervalMinutes}m bells" else "no intervals"}",
                        fontSize = 11.sp,
                        color = MutedClayText,
                        lineHeight = 14.sp
                    )
                }

                IconButton(
                    onClick = onPlay,
                    modifier = Modifier
                        .size(40.dp)
                        .background(TerracottaPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
