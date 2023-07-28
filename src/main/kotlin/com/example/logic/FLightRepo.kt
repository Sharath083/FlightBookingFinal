package com.example.logic

import com.example.data.request.Flight
import com.example.data.response.Output
interface FLightRepo {
    suspend fun addNewFlight(input: Flight): Output
    suspend fun removeFLight(id: String): Output
    suspend fun getAllPassengers():Any
    suspend fun getPassengerCountByFlight(flightId:String):Output


}