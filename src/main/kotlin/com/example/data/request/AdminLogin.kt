package com.example.data.request

import kotlinx.serialization.Serializable


@Serializable
data class AdminLogin(val name:String,val password:String)