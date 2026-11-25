package com.example.northwayvoicesdk.data.models

import com.google.gson.annotations.SerializedName

data class TextToSpeech(
    @SerializedName("text") var text: String? = null
)
