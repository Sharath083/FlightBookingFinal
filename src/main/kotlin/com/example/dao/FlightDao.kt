package com.example.dao

import org.jetbrains.exposed.sql.Table

object FLightDetailsDao: Table("flight_table"){

    val flightNumber = varchar("flightNumber",20)
    val airline=varchar("airline",45)
    val price=integer("price")
    val airport = varchar("source", 20)
    val destination = varchar("destination", 20)
    val departureTime=varchar("departureTime", 20)
    val arrivalTime=varchar("arrivalTime",20)
    override val primaryKey = PrimaryKey(flightNumber)
}