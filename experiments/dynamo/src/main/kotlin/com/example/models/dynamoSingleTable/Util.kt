package com.example.models.dynamoSingleTable

import aws.sdk.kotlin.runtime.AwsServiceException
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import kotlinx.coroutines.delay


suspend fun createTable(client: DynamoDbClient, name: String) {
    val tableExists = client.listTables(ListTablesRequest {}).tableNames?.contains(name) ?: false
    if (tableExists) return

    val req = CreateTableRequest {
        tableName = name
        attributeDefinitions = listOf(
            AttributeDefinition {
                attributeName = "PK"
                attributeType = ScalarAttributeType.S
            },
            AttributeDefinition {
                attributeName = "SK"
                attributeType = ScalarAttributeType.S
            },
        )
        keySchema = listOf(
            KeySchemaElement {
                attributeName = "PK"
                keyType = KeyType.Hash
            },
            KeySchemaElement {
                attributeName = "SK"
                keyType = KeyType.Range
            },
        )

        provisionedThroughput {
            readCapacityUnits = 10
            writeCapacityUnits = 10
        }
    }

    val resp = client.createTable(req)
    println("created table: ${resp.tableDescription?.tableArn}")
}

suspend fun createCustomerTable(client: DynamoDbClient, name: String) {
    val tableExists = client.listTables(ListTablesRequest {}).tableNames?.contains(name) ?: false
    if (tableExists) return

    val req = CreateTableRequest {
        tableName = name
        keySchema = listOf(
            KeySchemaElement {
                attributeName = "id"
                keyType = KeyType.Hash
            },
        )

        attributeDefinitions = listOf(
            AttributeDefinition {
                attributeName = "id"
                attributeType = ScalarAttributeType.S
            },
        )
        provisionedThroughput {
            readCapacityUnits = 10
            writeCapacityUnits = 10
        }
    }

    val resp = client.createTable(req)
    println("created table: ${resp.tableDescription?.tableArn}")
}

suspend fun DynamoDbClient.waitForTableReady(name: String) {
    while (true) {
        try {
            val req = DescribeTableRequest { tableName = name }
            if (describeTable(req).table?.tableStatus != TableStatus.Creating) {
                println("table ready")
                return
            }
        } catch (ex: AwsServiceException) {
            if (!ex.sdkErrorMetadata.isRetryable) throw ex
        }
        println("waiting for table to be ready...")
        delay(1000)
    }
}
