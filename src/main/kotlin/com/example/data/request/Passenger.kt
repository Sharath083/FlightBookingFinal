package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class Passenger(val name:String,val email:String,val password:String)