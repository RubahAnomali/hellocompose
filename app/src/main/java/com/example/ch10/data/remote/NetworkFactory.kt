package com.example.ch10.data.remote

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

// Factory jaringan dipakai NetworkModule dan pengujian MockWebServer.

const val API_KEY_HEADER = "X-Api-Key"
const val NEWS_BASE_URL = "https://newsapi.org/"

val newsJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

fun createOkHttpClient(
    apiKey: String,
    enableLogging: Boolean
): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = if (enableLogging) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        redactHeader(API_KEY_HEADER)
    }

    return OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(apiKey))
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
}

fun createNewsApiService(
    baseUrl: String,
    client: OkHttpClient,
    json: Json = newsJson
): NewsApiService = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(client)
    .addConverterFactory(
        json.asConverterFactory("application/json".toMediaType())
    )
    .build()
    .create(NewsApiService::class.java)

class ApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .header(API_KEY_HEADER, apiKey)
                .build()
        )
}