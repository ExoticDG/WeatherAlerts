// RetrofitClient.kt
package com.exoticdg.weatheralerts.network // Adjust package name

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient // Optional: for advanced configuration like logging

object RetrofitClient {

    private const val BASE_URL = "https://api.weather.gov/"

    // Optional: Configure OkHttpClient (e.g., add interceptors for logging)
    private val okHttpClient = OkHttpClient.Builder()
        // .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)) // Example: Add logging for debugging
        .build()

    val instance: NwsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use the configured OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // Add Gson support
            .build()

        retrofit.create(NwsApiService::class.java) // Create the API service implementation
    }
}