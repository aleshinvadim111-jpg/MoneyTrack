package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.data.Dates
import com.example.moneytrack.model.Category
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.SampleData
import com.example.moneytrack.ui.components.CircleIconButton
import com.example.moneytrack.ui.components.LineIcon
import com.example.moneytrack.ui.components.LineIcons
import com.example.moneytrack.ui.components.rub
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.White
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Unbounded
import java.util.Calendar
import kotlin.math.roundToInt

private data class CategoryStat(val category: Category, val amount: Long)

private val months = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)

@Composable
fun StatsScreen(expenses: List<Expense> = SampleData.expenses) {
    val now = remember { Calendar.getInstance() }
    var year by rememberSaveable { mutableIntStateOf(now.get(Calendar.YEAR)) }
    var month by rememberSaveable { mutableIntStateOf(now.get(Calendar.MONTH)) }

    val prevYear = if (month == 0) year - 1 else year
    val prevMonth = (month + 11) % 12
    val monthExpenses = expenses.filter { Dates.isSameMonth(it.dateMillis, year, month) }
    val prevTotal = expenses.filter { Dates.isSameMonth(it.dateMillis, prevYear, prevMonth) }.sumOf { it.amount }

    // Суммы по категориям за месяц, самая крупная — первой (она займёт большую плитку)
    val stats = Category.entries
        .map { c -> CategoryStat(c, monthExpenses.filter { it.category == c }.sumOf { it.amount }) }
        .sortedByDescending { it.amount }
    val total = stats.sumOf { it.amount }
    fun goPrev() {
        if (month == 0) {
            month = 11
            year -= 1
        } else {
            month -= 1
        }
    }
    fun goNext() {
        if (month == 11) {
            month = 0
            year += 1
        } else {
            month += 1
        }
    }
    fun percent(s: CategoryStat): Int = if (total == 0L) 0 else (s.amount * 100f / total).roundToInt()

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
        Text("СТАТИСТИКА · $year", style = MaterialTheme.typography.labelMedium, color = Muted)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(LineIcons.ChevronLeft, "Предыдущий месяц", Ink, { goPrev() })
            Text(months[month], style = MaterialTheme.typography.headlineMedium, color = Ink)
            CircleIconButton(LineIcons.ChevronRight, "Следующий месяц", Ink, { goNext() })
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column {
                Text("ПОТРАЧЕНО", style = MaterialTheme.typography.labelSmall, color = Muted)
                Text(rub(total), style = MaterialTheme.typography.displayMedium, color = Ink)
            }
            // Сравнение с прошлым месяцем
            if (prevTotal > 0 && total > 0) {
                val diff = ((total - prevTotal) * 100f / prevTotal).roundToInt()
                Text(
                    if (diff >= 0) "↑ $diff%" else "↓ ${-diff}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (diff > 0) Color(0xFFFF8A6B) else Lime,
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Ink)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }

        if (total == 0L) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(White)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("Пусто", style = MaterialTheme.typography.titleLarge, color = Ink)
                Text(
                    "В этом месяце трат нет. Добавьте расход кнопкой «+».",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                )
            }
        } else {

            StackedBar(stats.filter { it.amount > 0 })

            // Bento-плитки
            Row(
                Modifier.fillMaxWidth().height(210.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BigTile(stats[0], percent(stats[0]), Modifier.weight(1f).fillMaxHeight())
                Column(
                    Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SmallTile(stats[1], percent(stats[1]), Modifier.weight(1f).fillMaxWidth())
                    SmallTile(stats[2], percent(stats[2]), Modifier.weight(1f).fillMaxWidth())
                }
            }
            Row(
                Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SmallTile(stats[3], percent(stats[3]), Modifier.weight(1f).fillMaxHeight())
                SmallTile(stats[4], percent(stats[4]), Modifier.weight(1f).fillMaxHeight())
            }
            WideTile(stats[5], percent(stats[5]))
        }
    }
}

@Composable
private fun StackedBar(stats: List<CategoryStat>) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        stats.forEach {
            Box(
                Modifier
                    .weight(it.amount.toFloat())
                    .fillMaxHeight()
                    .background(it.category.color)
            )
        }
    }
}

@Composable
private fun BigTile(s: CategoryStat, percent: Int, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(24.dp))
            .background(s.category.light)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        LineIcon(s.category.icon, Ink, iconSize = 30.dp, strokeWidth = 1.8f)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                "$percent%",
                style = TextStyle(fontFamily = Unbounded, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 44.sp),
                color = Ink,
            )
            Text(s.category.title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(rub(s.amount), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal), color = InkSoft)
        }
    }
}

@Composable
private fun SmallTile(s: CategoryStat, percent: Int, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(24.dp))
            .background(s.category.light)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("$percent%", style = MaterialTheme.typography.titleLarge, color = Ink)
        Column {
            Text(s.category.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp), color = Ink)
            Text(rub(s.amount), style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal, letterSpacing = 0.sp), color = InkSoft)
        }
    }
}

@Composable
private fun WideTile(s: CategoryStat, percent: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(s.category.light)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$percent%", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), color = Ink)
            Text(s.category.title, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp), color = Ink)
        }
        Text(rub(s.amount), style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal), color = InkSoft)
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun StatsScreenPreview() {
    MoneyTrackTheme { StatsScreen() }
}
