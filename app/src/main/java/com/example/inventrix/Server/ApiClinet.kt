package com.example.inventrix.Server

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClinet {
    private const val BASE_URL = "http://192.168.1.7:8080/inventrix/"

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Logging interceptor (untuk debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor: selalu ambil token terbaru dari SharedPreferences
    private fun getAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val prefs = appContext?.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE)
            val token = prefs?.getString("TOKEN", null)

            val requestBuilder = original.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    // Client dibuat ulang setiap kali instance diakses agar token update
    private fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(getAuthInterceptor()) // ambil token terbaru tiap request
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // Retrofit instance yang selalu pakai client baru
    val instance: ApiService
        get() = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient()) // build ulang client
            .build()
            .create(ApiService::class.java)
}
