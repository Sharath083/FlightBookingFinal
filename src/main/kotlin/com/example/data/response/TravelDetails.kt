package com.example.data.response

import com.example.data.request.Flight
import kotlinx.serialization.Serializable

@Serializable
data class TravelDetails(val ticketNumber:Int,val flight:List<Flight>?)


