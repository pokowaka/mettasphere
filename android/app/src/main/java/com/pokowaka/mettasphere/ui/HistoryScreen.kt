package com.pokowaka.mettasphere.ui

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokowaka.mettasphere.data.DataRepository
import com.pokowaka.mettasphere.data.Reflection
import java.util.Calendar
import com.pokowaka.mettasphere.theme.*

@Composable
fun HistoryScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier
) {
    val reflections by repository.reflections.collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SandstoneBg)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Your Journal",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TerracottaPrimary
        )
        Text(
            text = "A record of your path toward tranquil awareness.",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = MutedClayText
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        if (reflections.isEmpty()) {
            EmptyHistory(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reflections) { ref ->
                    HistoryCard(reflection = ref)
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp)) // padding for bottom bar
                }
            }
        }
    }
}

@Composable
fun HistoryCard(reflection: Reflection) {
    val dateString = remember(reflection.timestamp) {
        val cal = Calendar.getInstance().apply { timeInMillis = reflection.timestamp }
        DateFormat.format("MM/dd/yyyy • hh:mm a", cal).toString()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(WarmSand)
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateString,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedClayText,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "6R Reflection",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkCharcoalText
            )
            
            // Hindrances tags
            if (reflection.hindrances.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    reflection.hindrances.forEach { h ->
                        Box(
                            modifier = Modifier
                                .background(ClaySoft, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = h.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TerracottaPrimary
                            )
                        }
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Radiance",
                    tint = TerracottaPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reflection.mettaRadiance.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TerracottaPrimary
                )
            }
            Text(
                text = "RADIANCE",
                fontSize = 9.sp,
                color = MutedClayText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(WarmSand, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Journal",
                    tint = MutedClayText,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your first journal entry is waiting for you at the end of a sit.",
                fontSize = 14.sp,
                color = MutedClayText,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
        }
    }
}
