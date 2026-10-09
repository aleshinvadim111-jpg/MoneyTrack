package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneytrack.model.Expense
import com.example.moneytrack.model.SampleData
import com.example.moneytrack.ui.components.ScreenHeader
import com.example.moneytrack.ui.components.Segmented
import com.example.moneytrack.ui.components.SwipeExpenseCard
import com.example.moneytrack.ui.components.rub
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.White
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class Period(val title: String, val days: Int, val caption: String) {
    DAY("День", 1, "ЗА СЕГОДНЯ"),
    WEEK("Неделя", 7, "ЗА 7 ДНЕЙ"),
    MONTH("Месяц", 30, "ЗА 30 ДНЕЙ"),
}

private fun dayLabel(daysAgo: Int): String = when (daysAgo) {
    0 -> "Сегодня"
    1 -> "Вчера"
    else -> {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgo) }
        SimpleDateFormat("d MMMM", Locale.forLanguageTag("ru")).format(cal.time)
    }
}

@Composable
fun HistoryScreen(
    expenses: List<Expense> = SampleData.expenses,
    onBack: () -> Unit = {},
    onDelete: (Expense) -> Unit = {},
    onRestore: (Expense) -> Unit = {},
    onOpen: (Expense) -> Unit = {},
) {
    var period by rememberSaveable { mutableStateOf(Period.WEEK) }
    val visible = expenses.filter { it.daysAgo < period.days }
    val groups = visible.groupBy { it.daysAgo }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        Modifier
            .fillMaxSize()
            .background(Paper)
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { ScreenHeader("ИСТОРИЯ", "Все траты", onBack) }
            item {
                Segmented(
                    options = Period.entries.map { it.title },
                    selectedIndex = period.ordinal,
                    onSelect = { period = Period.entries[it] },
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(period.caption, style = MaterialTheme.typography.labelMedium, color = Muted)
                    Text(rub(visible.sumOf { it.amount }), style = MaterialTheme.typography.titleLarge, color = Ink)
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        "За этот период трат нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Muted,
                        modifier = Modifier.padding(vertical = 40.dp),
                    )
                }
            }
            groups.forEach { (daysAgo, list) ->
                item(key = "day-$daysAgo") {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(dayLabel(daysAgo).uppercase(), style = MaterialTheme.typography.labelMedium, color = InkSoft)
                        Text(rub(list.sumOf { it.amount }), style = MaterialTheme.typography.labelMedium, color = InkSoft)
                    }
                }
                items(list, key = { it.id }) { expense ->
                    SwipeExpenseCard(
                        expense = expense,
                        onClick = { onOpen(expense) },
                        onDelete = {
                            onDelete(expense)
                            scope.launch {
                                val result = snackbar.showSnackbar(
                                    message = "Расход удалён",
                                    actionLabel = "Отменить",
                                    duration = SnackbarDuration.Short,
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onRestore(expense)
                                }
                            }
                        },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }

        SnackbarHost(
            snackbar,
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        ) { data ->
            Snackbar(
                data,
                shape = RoundedCornerShape(16.dp),
                containerColor = Ink,
                contentColor = White,
                actionColor = Lime,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HistoryScreenPreview() {
    MoneyTrackTheme { HistoryScreen() }
}
