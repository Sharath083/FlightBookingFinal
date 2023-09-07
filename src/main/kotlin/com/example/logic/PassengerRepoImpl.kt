package com.example.logic

import com.example.dao.BookDetailsDao
import com.example.dao.DatabaseFactory
import com.example.dao.FLightDetailsDao
import com.example.dao.PassengerDao
import com.example.dao.PassengerDao.id
import com.example.data.request.Flight
import com.example.data.request.Passenger
import com.example.data.request.Filter
import com.example.data.request.FilterBy
import com.example.data.response.*
import com.example.exceptions.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.descriptors.listSerialDescriptor
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PassengerRepoImpl:PassengerRepo {
    private val mapping=RowMapFunctions()
    private val methods=Methods()
    private val passengerRepoImpl=PassengerRepoImpl()

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

    override suspend fun getFLightsByFilter(filter:FilterBy): Response<List<Flight>> {
        return try {
            val list = DatabaseFactory.dbQuery {
                FLightDetailsDao.selectAll()
                    .map { mapping.resultRowToFlight(it) }
                    .filter { it.source.equals(filter.from,true) && it.destination.equals(filter.to,true) }
            }
            filterBy(filter.type,list)
        }catch (e:Exception){
            when(e){
                is ExposedSQLException -> Response.Output("$e",HttpStatusCode.BadRequest.toString())
                else->{
                    Response.Output("$e System Error", HttpStatusCode.BadRequest.toString())
                }
            }
        }
    }
    private fun filterBy(type:String,list:List<Flight>): Response<List<Flight>> {
        when(type.lowercase()){
            "price"->list.sortedBy { it.price }
            "duration"->list.sortedBy { methods.timeTaken(it.departureTime,it.arrivalTime) }
        }
        return Response.OutputList(list,HttpStatusCode.Accepted.toString())
    }
    override suspend fun userRegistration(details: Passenger):Response<String>{
        return try {
            if(methods.checkUser(details.name,details.email)) {
                val insertStatement = DatabaseFactory.dbQuery {
                    PassengerDao.insert {
                        it[name] = details.name
                        it[email] = details.email
                        it[password] = details.password
                    }
                }
                Response.Output("Registered Successfully",HttpStatusCode.Accepted.toString())
            }
            else{
                throw UserAlreadyExistsException("${details.name} or ${details.email} Already Exists ")
            }
        }catch (e:Exception){
            when(e){
                is UserAlreadyExistsException -> Response.Output("$e ${e.msg}",HttpStatusCode.BadRequest.toString())
                else->{
                    Response.Output("$e System Error", HttpStatusCode.BadRequest.toString())
                }
            }
        }
    }
    override suspend fun bookTicket(name:String,flightId:String):Output{
        return try {
            val user = getPassengerId(name)
            if (methods.flightCheck(flightId)) {
                bookFlight(flightId, user.id)
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
    override fun getPassengerId(name: String): PassengerId  {
        return PassengerDao.slice(id).select(PassengerDao.name eq name)
            .map { mapping.resultRowId(it) }.firstOrNull() ?:throw UserNotFoundException("User with $name DOes Not Exists")

    }
    override  fun getFlightId(id: Int): List<BookDetailsOut?> {
        return BookDetailsDao.select(BookDetailsDao.passengerId eq id)
            .map { mapping.resultRowBookingOut(it) }
    }
    private suspend fun time(flightId: String):String{
        val time=DatabaseFactory.dbQuery {
            FLightDetailsDao.select(FLightDetailsDao.flightNumber eq flightId).map { mapping.resultRowToFlight(it) }
                .firstOrNull()
        }
            return if(time==null){
                throw FlightNotFoundException("FLight With ID $flightId Does Not Exists")
            }
            else {
                methods.timeTaken(time.departureTime, time.arrivalTime).toString()
            }
    }
    override suspend fun getTravelTime(name: String): Response<List<TravelTime>> {
        return try {
            val id = getPassengerId(name)
            val bookingDetails = getFlightId(id.id)
            val list= mutableListOf<TravelTime>()
            bookingDetails.map { it?.let { details->
                list.add(TravelTime(details.ticket,time(details.flightNumber),details.flightNumber))
            } }
            if(list.isNotEmpty()){
                Response.OutputList(list,HttpStatusCode.Accepted.toString())
            }
            else{
                throw NoBookingsException("User Does Not Have Any Bookings")
            }

        }catch (e:Exception){
            when(e){
                is NoBookingsException ->Response.Output("$e ${e.msg} ", HttpStatusCode.BadRequest.toString())
                is FlightNotFoundException ->Response.Output("$e ${e.msg} ", HttpStatusCode.BadRequest.toString())
                is ExposedSQLException ->Response.Output("$e ", HttpStatusCode.BadRequest.toString())
                else -> {
                    Response.Output("$e System Error", HttpStatusCode.BadRequest.toString())

                }
            }

        }
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
                val token=methods.tokenGenerator(login.name,"login user")
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

    override suspend fun deleteAccount(name:String): Response<String>
    {
        return try {
            val res = DatabaseFactory.dbQuery {
                val id = getPassengerId(name).id
                PassengerDao.deleteWhere { PassengerDao.id eq id } > 0
            }
            if (res) {
                Response.Output("Your Has Deleted", HttpStatusCode.Accepted.toString())
            }
            else{
                throw UserNotFoundException("User With Name $name Does Not Exists")
            }
        }catch (e:Exception){
            when(e){
                is ExposedSQLException -> Response.Output("Data Base Error",HttpStatusCode.Unauthorized.toString())
                else->{
                    Response.Output("$e System Error", HttpStatusCode.BadRequest.toString())
                }

            }
        }
    }

    override suspend fun cancelTicket(id:Int,flightId: String): Boolean =DatabaseFactory.dbQuery {
        BookDetailsDao.deleteWhere { flightNumber eq flightId and (passengerId eq id)}>0
    }


}


