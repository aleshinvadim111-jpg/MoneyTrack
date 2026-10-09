package com.example.moneytrack.model

import androidx.compose.ui.graphics.Color
import com.example.moneytrack.data.Dates
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val monthlyLimit: Long,
) {
    val firstName: String get() = name.trim().substringBefore(' ')
    val initials: String
        get() = name.trim().split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifEmpty { "?" }
}

data class Expense(
    val id: Long,
    val category: Category,
    val note: String,
    val amount: Long,     // в рублях
    val dateMillis: Long, // когда потрачено
) {
    /** 0 — сегодня, 1 — вчера… */
    val daysAgo: Int get() = Dates.daysBetween(dateMillis, System.currentTimeMillis())
    val time: String get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(dateMillis))
}

enum class SubPeriod(val title: String) {
    MONTH("Каждый месяц"),
    YEAR("Каждый год"),
}

data class Subscription(
    val id: Long,
    val title: String,
    val amount: Long,
    val nextChargeMillis: Long,
    val period: SubPeriod,
    val color: Color,
    val remind: Boolean = true,
) {
    /** Сколько дней до списания */
    val daysUntil: Int get() = Dates.daysBetween(System.currentTimeMillis(), nextChargeMillis)

    /** Сколько подписка стоит в пересчёте на месяц */
    val monthlyAmount: Long get() = if (period == SubPeriod.YEAR) amount / 12 else amount
}

/** Примеры: для кнопки «Примерные данные» в Профиле и для @Preview */
object DemoData {
    fun expenses(): List<Expense> = listOf(
        Triple(0, 12 to 40, Expense(1, Category.FOOD, "Пятёрочка, продукты", 850, 0)),
        Triple(0, 9 to 15, Expense(2, Category.TRANSPORT, "Метро", 62, 0)),
        Triple(1, 20 to 10, Expense(3, Category.FUN, "Кино с друзьями", 600, 0)),
        Triple(2, 18 to 5, Expense(4, Category.HEALTH, "Аптека", 1_230, 0)),
        Triple(2, 10 to 30, Expense(5, Category.CONNECTION, "Мобильный интернет", 250, 0)),
        Triple(4, 8 to 50, Expense(6, Category.FOOD, "Кофе с собой", 290, 0)),
        Triple(6, 19 to 30, Expense(7, Category.FOOD, "Перекрёсток", 2_140, 0)),
        Triple(8, 16 to 20, Expense(8, Category.OTHER, "Подарок маме", 970, 0)),
        Triple(11, 13 to 0, Expense(9, Category.TRANSPORT, "Такси", 540, 0)),
        Triple(15, 19 to 0, Expense(10, Category.FOOD, "ВкусВилл", 1_640, 0)),
        Triple(20, 21 to 0, Expense(11, Category.FUN, "Концерт", 1_200, 0)),
        Triple(33, 12 to 0, Expense(12, Category.FOOD, "Ашан", 3_200, 0)),
        Triple(38, 9 to 0, Expense(13, Category.TRANSPORT, "Проездной", 1_500, 0)),
        Triple(45, 20 to 0, Expense(14, Category.FUN, "Боулинг", 900, 0)),
    ).map { (days, time, e) -> e.copy(dateMillis = Dates.daysAgoAt(days, time.first, time.second)) }

    fun subscriptions(): List<Subscription> = listOf(
        Subscription(1, "Яндекс Плюс", 399, Dates.daysAgoAt(-1, 10, 0), SubPeriod.MONTH, Color(0xFFFFB23F)),
        Subscription(2, "VK Музыка", 229, Dates.daysAgoAt(-5, 10, 0), SubPeriod.MONTH, Color(0xFF4D7CFE)),
        Subscription(3, "Облачное хранилище", 149, Dates.daysAgoAt(-9, 10, 0), SubPeriod.MONTH, Color(0xFF2EC4B6)),
        Subscription(4, "Telegram Premium", 299, Dates.daysAgoAt(-12, 10, 0), SubPeriod.MONTH, Color(0xFF9B6BFF)),
        Subscription(5, "Курс английского", 990, Dates.daysAgoAt(-17, 10, 0), SubPeriod.MONTH, Color(0xFFFF6B4A)),
    )
}

/** Данные только для @Preview в Android Studio */
object SampleData {
    val user = User(1, "Владимир Алешин", "v.aleshin@mail.ru", 30_000)
    val expenses: List<Expense> get() = DemoData.expenses()
    val subscriptions: List<Subscription> get() = DemoData.subscriptions()
    val rates = listOf(
        CurrencyRate("USD", "Доллар США", 1, 92.50, 92.15, "2026-10-08T11:30:00+03:00", System.currentTimeMillis()),
        CurrencyRate("EUR", "Евро", 1, 108.00, 108.40, "2026-10-08T11:30:00+03:00", System.currentTimeMillis()),
        CurrencyRate("CNY", "Китайский юань", 1, 12.90, 12.90, "2026-10-08T11:30:00+03:00", System.currentTimeMillis()),
    )
}
