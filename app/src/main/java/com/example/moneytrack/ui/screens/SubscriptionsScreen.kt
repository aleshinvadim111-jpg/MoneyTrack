package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.model.SampleData
import com.example.moneytrack.model.SubPeriod
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.ui.components.pluralDays
import com.example.moneytrack.ui.components.rub
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Peach
import com.example.moneytrack.ui.theme.White

@Composable
fun SubscriptionsScreen(
    subscriptions: List<Subscription> = SampleData.subscriptions,
    onOpen: (Subscription) -> Unit = {},
) {
    val subs = subscriptions.sortedBy { it.daysUntil }
    val monthly = subs.sumOf { it.monthlyAmount }

    Column(
        Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp)
            .padding(top = 20.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Новая подписка добавляется общей кнопкой «+» внизу экрана
        Text("ПОДПИСКИ", style = MaterialTheme.typography.labelMedium, color = Muted)
        Text("Подписки", style = MaterialTheme.typography.headlineMedium, color = Ink)

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Ink)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("В МЕСЯЦ НА ПОДПИСКИ", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA9A7A1))
            Text(rub(monthly), style = MaterialTheme.typography.displayMedium, color = Lime)
            Text(
                "Активных: ${subs.size} · в год ${rub(monthly * 12)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFC9C7C1),
            )
        }

        Text(
            "БЛИЖАЙШИЕ СПИСАНИЯ",
            style = MaterialTheme.typography.labelMedium,
            color = InkSoft,
            modifier = Modifier.padding(top = 4.dp),
        )
        if (subs.isEmpty()) {
            Text(
                "Подписок пока нет. Нажмите «+» внизу экрана, чтобы не забывать о списаниях.",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )
        }
        subs.forEach { sub -> SubscriptionRow(sub, onClick = { onOpen(sub) }) }
    }
}

/** Строка подписки — используется и в списке, и как живой предпросмотр в форме */
@Composable
internal fun SubscriptionRow(sub: Subscription, onClick: (() -> Unit)?) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(sub.color),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                sub.title.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = Ink,
            )
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                sub.title,
                style = MaterialTheme.typography.titleMedium,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            when (sub.daysUntil) {
                0, 1 -> Text(
                    if (sub.daysUntil == 0) "Сегодня" else "Завтра",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Peach)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
                else -> Text(
                    "через ${sub.daysUntil} ${pluralDays(sub.daysUntil)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Muted,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(rub(sub.amount), style = MaterialTheme.typography.labelLarge, color = Ink)
            Text(
                if (sub.period == SubPeriod.YEAR) "в год" else "в месяц",
                style = MaterialTheme.typography.labelSmall,
                color = Muted,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun SubscriptionsPreview() {
    MoneyTrackTheme { SubscriptionsScreen() }
}
