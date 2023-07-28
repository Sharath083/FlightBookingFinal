package com.example

import com.example.dao.DatabaseFactory
import com.example.logic.Methods
import io.ktor.server.application.*

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configureStatusPage()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
