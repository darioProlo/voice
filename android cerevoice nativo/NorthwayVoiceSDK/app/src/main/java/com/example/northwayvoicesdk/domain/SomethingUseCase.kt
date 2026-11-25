package com.example.northwayvoicesdk.domain

import com.example.northwayvoicesdk.data.repositories.TextRepository
import javax.inject.Inject

class SomethingUseCase @Inject constructor(private val repository: TextRepository) {

    fun invoke(): String = "I've got another confession to make I'm your fool Everyone's got their chains to break Holding you"//repository.getText()
}