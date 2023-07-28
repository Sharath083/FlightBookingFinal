package com.example.data.response

import kotlinx.serialization.Serializable


@Serializable
data class TravelTime(val ticketNumber:Int,val duration:String,val flightId:String)