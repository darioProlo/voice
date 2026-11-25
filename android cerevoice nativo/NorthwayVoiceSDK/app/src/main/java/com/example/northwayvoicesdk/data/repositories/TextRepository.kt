package com.example.northwayvoicesdk.data.repositories

import com.example.northwayvoicesdk.data.network.SomethingApiClient
import com.example.northwayvoicesdk.data.models.TextToSpeech
import com.example.northwayvoicesdk.data.network.SomethingService
import javax.inject.Inject

class TextRepository @Inject constructor(private val service: SomethingService) {

    fun getText(): String {
        return service.getSomething()
    }
}