package com.example.models.dynamoSingleTable

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.example.models.Order
import com.example.models.OrderItem
import com.example.models.Store

object OrderStoreDynamo : Store<Order, OrderItem> {
    val client = DynamoDbClient { region = "us-east-2" }
    val tableName = "order"

    override suspend fun insert(value: Order): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun find(id: String): Order? {
        TODO("Not yet implemented")
    }

    override suspend fun findDetails(id: String): List<OrderItem> {
        TODO("Not yet implemented")
    }

    override suspend fun isNotEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun list(): List<Order> {
        TODO("Not yet implemented")
    }

    override suspend fun update(value: Order): Boolean {
        TODO("Not yet implemented")
    }
}