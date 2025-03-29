import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.ui.theme.PrimaryColor

@Composable
fun CNCStatusButton(
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Base colors based on status
    val buttonColorStartBase = if (isActive) Color(0xFF66BB6A) else Color(0xFFEF5350)
    val buttonColorEndBase = if (isActive) Color(0xFF43A047) else Color(0xFFE53935)

    // Animate the button colors if active
    val infiniteTransition = rememberInfiniteTransition()
    val animatedAlpha by if (isActive) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200),
                repeatMode = RepeatMode.Reverse
            )
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    val buttonColorStart = buttonColorStartBase.copy(alpha = animatedAlpha)
    val buttonColorEnd = buttonColorEndBase.copy(alpha = animatedAlpha)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation = if (isPressed) 2.dp else 8.dp

    Box(
        modifier = modifier
            .size(196.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = if (isPressed) {
                        listOf(buttonColorEnd, buttonColorStart)
                    } else {
                        listOf(buttonColorStart, buttonColorEnd)
                    },
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                clip = false
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_cnc),
            contentDescription = "CNC Status",
            modifier = Modifier.size(128.dp)
        )
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
            color = PrimaryColor
        )
    }
}
