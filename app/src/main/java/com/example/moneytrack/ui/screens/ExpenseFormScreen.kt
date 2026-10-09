package com.example.moneytrack.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.model.Category
import com.example.moneytrack.model.Expense
import com.example.moneytrack.ui.components.DashedDivider
import com.example.moneytrack.ui.components.DatePickerField
import com.example.moneytrack.ui.components.DeleteButton
import com.example.moneytrack.ui.components.LabeledField
import com.example.moneytrack.ui.components.LineIcon
import com.example.moneytrack.ui.components.ReceiptButton
import com.example.moneytrack.ui.components.ReceiptCard
import com.example.moneytrack.ui.components.ScreenHeader
import com.example.moneytrack.ui.theme.Dotted
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Unbounded
import com.example.moneytrack.ui.theme.White

private val AmountStyle = TextStyle(
    fontFamily = Unbounded,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 44.sp,
    lineHeight = 50.sp,
)

@Composable
fun ExpenseFormScreen(
    initial: Expense? = null, // null — новый расход, иначе редактируем этот
    onClose: () -> Unit = {},
    onSave: (category: Category, amount: Long, dateMillis: Long, note: String) -> Unit = { _, _, _, _ -> },
    onDelete: () -> Unit = {},
) {
    val isEdit = initial != null
    var amount by rememberSaveable { mutableStateOf(initial?.amount?.toString() ?: "") }
    var category by rememberSaveable { mutableStateOf(initial?.category) }
    var dateMillis by rememberSaveable { mutableLongStateOf(initial?.dateMillis ?: System.currentTimeMillis()) }
    var comment by rememberSaveable { mutableStateOf(initial?.note ?: "") }
    val canSave = (amount.toLongOrNull() ?: 0L) > 0L && category != null

    // Новый расход: клавиатура открывается сразу — ввод за пару секунд.
    // При редактировании не мешаем: пользователь сам выберет, что менять.
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { if (!isEdit) focus.requestFocus() }

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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ScreenHeader(
            if (isEdit) "ИЗМЕНИТЬ РАСХОД" else "НОВЫЙ РАСХОД",
            if (isEdit) "Правим чек" else "Пробиваем чек",
            onClose,
            close = true,
        )

        ReceiptCard {
            Text("СУММА", style = MaterialTheme.typography.labelMedium, color = Muted)
            Row(verticalAlignment = Alignment.Bottom) {
                BasicTextField(
                    value = amount,
                    onValueChange = { v -> amount = v.filter { it.isDigit() }.take(9) },
                    singleLine = true,
                    textStyle = AmountStyle.copy(color = Ink),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(Ink),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focus),
                    decorationBox = { inner ->
                        Box {
                            if (amount.isEmpty()) Text("0", style = AmountStyle, color = Dotted)
                            inner()
                        }
                    },
                )
                Text("₽", style = AmountStyle.copy(fontSize = 32.sp), color = Muted)
            }
            DashedDivider()
            Text("КАТЕГОРИЯ", style = MaterialTheme.typography.labelMedium, color = Muted)
            CategoryGrid(selected = category, onSelect = { category = it })
        }

        DatePickerField("ДАТА", dateMillis) { dateMillis = it }

        LabeledField(
            "КОММЕНТАРИЙ", comment, { if (it.length <= 200) comment = it },
            placeholder = "Например, Пятёрочка",
            supporting = "${comment.length} / 200",
        )

        ReceiptButton(if (isEdit) "Сохранить" else "Пробить чек", enabled = canSave, onClick = {
            val c = category
            val sum = amount.toLongOrNull()
            if (c != null && sum != null && sum > 0) onSave(c, sum, dateMillis, comment.trim())
        })

        if (isEdit) {
            DeleteButton(
                text = "Удалить расход",
                confirmTitle = "Удалить расход?",
                confirmText = "Запись исчезнет из истории и статистики.",
                onConfirm = onDelete,
            )
        }
    }
}

@Composable
private fun CategoryGrid(selected: Category?, onSelect: (Category) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Category.entries.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { c ->
                    val isSelected = c == selected
                    val bg by animateColorAsState(if (isSelected) Ink else c.light, label = "categoryBg")
                    Column(
                        Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .clickable(role = Role.RadioButton) { onSelect(c) }
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                    ) {
                        LineIcon(c.icon, if (isSelected) Lime else Ink, iconSize = 24.dp, strokeWidth = 1.8f)
                        Text(
                            c.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                            color = if (isSelected) White else Ink,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun ExpenseFormPreview() {
    MoneyTrackTheme { ExpenseFormScreen() }
}
