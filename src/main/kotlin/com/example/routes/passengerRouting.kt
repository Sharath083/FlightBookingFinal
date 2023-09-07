package com.example.routes

import com.example.logic.Methods
import com.example.data.response.TravelDetails
import com.example.data.response.TravelTime
import com.example.data.request.Passenger
import com.example.data.request.Filter
import com.example.data.request.FilterBy
import com.example.data.request.FlightId
import com.example.data.response.PassengerLogin
import com.example.exceptions.FilterDoesNotExistException
import com.example.exceptions.FlightNotFoundException
import com.example.exceptions.UserNotFoundException
import com.example.logic.PassengerRepoImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun Route.passengerFunctions(passengerRepoImpl: PassengerRepoImpl, methods: Methods) {
    route("/passenger") {



        post("/register") {
            val details = call.receive<Passenger>()
            passengerRepoImpl.userRegistration(details)
            call.respond("${details.name} is Added")
        }
        post("/login") {
            val details=call.receive<PassengerLogin>()
            val result=passengerRepoImpl.userLogin(details)
            call.respond(result)
        }
        authenticate {
            get("/travelTime") {
                val principal = call.principal<JWTPrincipal>()
                val name = principal!!.payload.getClaim("user").asString()
                runBlocking {
                    launch {
                        val result = passengerRepoImpl.getTravelTime(name)
                        call.respond(result)
                    }
                }
            }
        }
        authenticate {
            post("/bookFlight") {
                val flightId = call.receive<FlightId>()
                val principal = call.principal<JWTPrincipal>()
                val name = principal!!.payload.getClaim("user").asString()
                val output=passengerRepoImpl.bookTicket(name,flightId.flightId)
                call.respond(output)
            }
        }
        authenticate {
            get("/all-flights-Of-Passenger") {
                val principal = call.principal<JWTPrincipal>()
                val name = principal!!.payload.getClaim("user").asString()
                val id = passengerRepoImpl.getPassengerId(name)
                if (id != null) {
                    val bookingDetails = passengerRepoImpl.getFlightId(id.id)
                    runBlocking {
                        val result = bookingDetails.map { number ->
                            async {
                                number?.let { it1 ->
                                    TravelDetails(it1.ticket, passengerRepoImpl.getFlight(number.flightNumber))
                                }
                            }
                        }
                        call.respond(result.awaitAll())
                    }
                } else {
                    throw UserNotFoundException("$name does not exists")
                }
            }
        }

        get("/Id={id}"){
            val id=call.parameters["id"]?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val result=passengerRepoImpl.getFlightById(id.toInt())
            if(result.isNotEmpty()){
                call.respond(result)
            }
            else{
                call.respond("$id does not have any bookings")
            }
        }
        get("/filterSource&Destination"){
            val input=call.receive<Filter>()
            val result=passengerRepoImpl.filterBySourceDestination(input).sortedBy { methods.timeTaken(it.departureTime,it.arrivalTime) }
            if(result.isNotEmpty()){
                call.respond(result )
            }
            else{
                call.respond("THERE ARE NO FLIGHTS BETWEEN ${input.source} AND ${input.destination}")
            }
        }


        get("/filterBy") {
            val input=call.receive<FilterBy>()
            val result = passengerRepoImpl.getFLightsByFilter(input)
            call.respond(result)
        }
//        authenticate {
//            delete("/deleteAccount") {
//                val principal = call.principal<JWTPrincipal>()
//                val name = principal!!.payload.getClaim("user").asString()
//                val details = passengerRepoImpl.getPassengerId(name)
//                if(details!=null) {
//                    passengerRepoImpl.removeUser(details.id)
//                    call.respond("$name Account Is Deleted")
//
//                }
//                else {
//                    throw UserNotFoundException("$name Does Not Have Account")
//                }
//
//            }
//        }
        authenticate {
            delete("cancelFlight/{id}") {
                val flightNumber=call.parameters["id"]?: return@delete call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )
                val principal = call.principal<JWTPrincipal>()
                val name = principal!!.payload.getClaim("user").asString()
                val details = passengerRepoImpl.getPassengerId(name)

                if(details!=null) {
                    if (passengerRepoImpl.cancelTicket(details.id,flightNumber)){
                        call.respond("Ticket Has Been Cancelled")
                    }
                    else{
                        throw FlightNotFoundException("$name Has not Booked The Flight")
                    }
                }
                else {
                    throw UserNotFoundException("$name Does Not Have Account")
                }
            }
        }
    }
}



