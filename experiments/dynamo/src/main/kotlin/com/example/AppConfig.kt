package com.example

import com.example.database.DatabaseFactory
import com.example.models.sql.Customer
import com.example.models.sql.Order
import com.example.models.sql.OrderItem
import com.example.models.Store

data class DatabaseConfig(
    val driverClass: String,
    val url: String,
    val user: String,
    val password: String
)

lateinit var customerStore: Store<Customer, String>
lateinit var orderStore: Store<Order, OrderItem>
lateinit var databaseConnectionFactory: DatabaseFactory