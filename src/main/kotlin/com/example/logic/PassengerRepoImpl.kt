package com.example.logic

import com.example.dao.BookDetailsDao
import com.example.dao.DatabaseFactory
import com.example.dao.FLightDetailsDao
import com.example.dao.PassengerDao
import com.example.dao.PassengerDao.id
import com.example.data.request.Flight
import com.example.data.request.Passenger
import com.example.data.request.Filter
import com.example.data.response.*
import com.example.exceptions.*
import io.ktor.http.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PassengerRepoImpl:PassengerRepo {
private val mapping=RowMapFunctions()
    override suspend fun bookFlight(flightId: String,pId:Int):Output {
        val insert=DatabaseFactory.dbQuery {
                BookDetailsDao.insert {
                it[passengerId] = pId
                it[flightNumber] = flightId
            }
        }
        return if(insert.resultedValues?.isEmpty()!=null){
            Output("$flightId has booked ", HttpStatusCode.Accepted.toString())
        }
        else{
            throw DataInsertionException("Error While Booking Flight")
        }
    }

    override suspend fun getFlight(fId: String): List<Flight> {
            val data=DatabaseFactory.dbQuery {
                FLightDetailsDao.select(FLightDetailsDao.flightNumber eq fId).map {mapping.resultRowToFlight(it) }
            }
            return data.ifEmpty {
                throw FlightNotFoundException("FLight with $fId Does Not Exists")
            }

    }

    override suspend fun getAllFLights(): List<Flight> = DatabaseFactory.dbQuery {
        FLightDetailsDao.selectAll().map { mapping.resultRowToFlight(it) }
    }

    override suspend fun userRegistration(details: Passenger): Passenger? = DatabaseFactory.dbQuery{
        val insertStatement= PassengerDao.insert {
            it[name]=details.name
            it[email]=details.email
            it[password]=details.password
        }
        insertStatement.resultedValues?.singleOrNull()?.let { mapping.resultRowPassenger(it) }
    }

    override suspend fun getPassengerId(name: String): PassengerId? = DatabaseFactory.dbQuery {
            PassengerDao.slice(id).select(PassengerDao.name eq name)
                .map { mapping.resultRowId(it) }.firstOrNull()
    }
    override suspend fun bookTicket(name:String,flightId:String):Output{
        return try {
            val user = getPassengerId(name)
            if (Methods().flightCheck(flightId)) {
                if (user != null) {
                    bookFlight(flightId, user.id)
                } else {
                    throw UserNotFoundException("$name is not register")
                }
            } else {
                throw FlightNotFoundException("Flight with $flightId Does Not Exists")
            }
        }catch (e:Exception){
            when(e){
                is DataInsertionException -> Output("$e Internal Error ", HttpStatusCode.ServiceUnavailable.toString())
                is UserNotFoundException -> Output("$e ${e.msg}", HttpStatusCode.BadRequest.toString())
                is FlightNotFoundException ->Output("$e ${e.msg} ", HttpStatusCode.BadRequest.toString())
                is ExposedSQLException ->Output("$e ", HttpStatusCode.BadRequest.toString())
                else->{
                    Output("$e System Error", HttpStatusCode.BadRequest.toString())
                }
            }
        }
    }

    override suspend fun getFlightId(id: Int): List<BookDetailsOut?> = DatabaseFactory.dbQuery{
        BookDetailsDao.select(BookDetailsDao.passengerId eq id)
            .map { mapping.resultRowBookingOut(it) }
    }
    override suspend fun getFlightById(passengerId:Int):List<Flight> = DatabaseFactory.dbQuery{
        ( BookDetailsDao innerJoin FLightDetailsDao).select(BookDetailsDao.passengerId eq passengerId)
            .map { mapping.resultRowToFlight(it) }
    }

    override suspend fun filterBySourceDestination(details: Filter): List<Flight> = DatabaseFactory.dbQuery {
        FLightDetailsDao.select(FLightDetailsDao.destination eq details.destination and (FLightDetailsDao.airport eq details.source) )
            .map { mapping.resultRowToFlight(it) }
    }

    override suspend fun userLogin(login: PassengerLogin): Output {
        return try {
            val userDetails = DatabaseFactory.dbQuery {
                PassengerDao.select(PassengerDao.name eq login.name and (PassengerDao.password eq login.password))
                    .map { mapping.resultRowLogin(it) }.firstOrNull()
            }
            if(userDetails!=null){
                val token=Methods().tokenGenerator(login.name,"login user")
                Output(token,HttpStatusCode.Accepted.toString())
            }
            else{
                throw InvalidLoginDetails()
            }
        }catch (e:Exception){
            when(e){
                is InvalidLoginDetails -> Output("$e Invalid Login Details",HttpStatusCode.Unauthorized.toString())
                is ExposedSQLException -> Output("Data Base Error",HttpStatusCode.Unauthorized.toString())
                else -> {
                    Output("System Error",HttpStatusCode.Unauthorized.toString())

                }
            }
    }
    }

    override suspend fun removeUser(id: Int): Boolean = DatabaseFactory.dbQuery {
        PassengerDao.deleteWhere { PassengerDao.id eq id }>0
    }

    override suspend fun cancelTicket(id:Int,flightId: String): Boolean =DatabaseFactory.dbQuery {
        BookDetailsDao.deleteWhere { flightNumber eq flightId and (passengerId eq id)}>0
    }


}


