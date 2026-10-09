package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.domain.CurrencyMath
import com.example.moneytrack.model.SampleData
import com.example.moneytrack.ui.components.DashedDivider
import com.example.moneytrack.ui.components.LabeledField
import com.example.moneytrack.ui.components.LineIcon
import com.example.moneytrack.ui.components.LineIcons
import com.example.moneytrack.ui.components.ReceiptCard
import com.example.moneytrack.ui.components.ReceiptLine
import com.example.moneytrack.ui.components.appSwitchColors
import com.example.moneytrack.ui.components.rub
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Unbounded
import com.example.moneytrack.ui.theme.White
import com.example.moneytrack.ui.viewmodel.RatesUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    name: String = "Владимир Алешин",
    email: String = "v.aleshin@mail.ru",
    initials: String = "ВА",
    limit: Long = 30_000,
    onLimitChange: (Long) -> Unit = {},
    onFillDemo: () -> Unit = {},
    onLogout: () -> Unit = {},
    rates: RatesUiState = RatesUiState(SampleData.rates),
    onRefreshRates: () -> Unit = {},
    notifications: Boolean = true,
    onNotificationsChange: (Boolean) -> Unit = {},
    onCheckReminders: () -> Unit = {},
) {
    var editLimit by remember { mutableStateOf(false) }

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
        Text("ПРОФИЛЬ", style = MaterialTheme.typography.labelMedium, color = Muted)

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Ink)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Lime),
                contentAlignment = Alignment.Center,
            ) {
                Text(initials, style = MaterialTheme.typography.titleLarge, color = Ink)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = Unbounded, fontSize = 17.sp),
                    color = White,
                )
                Text(email, style = MaterialTheme.typography.bodyMedium, color = Color(0xFFA9A7A1))
            }
        }

        Text("НАСТРОЙКИ", style = MaterialTheme.typography.labelMedium, color = InkSoft, modifier = Modifier.padding(top = 4.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(White)
        ) {
            SettingRow(LineIcons.Wallet, "Месячный лимит", rub(limit), onClick = { editLimit = true }) {
                LineIcon(LineIcons.ChevronRight, Muted, iconSize = 20.dp)
            }
            Box(Modifier.padding(horizontal = 16.dp)) { DashedDivider() }
            SettingRow(LineIcons.Bell, "Уведомления", "Лимит и подписки", onClick = { onNotificationsChange(!notifications) }) {
                Switch(checked = notifications, onCheckedChange = onNotificationsChange, colors = appSwitchColors())
            }
            if (notifications) {
                Box(Modifier.padding(horizontal = 16.dp)) { DashedDivider() }
                SettingRow(
                    LineIcons.Calendar,
                    "Проверить напоминания",
                    "Уведомить о завтрашних списаниях сейчас",
                    onClick = onCheckReminders,
                ) {
                    LineIcon(LineIcons.ChevronRight, Muted, iconSize = 20.dp)
                }
            }
            Box(Modifier.padding(horizontal = 16.dp)) { DashedDivider() }
            SettingRow(LineIcons.Plus, "Примерные данные", "Заполнить пустой аккаунт для демонстрации", onClick = onFillDemo) {
                LineIcon(LineIcons.ChevronRight, Muted, iconSize = 20.dp)
            }
        }

        RatesCard(rates, onRefreshRates)

        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.5.dp, Danger, RoundedCornerShape(18.dp))
                .clickable(role = Role.Button, onClick = onLogout),
            contentAlignment = Alignment.Center,
        ) {
            Text("Выйти из аккаунта", style = MaterialTheme.typography.titleMedium, color = Danger)
        }
    }

    if (editLimit) {
        LimitDialog(
            current = limit,
            onDismiss = { editLimit = false },
            onSave = {
                onLimitChange(it)
                editLimit = false
            },
        )
    }
}

@Composable
private fun SettingRow(
    icon: List<String>,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Paper),
            contentAlignment = Alignment.Center,
        ) {
            LineIcon(icon, Ink, iconSize = 20.dp, strokeWidth = 1.9f)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        trailing()
    }
}

/** Курсы ЦБ: данные из кэша Room, обновляются по сети */
@Composable
private fun RatesCard(state: RatesUiState, onRefresh: () -> Unit) {
    val timeFormat = remember { SimpleDateFormat("dd.MM 'в' HH:mm", Locale.forLanguageTag("ru")) }
    ReceiptCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("КУРСЫ ВАЛЮТ · ЦБ РФ", style = MaterialTheme.typography.labelMedium, color = Ink)
            if (state.loading) {
                CircularProgressIndicator(Modifier.size(18.dp), color = Ink, strokeWidth = 2.dp)
            } else {
                Text(
                    "обновить",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(role = Role.Button, onClick = onRefresh)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                )
            }
        }
        DashedDivider()

        if (state.rates.isEmpty()) {
            Text(
                if (state.loading) "Загружаем курсы…" else "Курсы ещё не загружены. Проверьте интернет и нажмите «обновить».",
                style = MaterialTheme.typography.bodyMedium,
                color = Muted,
            )
        }
        state.rates.forEach { r ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Box(Modifier.weight(1f)) { ReceiptLine(r.code, CurrencyMath.formatRate(r.perUnit)) }
                Text(
                    CurrencyMath.formatChange(r.change),
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        r.change > 0.004 -> Danger // рубль дешевеет — плохо для кошелька
                        r.change < -0.004 -> InkSoft
                        else -> Muted
                    },
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .widthIn(min = 56.dp),
                )
            }
        }

        state.error?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = Danger)
        }
        val footer = listOfNotNull(
            state.cbrDate?.let { "Курс на ${CurrencyMath.formatCbrDate(it)}" },
            state.updatedAt?.let { "загружено ${timeFormat.format(Date(it))}" },
        ).joinToString(" · ")
        if (footer.isNotEmpty()) {
            Text(footer, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
    }
}

@Composable
private fun LimitDialog(current: Long, onDismiss: () -> Unit, onSave: (Long) -> Unit) {
    var text by remember { mutableStateOf(current.toString()) }
    val value = text.toLongOrNull() ?: 0L
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Paper,
        title = { Text("Месячный лимит", style = MaterialTheme.typography.titleLarge, color = Ink) },
        text = {
            LabeledField(
                "СУММА, ₽", text, { v -> text = v.filter { it.isDigit() }.take(9) },
                keyboardType = KeyboardType.Number,
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(value) }, enabled = value > 0) {
                Text("Сохранить", color = Ink, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена", color = Muted) }
        },
    )
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun ProfilePreview() {
    MoneyTrackTheme { ProfileScreen() }
}
