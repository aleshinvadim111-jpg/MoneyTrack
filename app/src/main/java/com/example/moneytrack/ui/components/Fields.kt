package com.example.moneytrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.moneytrack.data.Dates
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Dotted
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Unbounded
import com.example.moneytrack.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val FieldBorder = Color(0xFFD5CDBD)
val SegmentBg = Color(0xFFE6DFD1)

/** Шапка вложенного экрана: «назад»/«закрыть», подпись капсом и крупный заголовок */
@Composable
fun ScreenHeader(label: String, title: String, onBack: () -> Unit, close: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(
                icon = if (close) LineIcons.Close else LineIcons.ChevronLeft,
                description = if (close) "Закрыть" else "Назад",
                color = Ink,
                onClick = onBack,
                modifier = Modifier.offset(x = (-10).dp),
            )
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Ink)
    }
}

/** Поле ввода с подписью капсом сверху и ошибкой/подсказкой снизу */
@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    placeholder: String? = null,
    supporting: String? = null,
    error: String? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = InkSoft)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = if (placeholder != null) {
                { Text(placeholder, color = Muted) }
            } else null,
            isError = error != null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Ink,
                unfocusedBorderColor = FieldBorder,
                errorBorderColor = Danger,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                errorContainerColor = White,
                focusedTextColor = Ink,
                unfocusedTextColor = Ink,
                errorTextColor = Ink,
                cursorColor = Ink,
                errorCursorColor = Danger,
            ),
        )
        when {
            error != null -> Text(
                error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = Danger,
            )
            supporting != null -> Text(supporting, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
    }
}

/** Главная кнопка: чёрная, с салатовым квадратом-стрелкой справа */
@Composable
fun ReceiptButton(text: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (enabled) Ink else Ink.copy(alpha = 0.35f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(start = 22.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium.copy(fontFamily = Unbounded), color = White)
        Box(
            Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Lime),
            contentAlignment = Alignment.Center,
        ) {
            LineIcon(LineIcons.ArrowRight, Ink, iconSize = 20.dp, strokeWidth = 2.4f)
        }
    }
}

/** Переключатель-сегменты: «День / Неделя / Месяц» */
@Composable
fun Segmented(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SegmentBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEachIndexed { index, text ->
            val selected = index == selectedIndex
            val bg by animateColorAsState(if (selected) Ink else Color.Transparent, label = "segmentBg")
            Box(
                Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bg)
                    .clickable(role = Role.Tab) { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) Lime else InkSoft,
                )
            }
        }
    }
}

/** Поле с датой: по тапу открывается календарь */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(label: String, dateMillis: Long, onDateChange: (Long) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val text = remember(dateMillis) {
        SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("ru")).format(Date(dateMillis))
    }
    val shape = RoundedCornerShape(16.dp)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = InkSoft)
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(shape)
                .background(White)
                .border(1.dp, FieldBorder, shape)
                .clickable(role = Role.Button) { open = true }
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.bodyLarge, color = Ink)
            LineIcon(LineIcons.Calendar, Ink, strokeWidth = 1.9f)
        }
    }

    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = Dates.localToPicker(dateMillis))
        // Дата, выбранная в календаре до перехода в режим ручного ввода (иконка-карандаш)
        var dateBeforeInput by remember { mutableStateOf(state.selectedDateMillis) }
        LaunchedEffect(state.displayMode) {
            if (state.displayMode == DisplayMode.Input) dateBeforeInput = state.selectedDateMillis
        }
        val colors = DatePickerDefaults.colors(
            containerColor = Paper,
            selectedDayContainerColor = Ink,
            selectedDayContentColor = Lime,
            todayDateBorderColor = Ink,
            todayContentColor = Ink,
        )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onDateChange(Dates.pickerToLocal(it)) }
                    open = false
                }) { Text("Готово", color = Ink, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (state.displayMode == DisplayMode.Input) {
                        // Ручной ввод: «Отмена» возвращает к календарю, а не закрывает диалог.
                        // Недописанную дату откатываем к той, что была до переключения на ввод.
                        state.selectedDateMillis = dateBeforeInput
                        dateBeforeInput?.let { state.displayedMonthMillis = it }
                        state.displayMode = DisplayMode.Picker
                    } else {
                        open = false
                    }
                }) { Text("Отмена", color = Muted) }
            },
            colors = colors,
        ) {
            DatePicker(state = state, colors = colors)
        }
    }
}

/** Цвета переключателей в стиле приложения */
@Composable
fun appSwitchColors(): SwitchColors = SwitchDefaults.colors(
    checkedThumbColor = Lime,
    checkedTrackColor = Ink,
    uncheckedThumbColor = Muted,
    uncheckedTrackColor = SegmentBg,
    uncheckedBorderColor = Dotted,
)
