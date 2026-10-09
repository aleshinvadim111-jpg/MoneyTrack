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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.domain.CurrencyMath
import com.example.moneytrack.domain.LimitCheck
import com.example.moneytrack.domain.LimitLevel
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.SampleData
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.ui.components.Barcode
import com.example.moneytrack.ui.components.BudgetRing
import com.example.moneytrack.ui.components.DashedDivider
import com.example.moneytrack.ui.components.LineIcon
import com.example.moneytrack.ui.components.LineIcons
import com.example.moneytrack.ui.components.ReceiptCard
import com.example.moneytrack.ui.components.ReceiptRow
import com.example.moneytrack.ui.components.pluralDays
import com.example.moneytrack.ui.components.groupDigits
import com.example.moneytrack.ui.components.rub
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Peach
import com.example.moneytrack.ui.theme.Unbounded
import com.example.moneytrack.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun HomeScreen(
    userName: String = SampleData.user.firstName,
    initials: String = SampleData.user.initials,
    spent: Long = 17_600,
    limit: Long = 30_000,
    todayExpenses: List<Expense> = SampleData.expenses.filter { it.daysAgo == 0 },
    nearestSubscription: Subscription? = SampleData.subscriptions.minByOrNull { it.daysUntil },
    usdRate: Double? = 92.5, // курс ЦБ из сети/кэша; null — ещё не загружен
    onOpenHistory: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenExpense: (Expense) -> Unit = {},
) {
    val dateLabel = remember { SimpleDateFormat("dd.MM", Locale.forLanguageTag("ru")).format(Date()) }

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
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("MONEYTRACK · $dateLabel", style = MaterialTheme.typography.labelMedium, color = Muted)
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Ink)
                    .clickable(onClick = onOpenProfile)
                    .semantics { contentDescription = "Профиль" },
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, style = MaterialTheme.typography.labelLarge.copy(fontFamily = Unbounded), color = Lime)
            }
        }

        Text("Привет,\n$userName", style = MaterialTheme.typography.headlineMedium, color = Ink)

        LimitCard(spent = spent, limit = limit, usdRate = usdRate)

        // Предупреждение: от 80% лимита — персиковая плашка, после превышения — красная
        LimitWarning(spent = spent, limit = limit)

        // Плашка появляется, если списание в ближайшие 3 дня
        nearestSubscription?.takeIf { it.daysUntil in 0..3 }?.let { SubscriptionBanner(it) }

        ReceiptCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("ЧЕК ЗА СЕГОДНЯ", style = MaterialTheme.typography.labelMedium, color = Ink)
                Text(
                    "все траты →",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier
                        .clickable(onClick = onOpenHistory)
                        .padding(vertical = 8.dp),
                )
            }
            DashedDivider()
            if (todayExpenses.isEmpty()) {
                Text(
                    "Сегодня трат ещё нет. Нажмите «+», чтобы пробить первый чек.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                )
            }
            todayExpenses.forEach { e ->
                val subtitle = listOf(e.note, e.time).filter { it.isNotBlank() }.joinToString(", ")
                ReceiptRow(
                    e.category.title, subtitle, e.amount,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenExpense(e) }
                        .padding(vertical = 4.dp),
                )
            }
            DashedDivider()
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("ИТОГО", style = MaterialTheme.typography.labelMedium, color = Ink)
                Text(rub(todayExpenses.sumOf { it.amount }), style = MaterialTheme.typography.titleLarge, color = Ink)
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Barcode()
                Text("№ ${dateLabel.replace(".", "")}-${todayExpenses.size.toString().padStart(2, '0')}", style = MaterialTheme.typography.labelSmall, color = Muted)
            }
        }
    }
}

@Composable
private fun LimitWarning(spent: Long, limit: Long) {
    val level = LimitCheck.level(spent, limit)
    if (level == LimitLevel.OK) return
    val exceeded = level == LimitLevel.EXCEEDED
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (exceeded) Danger else Peach)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LineIcon(LineIcons.Bell, if (exceeded) White else Ink, iconSize = 22.dp)
        Column(Modifier.weight(1f)) {
            Text(
                if (exceeded) "Лимит превышен" else "Потрачено ${LimitCheck.percent(spent, limit)}% лимита",
                style = MaterialTheme.typography.titleMedium,
                color = if (exceeded) White else Ink,
            )
            Text(
                if (exceeded) "Перерасход ${rub(spent - limit)}. Лимит можно изменить в Профиле."
                else "До конца месяца осталось ${rub(limit - spent)}",
                style = MaterialTheme.typography.bodySmall,
                color = if (exceeded) White else InkSoft,
            )
        }
    }
}

@Composable
private fun LimitCard(spent: Long, limit: Long, usdRate: Double?) {
    val left = limit - spent
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Ink)
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BudgetRing(spent = spent, limit = limit)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                if (left >= 0) "ОСТАЛОСЬ" else "ПЕРЕРАСХОД",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFA9A7A1),
            )
            Text(
                rub(abs(left)),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp, lineHeight = 32.sp),
                color = White,
                maxLines = 1,
            )
            Text("из ${rub(limit)}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFC9C7C1))
            // Пересчёт остатка в доллары по курсу ЦБ (если курс ещё не загружен — не показываем)
            usdRate?.let { CurrencyMath.rubToForeign(abs(left), it) }?.let { usd ->
                Text(
                    "≈ \$${groupDigits(usd)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Ink,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Lime)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun SubscriptionBanner(sub: Subscription) {
    val days = sub.daysUntil
    val whenText = when (days) {
        0 -> "Сегодня"
        1 -> "Завтра"
        else -> "Через $days ${pluralDays(days)}"
    }
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Peach)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LineIcon(LineIcons.Bell, Ink, iconSize = 20.dp, strokeWidth = 1.9f)
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(whenText) }
                append(" спишется ${sub.title}")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        Text(rub(sub.amount), style = MaterialTheme.typography.labelLarge, color = Ink)
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeScreenPreview() {
    MoneyTrackTheme { HomeScreen() }
}
