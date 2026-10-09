package com.example.moneytrack.model

import androidx.compose.ui.graphics.Color
import com.example.moneytrack.ui.components.LineIcons
import com.example.moneytrack.ui.theme.Connection
import com.example.moneytrack.ui.theme.ConnectionLight
import com.example.moneytrack.ui.theme.Food
import com.example.moneytrack.ui.theme.FoodLight
import com.example.moneytrack.ui.theme.Fun
import com.example.moneytrack.ui.theme.FunLight
import com.example.moneytrack.ui.theme.Health
import com.example.moneytrack.ui.theme.HealthLight
import com.example.moneytrack.ui.theme.Other
import com.example.moneytrack.ui.theme.OtherLight
import com.example.moneytrack.ui.theme.Transport
import com.example.moneytrack.ui.theme.TransportLight

/** Категории расходов: название, яркий цвет, светлый цвет плитки, иконка */
enum class Category(val title: String, val color: Color, val light: Color, val icon: List<String>) {
    FOOD("Еда", Food, FoodLight, LineIcons.Cart),
    TRANSPORT("Транспорт", Transport, TransportLight, LineIcons.Bus),
    FUN("Развлечения", Fun, FunLight, LineIcons.Film),
    HEALTH("Здоровье", Health, HealthLight, LineIcons.Heart),
    CONNECTION("Связь", Connection, ConnectionLight, LineIcons.Phone),
    OTHER("Прочее", Other, OtherLight, LineIcons.Dots),
}
