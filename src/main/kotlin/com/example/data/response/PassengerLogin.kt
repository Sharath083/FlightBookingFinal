package com.example.data.response

import kotlinx.serialization.Serializable

@Serializable
data class PassengerLogin(val name: String, val password: String)