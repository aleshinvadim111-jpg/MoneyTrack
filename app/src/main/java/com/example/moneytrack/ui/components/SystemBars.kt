package com.example.moneytrack.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Делает часы и значки в статус-баре светлыми, пока экран открыт
 * (нужно на тёмном экране входа). При уходе с экрана возвращает тёмные.
 */
@Composable
fun LightStatusBarIcons() {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        onDispose { controller.isAppearanceLightStatusBars = true }
    }
}
