package com.example.plugins

import com.example.logic.FLightRepoImpl
import com.example.logic.PassengerRepoImpl
import com.example.logic.Methods
import com.example.routes.flightFunctions
import com.example.routes.passengerFunctions
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    routing {
        flightFunctions(FLightRepoImpl(), Methods())
        passengerFunctions(PassengerRepoImpl(),Methods())

    }
}
