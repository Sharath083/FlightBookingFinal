package com.example.exceptions

class FlightNotFoundException(val msg:String):RuntimeException()
class UserNotFoundException(val msg:String):RuntimeException()
class InvalidLoginDetails:RuntimeException()
class FilterDoesNotExistException:RuntimeException()
class SameFlightIdException(val msg:String):RuntimeException()
class DataInsertionException(val msg: String):RuntimeException()
