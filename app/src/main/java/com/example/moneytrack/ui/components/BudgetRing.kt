package com.example.moneytrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.White
import kotlin.math.roundToInt

private val Track = Color(0xFF2C2C33)
private val Amber = Color(0xFFFFB23F)
private val SoftGray = Color(0xFFA9A7A1)

/**
 * КАСТОМНЫЙ КОМПОНЕНТ №2 — кольцо лимита.
 * При появлении дуга плавно дорастает, процент «набегает»,
 * цвет меняется: < 70% салатовый, 70–99% янтарный, ≥ 100% красный.
 */
@Composable
fun BudgetRing(
    spent: Long,
    limit: Long,
    modifier: Modifier = Modifier,
    ringSize: Dp = 116.dp,
    strokeWidth: Dp = 12.dp,
) {
    val target = if (limit > 0) spent.toFloat() / limit else 0f
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val progress by animateFloatAsState(
        targetValue = if (started) target else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "ringProgress",
    )
    val arcColor by animateColorAsState(
        targetValue = when {
            target >= 1f -> Danger
            target >= 0.7f -> Amber
            else -> Lime
        },
        label = "ringColor",
    )

    Box(modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val s = strokeWidth.toPx()
            val topLeft = Offset(s / 2, s / 2)
            val arcSize = Size(size.width - s, size.height - s)
            drawArc(Track, 0f, 360f, false, topLeft = topLeft, size = arcSize, style = Stroke(s))
            drawArc(
                arcColor, -90f, 360f * progress.coerceIn(0f, 1f), false,
                topLeft = topLeft, size = arcSize, style = Stroke(s, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.titleLarge, color = White)
            Text("ЛИМИТА", style = MaterialTheme.typography.labelSmall, color = SoftGray)
        }
    }
}

@Preview
@Composable
private fun BudgetRingPreview() {
    MoneyTrackTheme {
        Box(Modifier.background(Ink).padding(16.dp)) {
            BudgetRing(spent = 17_600, limit = 30_000)
        }
    }
}
