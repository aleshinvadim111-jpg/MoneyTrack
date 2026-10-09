package com.example.moneytrack

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.moneytrack.ui.navigation.MoneyTrackApp
import com.example.moneytrack.ui.theme.MoneyTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Приложение всегда «бумажное», поэтому значки статус-бара тёмные.
        // На тёмном экране входа они временно становятся светлыми (LightStatusBarIcons).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        val container = (application as MoneyTrackApplication).container
        setContent {
            MoneyTrackTheme {
                MoneyTrackApp(container)
            }
        }
    }
}
