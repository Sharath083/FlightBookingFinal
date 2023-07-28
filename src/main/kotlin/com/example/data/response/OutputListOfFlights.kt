package com.example.data.response

import com.example.data.request.Flight
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class OutputListOfFlights(val flights:List<Flight>,val status:String)