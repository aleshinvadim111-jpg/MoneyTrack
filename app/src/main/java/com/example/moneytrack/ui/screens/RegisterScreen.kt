package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneytrack.domain.Validators
import com.example.moneytrack.ui.components.LabeledField
import com.example.moneytrack.ui.components.ReceiptButton
import com.example.moneytrack.ui.components.ScreenHeader
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.InkSoft
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onRegister: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    error: String? = null,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeat by rememberSaveable { mutableStateOf("") }
    var agree by rememberSaveable { mutableStateOf(false) }

    // Валидация по ТЗ — правила общие с ViewModel (domain/Validators)
    val emailOk = Validators.isEmailValid(email)
    val passwordOk = Validators.isPasswordValid(password)
    val sameOk = Validators.passwordsMatch(password, repeat)
    val canSubmit = Validators.isNameValid(name) && emailOk && passwordOk && sameOk && agree

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
        ScreenHeader("НОВЫЙ АККАУНТ", "Регистрация", onBack)
        Text("Данные хранятся только на этом телефоне.", style = MaterialTheme.typography.bodyMedium, color = Muted)

        LabeledField("ИМЯ", name, { name = it }, placeholder = "Владимир")
        LabeledField(
            "EMAIL", email, { email = it.trim() },
            keyboardType = KeyboardType.Email,
            placeholder = "name@mail.ru",
            error = if (email.isNotEmpty() && !emailOk) "Проверьте email" else error,
        )
        LabeledField(
            "ПАРОЛЬ", password, { password = it },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            supporting = "Минимум 8 символов, буквы и цифры",
            error = if (password.isNotEmpty() && !passwordOk) "Минимум 8 символов, буквы и цифры" else null,
        )
        LabeledField(
            "ПОВТОРИТЕ ПАРОЛЬ", repeat, { repeat = it },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            error = if (repeat.isNotEmpty() && !sameOk) "Пароли не совпадают" else null,
        )

        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .toggleable(value = agree, role = Role.Checkbox, onValueChange = { agree = it }),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = agree,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(checkedColor = Ink, uncheckedColor = Muted, checkmarkColor = Lime),
                modifier = Modifier.padding(12.dp),
            )
            Text("Согласен на обработку персональных данных", style = MaterialTheme.typography.bodyMedium, color = InkSoft)
        }

        ReceiptButton("Создать аккаунт", enabled = canSubmit, onClick = { onRegister(name.trim(), email, password) })
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun RegisterScreenPreview() {
    MoneyTrackTheme { RegisterScreen() }
}
