package com.example.moneytrack.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.moneytrack.model.Expense
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.White
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * КАСТОМНЫЙ КОМПОНЕНТ №4 — карточка расхода со свайпом.
 * Тянешь влево — под ней красный фон, корзина растёт.
 * Дотянул до 40% ширины — карточка улетает и вызывает onDelete,
 * не дотянул — пружиной возвращается на место.
 */
@Composable
fun SwipeExpenseCard(
    expense: Expense,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var width by remember { mutableFloatStateOf(1f) }
    val currentOnDelete by rememberUpdatedState(onDelete)
    val pull = (-offsetX.value / (width * 0.4f)).coerceIn(0f, 1f)
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier
            .fillMaxWidth()
            .onSizeChanged { width = it.width.toFloat() }
            .clip(shape)
            .background(Danger)
    ) {
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 26.dp)
                .graphicsLayer {
                    val s = 0.8f + 0.4f * pull
                    scaleX = s
                    scaleY = s
                }
        ) {
            LineIcon(LineIcons.Trash, White, iconSize = 24.dp)
        }

        Row(
            Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .clip(shape)
                .background(White)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (-offsetX.value > width * 0.4f) {
                                    offsetX.animateTo(-width, tween(180))
                                    currentOnDelete()
                                } else {
                                    offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                }
                            }
                        },
                        onDragCancel = { scope.launch { offsetX.animateTo(0f, spring()) } },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch { offsetX.snapTo((offsetX.value + dragAmount).coerceIn(-width, 0f)) }
                        },
                    )
                }
                .clickable(onClick = onClick)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(expense.category.light),
                contentAlignment = Alignment.Center,
            ) {
                LineIcon(expense.category.icon, Ink, strokeWidth = 1.8f)
            }
            Column(Modifier.weight(1f)) {
                Text(expense.category.title, style = MaterialTheme.typography.titleMedium, color = Ink)
                Text(
                    expense.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("−${rub(expense.amount)}", style = MaterialTheme.typography.labelLarge, color = Ink)
                Text(expense.time, style = MaterialTheme.typography.labelSmall, color = Muted)
            }
        }
    }
}
