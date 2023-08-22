package com.example.models.dynamo

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import com.example.models.Customer
import com.example.models.Store

fun customerAsAttributeValue(customer: Customer): Map<String, AttributeValue.S> {
    return mapOf(
        "id" to AttributeValue.S(customer.id),
        "firstName" to AttributeValue.S(customer.firstName),
        "lastName" to AttributeValue.S(customer.lastName),
        "email" to AttributeValue.S(customer.email),
    )
}

suspend fun DynamoDbClient.saveCustomer(name: String, customer: Customer): Customer {
    val req = PutItemRequest {
        tableName = name
        item = customerAsAttributeValue(customer)
    }
    return customerFromAttributeValues(putItem(req).attributes ?: mapOf())
}

fun customerFromAttributeValues(item: Map<String, AttributeValue>): Customer {
    return Customer(
        item["id"]?.asS() ?: "",
        item["firstName"]?.asS() ?: "",
        item["lastName"]?.asS() ?: "",
        item["email"]?.asS() ?: "",
    )
}

suspend fun DynamoDbClient.findCustomerById(name: String, id: String): Customer {
    val req = QueryRequest {
        tableName = name
        keyConditionExpression = "#id = :id"
        expressionAttributeNames = mapOf(
            "#id" to "id",
        )
        expressionAttributeValues = mapOf(
            ":id" to AttributeValue.S(id),
        )
    }
    val data = query(req)
    return customerFromAttributeValues(data.items?.get(0) ?: emptyMap())
}

suspend fun DynamoDbClient.delete(name: String, id: String): Customer {
    val keyToDelete = mapOf("id" to AttributeValue.S(id))
    val req = DeleteItemRequest {
        tableName = name
        key = keyToDelete
        returnValues = ReturnValue.AllOld

    }
    val data = deleteItem(req)
    return customerFromAttributeValues(data.attributes ?: mapOf())
}

suspend fun DynamoDbClient.update(name: String, customer: Customer): Customer {
    val keyToUpdate = mapOf("id" to AttributeValue.S(customer.id))
    val values = customerAsAttributeValue(customer)
    val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
    values.forEach {
        updatedValues[it.key] = AttributeValueUpdate {
            value = it.value
            action = AttributeAction.Put
        }
    }
    val request = UpdateItemRequest {
        tableName = name
        key = keyToUpdate
        attributeUpdates = updatedValues
        returnValues = ReturnValue.AllNew
    }
    val data = updateItem(request)
    return customerFromAttributeValues(data.attributes ?: mapOf())
}

suspend fun DynamoDbClient.listCustomers(name: String): List<Customer> {
    val request = ScanRequest {
        tableName = name
    }
    return scan(request).items?.map { customerFromAttributeValues(it) } ?: emptyList()
}

object CustomerStoreDynamo : Store<Customer, String> {
    private val client = DynamoDbClient { region = "us-east-2" }
    private const val TABLE_NAME = "customer"

    override suspend fun insert(value: Customer): Boolean {
        val insertedCustomer = client.saveCustomer(TABLE_NAME, value)
        // TODO: find a better solution
        return insertedCustomer.id != ""
    }

    override suspend fun delete(id: String): Boolean {
        val deletedCustomer = client.delete(TABLE_NAME, id)
        // TODO: find a better solution
        return deletedCustomer.id != ""
    }

    override suspend fun find(id: String): Customer? {
        return client.findCustomerById(TABLE_NAME, id)
    }

    override suspend fun findDetails(id: String): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun isNotEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun list(): List<Customer> {
        return client.listCustomers(TABLE_NAME)
    }

    override suspend fun update(value: Customer): Boolean {
        val updateCustomer = client.update(TABLE_NAME, value)
        // TODO: find a better solution
        return updateCustomer.id != ""
    }
}