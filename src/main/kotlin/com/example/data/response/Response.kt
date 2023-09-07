package com.example.data.response

import com.example.data.request.Flight
import kotlinx.serialization.Serializable

@Serializable
sealed class Response<out T>() {
    @Serializable
    data class OutputList<T>(val data:T,val status: String):Response<T>()
    @Serializable
    data class Output(val response:String,val status:String):Response<Nothing>()
}