package com.project.cureconnect.data.api.Api

import androidx.compose.ui.text.rememberTextMeasurer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private fun getClient(timeoutSeconds: Long = 30): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .build()
    }

    private fun getRetrofit(baseUrl: String, timeoutSeconds: Long = 30): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(getClient(timeoutSeconds))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ✅ Lazy initialization
    val response: BackendApi by lazy {
        getRetrofit("http://192.168.207.120:8001/").create(BackendApi::class.java)
    }

    val response2: BackendApiXray by lazy {
        getRetrofit("http://192.168.207.120:8002/").create(BackendApiXray::class.java)
    }

    val responsechat: BackendApichat by lazy {
        getRetrofit("http://192.168.207.120:8000/").create(BackendApichat::class.java)
    }

    val response3: BackendApiLLM by lazy {
        // Gemini API might require more time
        getRetrofit("https://generativelanguage.googleapis.com/", timeoutSeconds = 60)
            .create(BackendApiLLM::class.java)
    }
}
