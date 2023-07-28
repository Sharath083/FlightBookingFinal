package com.example.data.request

import com.example.dao.FLightDetailsDao
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow


@Serializable
data class Flight(
    val airline:String,
    val price:Int,
    val source:String,
    val destination:String,
    val departureTime: String,
    val arrivalTime: String,
    val flightNumbers:String

    )






