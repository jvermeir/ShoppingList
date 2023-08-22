package com.example

import com.example.database.DatabaseConnectionFactoryImpl
import com.example.database.DummyDatabaseConnectionFactoryImpl
import com.example.models.dynamo.CustomerStoreDynamo
import com.example.models.dynamo.OrderStoreDynamo
import com.example.models.sql.CustomerStoreSQL
import com.example.models.sql.OrderStoreSQL
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    println(">>>>>>>>>>>>>>Application")
    val databaseType = environment.config.propertyOrNull("ktor.databaseType")?.getString()
    if (databaseType == "dynamo") {
        dynamoDatabase(this.environment)
    } else {
        h2Database(this.environment)
    }

    configureRouting()
    configureSerialization()
}

fun dynamoDatabase(environment:ApplicationEnvironment) {
    // TODO: fix paramlist
    databaseConnectionFactory = DummyDatabaseConnectionFactoryImpl(DatabaseConfig("","","",""))
    customerStore = CustomerStoreDynamo
    orderStore = OrderStoreDynamo
}

fun h2Database(environment:ApplicationEnvironment) {
    val database = environment.config.config("ktor.database")
    val databaseConfig = DatabaseConfig(
        database.property("driverClass").getString(),
        database.property("url").getString(),
        database.property("user").getString(),
        database.property("password").getString()
    )
    println("databaseConfig: $databaseConfig")

    databaseConnectionFactory = DatabaseConnectionFactoryImpl(databaseConfig)

    databaseConnectionFactory.connect().createStatement().use {
        it.execute("CREATE TABLE IF NOT EXISTS customers (ID VARCHAR(36) PRIMARY KEY, FIRSTNAME VARCHAR(255), LASTNAME VARCHAR(255), EMAIL VARCHAR(255))")
    }
    databaseConnectionFactory.connect().createStatement().use {
        it.execute("CREATE TABLE IF NOT EXISTS orders (ID VARCHAR(36) PRIMARY KEY)")
    }
    databaseConnectionFactory.connect().createStatement().use {
        it.execute("CREATE TABLE IF NOT EXISTS order_items  (ORDER_ID VARCHAR(36), ID VARCHAR(36) PRIMARY KEY, ITEM VARCHAR(255), AMOUNT INT, PRICE DOUBLE, FOREIGN KEY (ORDER_ID) REFERENCES orders (ID))")
    }

    customerStore = CustomerStoreSQL
    orderStore = OrderStoreSQL

}