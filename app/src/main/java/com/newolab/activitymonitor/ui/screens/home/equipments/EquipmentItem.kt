package com.newolab.activitymonitor.ui.screens.home.equipments

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.model.Equipment
import com.newolab.activitymonitor.ui.theme.PrimaryColor

@Composable
fun EquipmentItem(
    equipment: Equipment,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onItemClick(equipment.id) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            equipment.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            AsyncImage(
                model = R.drawable.icon_cnc,
                contentDescription = "Equipment Image",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentScale = ContentScale.Fit
            )

            EquipmentStatusIndicator(status = equipment.status.toString())
        }
    }
}

@Composable
fun EquipmentStatusIndicator(status: String?) {
    val isActive = status?.toBoolean() ?: false

    val activeLabel = stringResource(R.string.active)
    val passiveLabel = stringResource(R.string.passive)

    val (indicatorColor, statusText) = if (isActive) {
        Pair(Color(0xFF4CAF50), activeLabel) // Green
    } else {
        Pair(Color(0xFFF44336), passiveLabel) // Red
    }

    val infiniteTransition = rememberInfiniteTransition()

    // If active, animate the indicator color between fully opaque green and transparent green.
    // If not active, just remain the static red color.
    val animatedColor by if (isActive) {
        infiniteTransition.animateColor(
            initialValue = indicatorColor.copy(alpha = 1f),
            targetValue = indicatorColor.copy(alpha = 0f),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(indicatorColor) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = animatedColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = statusText,
            fontSize = 17.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
            color = Color.White
        )
    }
}