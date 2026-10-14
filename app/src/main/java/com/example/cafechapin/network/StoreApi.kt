package com.example.cafechapin.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private const val BASE_URL = "https://6acf0d25f338359cf1c3ef97.mockapi.io/api/v1/"

private val json = Json {
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        json.asConverterFactory("application/json".toMediaType())
    )
    .build()

object StoreApi {
    val orderApi: OrderApi by lazy {
        retrofit.create(OrderApi::class.java)
    }
}
