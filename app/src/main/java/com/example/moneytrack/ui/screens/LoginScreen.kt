package com.example.moneytrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytrack.domain.Validators
import com.example.moneytrack.ui.components.LabeledField
import com.example.moneytrack.ui.components.LightStatusBarIcons
import com.example.moneytrack.ui.components.ReceiptButton
import com.example.moneytrack.ui.components.ZigzagTopShape
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Lime
import com.example.moneytrack.ui.theme.MoneyTrackTheme
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper
import com.example.moneytrack.ui.theme.Unbounded
import com.example.moneytrack.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {},
    error: String? = null,
) {
    LightStatusBarIcons() // светлые часы и значки на тёмном фоне

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val canLogin = Validators.isEmailValid(email) && password.length >= Validators.PASSWORD_MIN_LENGTH
    val today = remember { SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")).format(Date()) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MONEYTRACK", style = MaterialTheme.typography.labelMedium, color = Lime)
                Text("КАССА №1", style = MaterialTheme.typography.labelMedium, color = Color(0xFF8E8C86))
            }
            Text(
                buildAnnotatedString {
                    append("Считай\nкаждый\n")
                    withStyle(SpanStyle(color = Lime)) { append("рубль.") }
                },
                style = TextStyle(
                    fontFamily = Unbounded,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 46.sp,
                    lineHeight = 50.sp,
                    letterSpacing = (-0.5).sp,
                ),
                color = White,
            )
            Text(
                "Расходы, лимит и подписки — в одном чеке.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFB7B5AF),
                modifier = Modifier.widthIn(max = 260.dp),
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .clip(ZigzagTopShape())
                .background(Paper)
                .navigationBarsPadding()
                .imePadding()
                .padding(start = 22.dp, end = 22.dp, top = 30.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("Вход", style = MaterialTheme.typography.titleLarge, color = Ink)
                Text(today, style = MaterialTheme.typography.labelSmall, color = Muted)
            }
            LabeledField("EMAIL", email, { email = it.trim() }, keyboardType = KeyboardType.Email, placeholder = "name@mail.ru")
            LabeledField(
                "ПАРОЛЬ", password, { password = it },
                keyboardType = KeyboardType.Password,
                isPassword = true,
                error = error,
            )
            ReceiptButton(
                "Войти",
                enabled = canLogin,
                onClick = { onLogin(email, password) },
                modifier = Modifier.padding(top = 6.dp),
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Нет аккаунта? ", style = MaterialTheme.typography.bodyMedium, color = Muted)
                Text(
                    "Регистрация",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink,
                    modifier = Modifier
                        .clickable(role = Role.Button, onClick = onRegister)
                        .padding(vertical = 12.dp),
                )
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun LoginScreenPreview() {
    MoneyTrackTheme { LoginScreen() }
}
