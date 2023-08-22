package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)

@Serializable
data class Order(val id: String, val contents: List<OrderItem>)

@Serializable
data class OrderItem(val id: String, val item: String, val amount: Int, val price: Double)
