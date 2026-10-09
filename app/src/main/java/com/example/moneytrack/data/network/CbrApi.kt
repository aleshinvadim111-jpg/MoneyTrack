package com.example.moneytrack.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

/**
 * API курсов ЦБ РФ (cbr-xml-daily.ru) — бесплатно, без ключа.
 * Ответ: {"Date": "...", "Valute": {"USD": {...}, "EUR": {...}, ...}}
 */
interface CbrApi {
    @GET("daily_json.js")
    suspend fun daily(): CbrDailyResponse
}

/** Ответ целиком. Поля nullable: если сервер что-то не пришлёт, приложение не упадёт. */
data class CbrDailyResponse(
    @SerializedName("Date") val date: String?,
    @SerializedName("Valute") val valute: Map<String, CbrValute>?,
)

/** Одна валюта. Value — сколько рублей стоят Nominal единиц валюты (для CNY Nominal бывает 1 или 10). */
data class CbrValute(
    @SerializedName("CharCode") val charCode: String?,
    @SerializedName("Nominal") val nominal: Int?,
    @SerializedName("Name") val name: String?,
    @SerializedName("Value") val value: Double?,
    @SerializedName("Previous") val previous: Double?,
)
