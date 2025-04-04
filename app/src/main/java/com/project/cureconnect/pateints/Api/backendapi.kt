package com.project.cureconnect.pateints.Api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.POST

interface BackendApi {
    @POST("ecg")  // Your backend API endpoint
    suspend fun uploadImageUrl(
        @Body request: RequestBody // Pass image URL as JSON
    ): Response<jsonModel>  // Replace with your actual response model
}


interface BackendApichat {
    @POST("chat")  // Your backend API endpoint
    suspend fun uploadprompt(
        @Body request: RequestBody // Pass image URL as JSON
    ): Response<chatmodel>  // Replace with your actual response model
}
interface BackendApiXray {
    @POST("model")  // Your backend API endpoint
    suspend fun uploadImageUrl(
        @Body request: RequestBody // Pass image URL as JSON
    ): Response<jsonModel>  // Replace with your actual response model
}