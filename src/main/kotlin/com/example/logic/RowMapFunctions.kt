package com.example.logic

import com.example.dao.BookDetailsDao
import com.example.dao.FLightDetailsDao
import com.example.dao.PassengerDao
import com.example.data.request.BookDetails
import com.example.data.request.Flight
import com.example.data.request.Passenger
import com.example.data.response.BookDetailsOut
import com.example.data.response.PassengerDetails
import com.example.data.response.PassengerId
import com.example.data.response.PassengerLogin
import org.jetbrains.exposed.sql.ResultRow

class RowMapFunctions {
    fun resultRowToFlight(row: ResultRow): Flight {
        return  Flight( row[FLightDetailsDao.airline], row[FLightDetailsDao.price], row[FLightDetailsDao.airport],row[FLightDetailsDao.destination],row[FLightDetailsDao.departureTime],row[FLightDetailsDao.arrivalTime],row[FLightDetailsDao.flightNumber])

    }
    fun resultRowPassenger(row: ResultRow): Passenger {
        return Passenger(row[PassengerDao.name],row[PassengerDao.email],row[PassengerDao.password])
    }
    fun resultRowBooking(row: ResultRow): BookDetails {
        return BookDetails(row[BookDetailsDao.passengerId],row[BookDetailsDao.flightNumber])
    }
    fun resultRowId(row: ResultRow): PassengerId {
        return PassengerId(row[PassengerDao.id])
    }
    fun resultRowBookingOut(row: ResultRow): BookDetailsOut {
        return BookDetailsOut(row[BookDetailsDao.ticketNo],row[BookDetailsDao.passengerId],row[BookDetailsDao.flightNumber])
    }
    fun resultRowLogin(row: ResultRow): PassengerLogin {
        return PassengerLogin(row[PassengerDao.name],row[PassengerDao.password])
    }
    fun resultRowPassengerDetails(row: ResultRow): PassengerDetails {
        return PassengerDetails(row[PassengerDao.name],row[PassengerDao.email])
    }
}