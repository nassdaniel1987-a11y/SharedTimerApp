package com.example.sharedtimer.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface OneSignalApiService {
    
    @Headers("Content-Type: application/json")
    @POST("notifications")
    suspend fun sendNotification(
        @Body request: OneSignalNotificationRequest
    ): Response<OneSignalNotificationResponse>
    
    companion object {
        private const val BASE_URL = "https://onesignal.com/api/v1/"
        
        // WICHTIG: Ersetze mit deinem REST API Key
        private const val ONESIGNAL_REST_API_KEY = "YOUR_ONESIGNAL_REST_API_KEY"
        
        fun create(): OneSignalApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Basic $ONESIGNAL_REST_API_KEY")
                        .build()
                    chain.proceed(request)
                }
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OneSignalApiService::class.java)
        }
    }
}
