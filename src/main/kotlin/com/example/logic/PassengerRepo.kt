package com.example.logic

import com.example.data.request.Flight
import com.example.data.request.Passenger
import com.example.data.request.Filter
import com.example.data.response.*

interface PassengerRepo {
//    suspend fun flightCheck(flightId: String):Boolean

    suspend fun bookFlight(flightId: String,pId:Int): Output
    suspend fun getFlight(fId:String):Any
    suspend fun userRegistration(details: Passenger):Passenger?
    suspend fun getPassengerId(name:String): PassengerId?
    suspend fun getFlightId(id:Int):List<BookDetailsOut?>
    suspend fun getAllFLights():List<Flight>
    suspend fun getFlightById(passengerId:Int):List<Flight>
    suspend fun filterBySourceDestination(details:Filter):List<Flight>
    suspend fun userLogin(login: PassengerLogin):Output
    suspend fun removeUser(id: Int):Boolean
    suspend fun cancelTicket(id:Int,flightId: String):Boolean
    suspend fun bookTicket(name:String,flightId:String):Output





//    suspend fun journeyDuration(name:String):List<Flight>
}