package com.project.cureconnect.pateints.Api

import androidx.compose.ui.text.rememberTextMeasurer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val baseUrl = "http://192.168.207.120:8001/"
    private val baseUrl1 = "http://192.168.207.120:8000/"
    private val baseUrl2 = "http://192.168.207.120:8002/"
    private fun getInstance() : Retrofit{
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }
    private fun getInstance1() : Retrofit{
        return Retrofit.Builder().baseUrl(baseUrl1).addConverterFactory(GsonConverterFactory.create()).build()
    }
    private fun getInstance2() : Retrofit{
        return Retrofit.Builder().baseUrl(baseUrl2).addConverterFactory(GsonConverterFactory.create()).build()
    }
    val response = getInstance().create(BackendApi::class.java)
    val response2 = getInstance2().create(BackendApiXray::class.java)
    val responsechat = getInstance1().create(BackendApichat::class.java)
}