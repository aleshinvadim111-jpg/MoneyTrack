package com.example.moneytrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme

/** Вкладки нижней навигации */
enum class MainTab(val route: String, val label: String, val icon: List<String>) {
    Home("home", "Главная", LineIcons.Home),
    Stats("stats", "Статистика", LineIcons.Stats),
    Subscriptions("subscriptions", "Подписки", LineIcons.Calendar),
    Profile("profile", "Профиль", LineIcons.User),
}

private val InactiveIcon = Color(0xFFC9C7C1)

/**
 * КАСТОМНЫЙ КОМПОНЕНТ №3 — плавающая тёмная панель навигации
 * и отдельная кнопка «+» с анимацией нажатия.
 * Кнопка контекстная: на вкладке «Подписки» добавляет подписку, на остальных — расход.
 */
@Composable
fun FloatingNavBar(
    current: MainTab,
    onSelect: (MainTab) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier
                .weight(1f)
                .height(64.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
                .background(Ink)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MainTab.entries.forEach { tab ->
                val selected = tab == current
                val bg by animateColorAsState(if (selected) Lime else Color.Transparent, label = "tabBg")
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(bg)
                        .clickable { onSelect(tab) }
                        .semantics { contentDescription = tab.label },
                    contentAlignment = Alignment.Center,
                ) {
                    LineIcon(tab.icon, if (selected) Ink else InactiveIcon)
                }
            }
        }
        AnimatedAddButton(
            onClick = onAdd,
            description = if (current == MainTab.Subscriptions) "Добавить подписку" else "Добавить расход",
        )
    }
}

/** Кнопка «+»: при нажатии сжимается и поворачивает крестик на 45° */
@Composable
fun AnimatedAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String = "Добавить расход",
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.88f else 1f, label = "addScale")
    val rotation by animateFloatAsState(if (pressed) 45f else 0f, label = "addRotation")

    Box(
        modifier
            .size(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(Lime)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        LineIcon(
            LineIcons.Plus, Ink,
            modifier = Modifier.graphicsLayer { rotationZ = rotation },
            iconSize = 28.dp,
            strokeWidth = 2.4f,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2EDE3, widthDp = 360)
@Composable
private fun FloatingNavBarPreview() {
    MoneyTrackTheme {
        FloatingNavBar(current = MainTab.Home, onSelect = {}, onAdd = {})
    }
}
