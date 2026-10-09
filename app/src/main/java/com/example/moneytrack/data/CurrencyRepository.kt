package com.example.moneytrack.data

import com.example.moneytrack.data.network.CbrApi
import com.example.moneytrack.domain.CurrencyMath
import com.example.moneytrack.model.CurrencyRate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Состояние последней загрузки курсов */
sealed interface RatesStatus {
    data object Idle : RatesStatus
    data object Loading : RatesStatus
    data class Error(val message: String) : RatesStatus
}

/**
 * Курсы валют по схеме «сначала кэш»:
 * экраны всегда читают курсы из Room, а сеть только обновляет этот кэш.
 * Нет интернета — показываем последний сохранённый курс и сообщение об ошибке.
 */
class CurrencyRepository(
    private val dao: CurrencyDao,
    private val api: CbrApi,
    private val now: () -> Long = System::currentTimeMillis,
) {
    private val mutex = Mutex()

    private val _status = MutableStateFlow<RatesStatus>(RatesStatus.Idle)
    val status: StateFlow<RatesStatus> = _status.asStateFlow()

    /** Курсы из базы в порядке USD, EUR, CNY. Обновляются сами, когда меняется таблица. */
    val rates: Flow<List<CurrencyRate>> = dao.observeAll().map { list ->
        list.map { it.toModel() }.sortedBy { CurrencyMath.TRACKED.indexOf(it.code) }
    }

    /** force = false — скачиваем, только если кэш старше часа; true — кнопка «Обновить» */
    suspend fun refresh(force: Boolean = false) = mutex.withLock {
        if (!force && !CurrencyMath.isStale(dao.lastUpdated(), now())) return@withLock
        _status.value = RatesStatus.Loading
        _status.value = try {
            val response = api.daily()
            val time = now()
            val date = response.date.orEmpty()
            val fresh = response.valute.orEmpty().values.mapNotNull { v ->
                val code = v.charCode ?: return@mapNotNull null
                if (code !in CurrencyMath.TRACKED) return@mapNotNull null
                CurrencyRateEntity(
                    code = code,
                    name = v.name.orEmpty(),
                    nominal = (v.nominal ?: 1).coerceAtLeast(1),
                    value = v.value ?: return@mapNotNull null,
                    previous = v.previous ?: v.value,
                    cbrDate = date,
                    updatedAt = time,
                )
            }
            if (fresh.isEmpty()) {
                RatesStatus.Error("Сервер ЦБ вернул пустой ответ")
            } else {
                dao.upsert(fresh)
                RatesStatus.Idle
            }
        } catch (e: CancellationException) {
            throw e // экран закрыли — это не ошибка сети
        } catch (e: Exception) {
            RatesStatus.Error("Нет соединения с ЦБ — показан сохранённый курс")
        }
    }
}
