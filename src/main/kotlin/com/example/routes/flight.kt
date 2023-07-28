package com.example.routes


import com.example.data.request.AdminLogin
import com.example.data.request.Flight
import com.example.data.request.FlightId
import com.example.logic.FLightRepoImpl

import com.example.logic.Methods
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.flightFunctions(flightRepoImpl: FLightRepoImpl, methods:Methods){
    route("/flight") {
        post("/adminLogin") {
            val details = call.receive<AdminLogin>()
            val output = methods.adminLogin(details.name, details.password)
            call.respond(output)
        }
        authenticate("Admin") {
            post("/addNewFlight") {
                val params = call.receive<Flight>()
                val output = flightRepoImpl.addNewFlight(params)
                call.respond(output)
            }
        }
        authenticate("Admin") {
            delete("/removeFlight") {
                val params = call.receive<FlightId>()
                val output = flightRepoImpl.removeFLight(params.flightId)
                call.respond(output)
            }
        }
        authenticate("Admin") {
            get("/countPassengers/{flightId}") {
                val params = call.receive<FlightId>()
                val count = flightRepoImpl.getPassengerCountByFlight(params.flightId)
                call.respond("$count Passengers has booked the Flight")

            }
        }
        authenticate("Admin") {
            get("getAllPassengers") {
                val result = flightRepoImpl.getAllPassengers()
                call.respond(result)
            }
        }
    }


}