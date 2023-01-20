package com.example.models

import kotlinx.serialization.Serializable
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.Protocol
import aws.smithy.kotlin.runtime.http.Url
import aws.smithy.kotlin.runtime.util.net.Host
import com.example.Config
import kotlinx.coroutines.runBlocking

@Serializable
data class Category(val id: String, val name: String, val shopOrder: Int)

class CategoryRepository {

    private val theRegion = "eu-central-1"
    private val theEndpointUrl = Url(
        Protocol.HTTP, Host.parse("localhost"), 8000
    )

    init {
        runBlocking {
            createDatabase()
        }
    }

    // TODO: This doesn't work. without credentials access to a local dynamoDB works fine.
// Adding this causes queries to work without returning data.
    private val theCredentialsProvider = StaticCredentialsProvider(
        Credentials(
            "Dummy",
            "dummy"
        )
    )

    suspend fun createDatabase() {
        println("*************")
        println("Creating database: ${Config.tableName}")
        println("*************")

        val client = DynamoDbClient {
            region = theRegion
            endpointUrl = theEndpointUrl
        }

        suspend fun existsTable(): Boolean {
            DescribeTableRequest {
                tableName = Config.tableName
            }.let { request ->
                try {
                    client.describeTable(request)
                    return true
                } catch (e: ResourceNotFoundException) {
                    println("Table ${Config.tableName} does not exist yet")
                }
                return false
            }
        }

        if (existsTable()) {
            DeleteTableRequest {
                tableName = Config.tableName
            }.let {
                println("Deleting table: ${Config.tableName}")
                client.deleteTable(it)
                println("ok")
            }
        }

        CreateTableRequest {
            tableName = Config.tableName
            attributeDefinitions = listOf(
                AttributeDefinition {
                    attributeName = "PK"
                    attributeType = ScalarAttributeType.S
                },
                AttributeDefinition {
                    attributeName = "SK"
                    attributeType = ScalarAttributeType.S
                },
                AttributeDefinition {
                    attributeName = "GSI1PK"
                    attributeType = ScalarAttributeType.S
                },
                AttributeDefinition {
                    attributeName = "GSI1SK"
                    attributeType = ScalarAttributeType.S
                },
//                AttributeDefinition {
//                    attributeName = "GSI2PK"
//                    attributeType = ScalarAttributeType.S
//                },
//                AttributeDefinition {
//                    attributeName = "GSI2SK"
//                    attributeType = ScalarAttributeType.S
//                },
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
            globalSecondaryIndexes = listOf(
                GlobalSecondaryIndex {
                    indexName = "GSI1"
                    keySchema = listOf(
                        KeySchemaElement {
                            attributeName = "GSI1PK"
                            keyType = KeyType.Hash
                        },
                        KeySchemaElement {
                            attributeName = "GSI1SK"
                            keyType = KeyType.Range
                        }
                    )
                    Projection {
                        projectionType = ProjectionType.All
                    }
                },
//                GlobalSecondaryIndex {
//                    indexName = "GSI2"
//                    keySchema = listOf(
//                        KeySchemaElement {
//                            attributeName = "GSI2PK"
//                            keyType = KeyType.Hash
//                        },
//                        KeySchemaElement {
//                            attributeName = "GSI2SK"
//                            keyType = KeyType.Range
//                        }
//                    )
//                    Projection {
//                        projectionType = ProjectionType.All
//                    }
//                }
            )
            billingMode = BillingMode.PayPerRequest
        }.let {
            println("Creating table: ${Config.tableName}")
            client.createTable(it)
            println("ok")
        }
    }

    suspend fun listCategories(): List<Category> {
        val request = ScanRequest {
            tableName = Config.tableName
            print("tableName: ${Config.tableName}")
            filterExpression = "begins_with(#PK, :pk) and begins_with(#SK, :sk)"
            expressionAttributeNames = mapOf(
                "#PK" to "PK",
                "#SK" to "SK",
            )
            expressionAttributeValues = mapOf(
                ":pk" to AttributeValue.S("CATEGORY#"),
                ":sk" to AttributeValue.S("CATEGORY#"),
            )
        }

        DynamoDbClient {
            region = theRegion
            endpointUrl = theEndpointUrl
        }.use { ddb ->
            val response = ddb.scan(request)

            val categories = response.items?.map { item -> convertItemToCategory(item) }

            return categories.orEmpty()
        }
    }

    suspend fun findCategoryById(id: String): Category? {
        val request = GetItemRequest {
            tableName = Config.tableName
            key = mapOf(
                "PK" to AttributeValue.S("CATEGORY#$id"),
                "SK" to AttributeValue.S("CATEGORY#$id"),
            )
        }

        DynamoDbClient {
            region = theRegion
            endpointUrl = theEndpointUrl
        }.use { ddb ->
            val response = ddb.getItem(request)

            return response.item?.let { item -> convertItemToCategory(item) }
        }
    }

    suspend fun putItemInTable(
        category: Category
    ) {
        print("***************************************tableName: ${Config.tableName}")

        DynamoDbClient {
            region = theRegion
            endpointUrl = theEndpointUrl
        }.use { ddb ->
            ddb.putItem(putItemRequest(category))
            ddb.putItem(putSecondaryItemRequest(category))
        }
    }

    private fun putItemRequest(category: Category): PutItemRequest {
        val itemValues = mutableMapOf<String, AttributeValue>()

        itemValues["PK"] = AttributeValue.S("CATEGORY#$category.id")
        itemValues["SK"] = AttributeValue.S("CATEGORY#$category.id")
        itemValues["id"] = AttributeValue.S(category.id)
        itemValues["name"] = AttributeValue.S(category.name)
        itemValues["shopOrder"] = AttributeValue.S("$category.shopOrder")

        val request = PutItemRequest {
            tableName = Config.tableName
            item = itemValues
        }
        return request
    }

    private fun putSecondaryItemRequest(category: Category): PutItemRequest {
        val itemValues = mutableMapOf<String, AttributeValue>()

        itemValues["PK"] = AttributeValue.S("CATEGORYNAME#$category.name")
        itemValues["SK"] = AttributeValue.S("CATEGORYNAME#$category.name")
        itemValues["id"] = AttributeValue.S(category.id)
        itemValues["name"] = AttributeValue.S(category.name)

        val request = PutItemRequest {
            tableName = Config.tableName
            item = itemValues
        }
        return request
    }


    private fun convertItemToCategory(item: Map<String, AttributeValue>): Category {
        val id = item["id"]?.asS().orEmpty()
        val name = item["name"]?.asS().orEmpty()
        val shopOrder: Int = item["shopOrder"]?.asS()?.toIntOrNull() ?: 0
        return Category(id, name, shopOrder)
    }
}