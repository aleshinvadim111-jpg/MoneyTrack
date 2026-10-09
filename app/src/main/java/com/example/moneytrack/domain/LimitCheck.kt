package com.example.moneytrack.domain

/** Насколько близко траты к месячному лимиту */
enum class LimitLevel { OK, WARNING, EXCEEDED }

/** Правила предупреждений о лимите. Чистый Kotlin — покрыт юнит-тестами. */
object LimitCheck {
    /** С какого процента лимита начинаем предупреждать */
    const val WARNING_PERCENT = 80

    fun level(spent: Long, limit: Long): LimitLevel = when {
        limit <= 0 -> LimitLevel.OK
        spent > limit -> LimitLevel.EXCEEDED
        spent * 100 >= limit * WARNING_PERCENT -> LimitLevel.WARNING
        else -> LimitLevel.OK
    }

    fun percent(spent: Long, limit: Long): Int = if (limit <= 0) 0 else (spent * 100 / limit).toInt()

    /**
     * Перешли ли траты на новый уровень после добавления расхода.
     * Возвращает новый уровень (WARNING или EXCEEDED) или null, если уведомлять не нужно.
     * Так уведомление приходит один раз — в момент пересечения границы, а не после каждой траты.
     */
    fun crossed(spentBefore: Long, spentAfter: Long, limit: Long): LimitLevel? {
        val before = level(spentBefore, limit)
        val after = level(spentAfter, limit)
        return if (after.ordinal > before.ordinal) after else null
    }
}
