package com.example.models.dynamo

import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.example.models.Customer
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val client = DynamoDbClient { region = "us-east-2" }

    val tableName = "customer"

    try {
        createCustomerTable(client, tableName)
        client.waitForTableReady(tableName)
        println("save")
        val c1 = Customer("1", "A", "B", "a.b@vermeir.nl")
        client.saveCustomer(tableName, c1)
        println("load")
        val c2 = client.findCustomerById(tableName, "1")
        println("c2: ${c2}")
    } catch (ex: AwsServiceException) {
        println(ex)
    }

    client.close()
}
