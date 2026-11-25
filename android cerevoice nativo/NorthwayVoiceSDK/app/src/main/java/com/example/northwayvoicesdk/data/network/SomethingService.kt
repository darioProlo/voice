package com.example.northwayvoicesdk.data.network

import javax.inject.Inject

class SomethingService @Inject constructor(private val api: SomethingApiClient){

    fun getSomething() : String {
        val response = api.getSomething()
        return response.body().toString()
    }
}