package com.example.logic

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.dao.FLightDetailsDao
import com.example.dao.PassengerDao
import com.example.data.response.Output
import io.ktor.http.*
import io.ktor.server.application.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select

class Methods {

    fun timeTaken(departureTime: String, arrivalTime: String): Int{
        val startTime = LocalTime.parse(departureTime, DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay()

        val endTime = LocalTime.parse(arrivalTime, DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay()

        return ((endTime - startTime) / 3600)
    }
    fun timeConvert(time: String): Int {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).toSecondOfDay()

    }
    fun tokenGenerator(name:String,secret:String):String{

        val audience = "http://0.0.0.0:8080/hello"
        val issuer = "http://0.0.0.0:8080/"
        val jwtRealm = "ktor sample app"
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("user",name)
            .withExpiresAt(Date(System.currentTimeMillis() + 600000))
            .sign(Algorithm.HMAC256(secret))
    }
    fun flightCheck(flightId: String): Boolean {
        val data=
            FLightDetailsDao.select(FLightDetailsDao.flightNumber eq flightId).map { RowMapFunctions().resultRowToFlight(it) }
        return data.isEmpty()
    }
    fun adminLogin(name:String,password:String):Output{
        return if(name == "Admin" && password=="123456") {
            val token=tokenGenerator(name,"login Admin")
            Output( token ,"Expires in 10  Minutes")
        }
        else{
            Output("Imposter",HttpStatusCode.Unauthorized.toString())
        }

    }

    fun checkUser(name: String, email: String): Boolean {
        val data=PassengerDao.select(PassengerDao.name eq name or (PassengerDao.email eq email)).map { RowMapFunctions().resultRowPassenger(it) }
        return data.isEmpty()
    }
}