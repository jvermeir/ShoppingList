package com.example.models

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.smithy.kotlin.runtime.http.Protocol
import aws.smithy.kotlin.runtime.http.Url
import aws.smithy.kotlin.runtime.util.net.Host
import com.example.Config
import kotlinx.coroutines.runBlocking

class Database {
    init {
        runBlocking {
            createDatabase()
        }
    }

    suspend fun createDatabase() {
        val client = DynamoDbClient {
            region = "eu-central-1"
            endpointUrl = Url(Protocol.HTTP, Host.parse("localhost"), 8000)
        }

        CreateTableRequest {
            tableName = Config.tableName
            attributeDefinitions = listOf(
                AttributeDefinition { attributeName = "PK"; attributeType = ScalarAttributeType.S },
                AttributeDefinition { attributeName = "SK"; attributeType = ScalarAttributeType.S },
                AttributeDefinition { attributeName = "GSI1PK"; attributeType = ScalarAttributeType.S },
                AttributeDefinition { attributeName = "GSI1SK"; attributeType = ScalarAttributeType.S },
            )
            keySchema = listOf(
                KeySchemaElement { attributeName = "PK"; keyType = KeyType.Hash },
                KeySchemaElement { attributeName = "SK"; keyType = KeyType.Range },
            )
            globalSecondaryIndexes = listOf(
                GlobalSecondaryIndex {
                    indexName = "GSI1"
                    keySchema = listOf(
                        KeySchemaElement { attributeName = "GSI1PK"; keyType = KeyType.Hash },
                        KeySchemaElement { attributeName = "GSI1SK"; keyType = KeyType.Range }
                    )
                    Projection { projectionType = ProjectionType.All }
                },
            )
        }
    }
}