package com.example.moneytrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytrack.ui.theme.Danger
import com.example.moneytrack.ui.theme.Ink
import com.example.moneytrack.ui.theme.Muted
import com.example.moneytrack.ui.theme.Paper

/** Красная кнопка «Удалить» с подтверждением — чтобы запись нельзя было стереть случайно */
@Composable
fun DeleteButton(
    text: String,
    confirmTitle: String,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var ask by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(role = Role.Button) { ask = true }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LineIcon(LineIcons.Trash, Danger, iconSize = 20.dp)
        Text(text, style = MaterialTheme.typography.titleMedium, color = Danger)
    }

    if (ask) {
        AlertDialog(
            onDismissRequest = { ask = false },
            containerColor = Paper,
            title = { Text(confirmTitle, style = MaterialTheme.typography.titleLarge, color = Ink) },
            text = { Text(confirmText, style = MaterialTheme.typography.bodyMedium, color = Muted) },
            confirmButton = {
                TextButton(onClick = {
                    ask = false
                    onConfirm()
                }) { Text("Удалить", color = Danger, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { ask = false }) { Text("Отмена", color = Muted) }
            },
        )
    }
}
