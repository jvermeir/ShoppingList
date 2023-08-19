package com.example

import com.example.database.DatabaseFactory

data class DatabaseConfig(
    val driverClass: String,
    val url: String,
    val user: String,
    val password: String
)

lateinit var databaseConfig: DatabaseConfig

lateinit var databaseConnectionFactory: DatabaseFactory