package com.project.cureconnect.data.api.Api

import androidx.compose.ui.text.rememberTextMeasurer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private fun getInstance() : Retrofit{
        return Retrofit.Builder().baseUrl("http://192.168.207.120:8001/").addConverterFactory(GsonConverterFactory.create()).build()
    }
    private fun getInstance1() : Retrofit{
        return Retrofit.Builder().baseUrl("http://192.168.207.120:8000/").addConverterFactory(GsonConverterFactory.create()).build()
    }
    private fun getInstance2() : Retrofit{
        return Retrofit.Builder().baseUrl("http://192.168.207.120:8002/").addConverterFactory(GsonConverterFactory.create()).build()
    }
    private fun getInstance3() : Retrofit{
        return Retrofit.Builder().baseUrl("https://generativelanguage.googleapis.com/").addConverterFactory(GsonConverterFactory.create()).build()
    }
    val response = getInstance().create(BackendApi::class.java)
    val response2 = getInstance2().create(BackendApiXray::class.java)
    val responsechat = getInstance1().create(BackendApichat::class.java)
    val response3 = getInstance3().create(BackendApiLLM::class.java)

}