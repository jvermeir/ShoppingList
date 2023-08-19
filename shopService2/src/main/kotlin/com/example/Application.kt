package com.example

import com.example.database.DatabaseConnectionFactoryImpl
import com.example.models.CustomerStoreSQL
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import jsonkotlinx.Customer
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    println(">>>>>>>>>>>>>>Application")

    val database = environment.config.config("ktor.database")
    databaseConfig = DatabaseConfig(
        database.property("driverClass").getString(),
        database.property("url").getString(),
        database.property("user").getString(),
        database.property("password").getString()
    )
    println("databaseConfig: $databaseConfig")

    databaseConnectionFactory = DatabaseConnectionFactoryImpl(databaseConfig)

    databaseConnectionFactory.connect().createStatement().use {
        it.execute("CREATE TABLE IF NOT EXISTS customer (ID VARCHAR(36) PRIMARY KEY, FIRSTNAME VARCHAR(255), LASTNAME VARCHAR(255), EMAIL VARCHAR(255))")
    }

    val customerStore = CustomerStoreSQL

    configureRouting(customerStore)
    configureSerialization()
}
