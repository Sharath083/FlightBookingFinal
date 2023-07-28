package com.example.dao

import org.jetbrains.exposed.sql.Table


object PassengerDao: Table("passenger_table"){
    val id = integer("id").autoIncrement()
    val name=varchar("name",45)
    val email=varchar("email",45)
    val password=varchar("password",45)
    override val primaryKey=PrimaryKey(id)


}