package com.example.logic

import com.example.dao.BookDetailsDao
import com.example.dao.DatabaseFactory
import com.example.dao.FLightDetailsDao
import com.example.dao.PassengerDao
import com.example.data.request.Flight
import com.example.data.response.Output
import com.example.data.response.OutputListOfPassengers
import com.example.data.response.PassengerDetails
import com.example.exceptions.FlightNotFoundException
import com.example.exceptions.SameFlightIdException
import com.example.exceptions.UserNotFoundException
import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.lang.IllegalArgumentException

class FLightRepoImpl : FLightRepo {
    private val mapping=RowMapFunctions()
    override suspend fun addNewFlight(input: Flight): Output {
        return try {
            DatabaseFactory.dbQuery {
                if(Methods().flightCheck(input.flightNumbers) ) {
                    FLightDetailsDao.insert {
                        it[flightNumber] = input.flightNumbers
                        it[airline] = input.airline
                        it[price] = input.price
                        it[airport] = input.source
                        it[destination] = input.destination
                        it[departureTime] = input.departureTime
                        it[arrivalTime] = input.arrivalTime
                    }
                    Output("Add New Flight ", HttpStatusCode.Accepted.toString())
                }
                else{
                    throw SameFlightIdException("FLight With ${input.flightNumbers} Already Exists")
                }
            }
        }catch (e:Exception){
            when(e){
                is IllegalArgumentException -> Output("$e $input", HttpStatusCode.BadRequest.toString())
                is SameFlightIdException -> Output("$e ${e.msg}", HttpStatusCode.BadRequest.toString())
                is ExposedSQLException -> Output("$e InternalError", HttpStatusCode.InternalServerError.toString())
                else -> {
                    Output(" $e System Error", HttpStatusCode.ServiceUnavailable.toString())
                }
            }
        }
    }

    override suspend fun removeFLight(id: String): Output {
        return try {
            val result= DatabaseFactory.dbQuery {
                FLightDetailsDao.deleteWhere { flightNumber eq id }>0
            }
            if(result){
                Output("Flight $id Has Removed", HttpStatusCode.Accepted.toString())
            }
            else{
                throw FlightNotFoundException("FLight With $id Does Not Exists")
            }
        }catch (e:Exception){
            when(e) {
                is IllegalArgumentException -> Output("$e ", HttpStatusCode.BadRequest.toString())
                is FlightNotFoundException -> Output("$e ${e.msg}", HttpStatusCode.BadRequest.toString())
                is ExposedSQLException -> Output("$e InternalError", HttpStatusCode.InternalServerError.toString())
                else -> {
                    Output(" $e System Error", HttpStatusCode.ServiceUnavailable.toString())
                }
            }
        }
    }
    override suspend fun getPassengerCountByFlight(flightId: String): Output {
        return try {
            DatabaseFactory.dbQuery {
                if (Methods().flightCheck(flightId)) {
                    val count=BookDetailsDao.select(BookDetailsDao.flightNumber eq flightId).count()
                    Output("Flight $flightId has $count Passengers", HttpStatusCode.Accepted.toString())
                } else {
                    throw FlightNotFoundException("FLight With $flightId Does Not Exists")
                }
            }
        }catch (e:Exception){
            when(e) {
                is IllegalArgumentException -> Output("$e ", HttpStatusCode.BadRequest.toString())
                is FlightNotFoundException -> Output("$e ${e.msg}", HttpStatusCode.BadRequest.toString())
                is ExposedSQLException -> Output("$e InternalError", HttpStatusCode.InternalServerError.toString())
                else -> {
                    Output(" $e System Error", HttpStatusCode.ServiceUnavailable.toString())
                }
            }
        }
    }
    override suspend fun getAllPassengers(): Any {
        return try {
            val list= DatabaseFactory.dbQuery {
                (BookDetailsDao innerJoin PassengerDao).slice(PassengerDao.name, PassengerDao.email).selectAll()
                    .map { mapping.resultRowPassengerDetails(it) }
            }
            if (list.isNotEmpty()){
                OutputListOfPassengers(list.toSet(), HttpStatusCode.Accepted.toString())
            }
            else{
                throw UserNotFoundException("The Users List Is Empty")
            }
        }catch (e:Exception){
            when(e) {
                is ExposedSQLException -> Output("$e InternalError", HttpStatusCode.InternalServerError.toString())
                else -> {
                    Output(" $e System Error", HttpStatusCode.ServiceUnavailable.toString())
                }
            }
        }
    }

}