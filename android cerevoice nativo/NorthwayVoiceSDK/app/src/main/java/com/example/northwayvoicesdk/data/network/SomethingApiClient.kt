package com.example.northwayvoicesdk.data.network

import retrofit2.Response
import retrofit2.http.GET

interface SomethingApiClient {

    //Declare here endpoints to get information from services
    @GET("textToSpeech")
    fun getSomething() : Response<String>
}