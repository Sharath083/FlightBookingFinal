package com.example.data.response

import com.example.data.request.Flight
import kotlinx.serialization.Serializable

@Serializable
data class OutputListOfPassengers(val flights:Set<PassengerDetails>, val status:String)