package com.example.plugins


import com.example.exceptions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<FlightNotFoundException> { call, cause->
            call.respondText(text = "400: $cause : ${cause.msg}",status= HttpStatusCode.BadRequest)
        }
        exception<UserNotFoundException> { call, cause->
            call.respondText(text = "400: $cause : ${cause.msg}",status= HttpStatusCode.BadRequest)
        }
        exception<InvalidLoginDetails>{ call, cause->
            call.respondText(text = "400: $cause : $cause",status= HttpStatusCode.NotFound)
        }
        exception<FilterDoesNotExistException>{ call, cause->
            call.respondText(text = "400: $cause : $cause",status= HttpStatusCode.NotFound)
        }
        exception<SameFlightIdException>{ call, cause->
            call.respondText(text = "400: $cause : ${cause.msg}",status= HttpStatusCode.BadRequest)}
    }
}