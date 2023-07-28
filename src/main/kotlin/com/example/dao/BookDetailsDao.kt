package com.example.dao

import org.jetbrains.exposed.sql.Table

object BookDetailsDao :Table("booking_table"){
    val ticketNo=integer("ticket").autoIncrement("123451234")

    val passengerId=reference("passengerId",PassengerDao.id)

    val flightNumber=reference("flight-number",FLightDetailsDao.flightNumber)


    override val primaryKey = PrimaryKey(ticketNo)

}

