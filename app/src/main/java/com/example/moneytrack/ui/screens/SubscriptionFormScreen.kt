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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneytrack.data.DAY_MS
import com.example.moneytrack.model.SubPeriod
import com.example.moneytrack.model.Subscription
import com.example.moneytrack.ui.components.DatePickerField
import com.example.moneytrack.ui.components.DeleteButton
import com.example.moneytrack.ui.components.LabeledField
import com.example.moneytrack.ui.components.LineIcon
import com.example.moneytrack.ui.components.LineIcons
import com.example.moneytrack.ui.components.ReceiptButton
import com.example.moneytrack.ui.components.ScreenHeader
import com.example.moneytrack.ui.components.Segmented
import com.example.moneytrack.ui.components.appSwitchColors
import com.example.moneytrack.ui.theme.Connection
import com.example.moneytrack.ui.theme.Food
import com.example.moneytrack.ui.theme.Fun
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Other
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Transport
import com.example.moneytrack.ui.theme.White

private val LabelColors = listOf(Color(0xFFFFB23F), Transport, Connection, Fun, Food, Other)
private val LabelColorNames = listOf("Янтарный", "Синий", "Бирюзовый", "Фиолетовый", "Томатный", "Песочный")

@Composable
fun SubscriptionFormScreen(
    initial: Subscription? = null, // null — новая подписка, иначе редактируем эту
    onClose: () -> Unit = {},
    onSave: (Subscription) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val isEdit = initial != null
    var title by rememberSaveable { mutableStateOf(initial?.title ?: "") }
    var amount by rememberSaveable { mutableStateOf(initial?.amount?.toString() ?: "") }
    var dateMillis by rememberSaveable { mutableLongStateOf(initial?.nextChargeMillis ?: (System.currentTimeMillis() + DAY_MS)) }
    var periodIndex by rememberSaveable { mutableIntStateOf(initial?.period?.ordinal ?: 0) }
    var colorIndex by rememberSaveable {
        mutableIntStateOf(
            initial?.let { s -> LabelColors.indexOfFirst { it.toArgb() == s.color.toArgb() } }?.takeIf { it >= 0 } ?: 0
        )
    }
    var remind by rememberSaveable { mutableStateOf(initial?.remind ?: true) }
    val canSave = title.isNotBlank() && (amount.toLongOrNull() ?: 0L) > 0L

    // Живой предпросмотр: как подписка будет выглядеть в списке
    val preview = Subscription(
        id = initial?.id ?: 0,
        title = title.ifBlank { "Название" },
        amount = amount.toLongOrNull() ?: 0L,
        nextChargeMillis = dateMillis,
        period = SubPeriod.entries[periodIndex],
        color = LabelColors[colorIndex],
        remind = remind,
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 18.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(if (isEdit) "ИЗМЕНИТЬ ПОДПИСКУ" else "НОВАЯ ПОДПИСКА", "Подписка", onClose, close = true)

        SubscriptionRow(preview, onClick = null)

        LabeledField("НАЗВАНИЕ", title, { title = it }, placeholder = "Например, Яндекс Плюс")
        LabeledField(
            "СУММА, ₽", amount, { v -> amount = v.filter { it.isDigit() }.take(7) },
            keyboardType = KeyboardType.Number,
            placeholder = "399",
        )
        DatePickerField("СЛЕДУЮЩЕЕ СПИСАНИЕ", dateMillis) { dateMillis = it }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("ПЕРИОД", style = MaterialTheme.typography.labelMedium, color = InkSoft)
            Segmented(SubPeriod.entries.map { it.title }, periodIndex, { periodIndex = it })
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ЦВЕТ МЕТКИ", style = MaterialTheme.typography.labelMedium, color = InkSoft)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LabelColors.forEachIndexed { i, c ->
                    val selected = i == colorIndex
                    Box(
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(c)
                            .then(if (selected) Modifier.border(3.dp, Ink, CircleShape) else Modifier)
                            .clickable(role = Role.RadioButton) { colorIndex = i }
                            .semantics { contentDescription = LabelColorNames[i] },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) LineIcon(LineIcons.Check, Ink, iconSize = 20.dp, strokeWidth = 2.6f)
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(White)
                .clickable(role = Role.Switch) { remind = !remind }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Напомнить за день", style = MaterialTheme.typography.titleMedium, color = Ink)
                Text("Уведомление накануне списания", style = MaterialTheme.typography.bodySmall, color = Muted)
            }
            Switch(checked = remind, onCheckedChange = { remind = it }, colors = appSwitchColors())
        }

        ReceiptButton("Сохранить", enabled = canSave, onClick = { onSave(preview.copy(title = title.trim())) })

        if (isEdit) {
            DeleteButton(
                text = "Удалить подписку",
                confirmTitle = "Удалить подписку?",
                confirmText = "Она пропадёт из списка, напоминаний о ней больше не будет.",
                onConfirm = onDelete,
            )
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun SubscriptionFormPreview() {
    MoneyTrackTheme { SubscriptionFormScreen() }
}
