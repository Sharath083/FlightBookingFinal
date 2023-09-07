package com.example.data.request

import kotlinx.serialization.Serializable

@Serializable
data class FilterBy(val from:String,val to:String,val type:String)