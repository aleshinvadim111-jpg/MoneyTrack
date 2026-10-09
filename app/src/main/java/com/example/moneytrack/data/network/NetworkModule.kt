package com.example.moneytrack.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Настройка сети: один HTTP-клиент и один Retrofit на всё приложение */
object NetworkModule {
    private const val CBR_BASE_URL = "https://www.cbr-xml-daily.ru/"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    val cbrApi: CbrApi by lazy {
        Retrofit.Builder()
            .baseUrl(CBR_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CbrApi::class.java)
    }
}
