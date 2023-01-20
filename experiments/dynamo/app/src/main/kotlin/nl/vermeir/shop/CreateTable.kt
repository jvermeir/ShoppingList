package nl.vermeir.shop

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableNotExists
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.Protocol
import aws.smithy.kotlin.runtime.http.Url
import aws.smithy.kotlin.runtime.util.net.Host

suspend fun main(args: Array<String>) {
//        dropTable("shop", ddb)
    dynamoDbClient.use { ddb ->
//        createTable("shop", "key", ddb)
//        describeTable("shop", ddb)
//        scanTable("shop", ddb)
//        listAllTables(ddb)
    }
    scanShop()
}

suspend fun listAllTables(ddb: DynamoDbClient) {
    println("listAllTables")
    val response = ddb.listTables(ListTablesRequest {})
    response.tableNames?.forEach { tableName ->
        println("Table name is $tableName")
    }
    println("listAllTables - done")
}

suspend fun scanShop() {

    println("scan shop")
    val request = ScanRequest {
        tableName = "shop"
    }

    DynamoDbClient {
        region = "eu-central-1"
        endpointUrl = Url(
            Protocol.HTTP, Host.parse("localhost"), 8000
        )
    }.use { ddb ->
        val response = ddb.scan(request)
        response.items?.forEach { item ->
            println("Item is $item")
        }
    }
    println("scan shop - done")
}

suspend fun scanTable(tableNameVal: String, ddb: DynamoDbClient) {
    println("scanTable: $tableNameVal")
    val request = ScanRequest {
        tableName = tableNameVal
    }


    val response = ddb.scan(request)
    response.items?.forEach { item ->
        item.keys.forEach { key ->
            println("The key name is $key\n")
            println("The value is ${item[key]}")
        }
    }
    println("scanTable - done")
}


private const val theRegion = "eu-central-1"
private val dynamoDbClient = DynamoDbClient {
    region = theRegion
    endpointUrl = Url(
        Protocol.HTTP, Host.parse("localhost"), 8000
    )
    credentialsProvider = StaticCredentialsProvider(
        Credentials(
            "Dummy",
            "dummy"
        )
    )
}

suspend fun existsTable(tableNameParam: String, ddb: DynamoDbClient): Boolean {
    println("exists $tableNameParam ?")
    val describeRequest = DescribeTableRequest { tableName = tableNameParam }
    try {
        ddb.describeTable(describeRequest)
        return true
    } catch (e: ResourceNotFoundException) {
        return false
    }
}

suspend fun describeTable(tableNameParam: String, ddb: DynamoDbClient) {
    println("describe $tableNameParam")
    val describeRequest = DescribeTableRequest { tableName = tableNameParam }
    try {
        val response = ddb.describeTable(describeRequest)
        println(response)
    } catch (e: ResourceNotFoundException) {
        println(e)
        println("$tableNameParam not found")
    }
}

suspend fun dropTable(tableNameParam: String, ddb: DynamoDbClient) {
    println("delete $tableNameParam")
    if (existsTable(tableNameParam, ddb)) {
        val deleteRequest = DeleteTableRequest { tableName = tableNameParam }

        ddb.deleteTable(deleteRequest)
        ddb.waitUntilTableNotExists { // suspend call
            tableName = tableNameParam
        }
    }
}

suspend fun createTable(tableNameVal: String, key: String, ddb: DynamoDbClient): String? {
    println("create $tableNameVal")
    val attDef = AttributeDefinition {
        attributeName = key
        attributeType = ScalarAttributeType.S
    }

    val keySchemaVal = KeySchemaElement {
        attributeName = key
        keyType = KeyType.Hash
    }

    val provisionedVal = ProvisionedThroughput {
        readCapacityUnits = 10
        writeCapacityUnits = 10
    }

    val request = CreateTableRequest {
        attributeDefinitions = listOf(attDef)
        keySchema = listOf(keySchemaVal)
        provisionedThroughput = provisionedVal
        tableName = tableNameVal
    }

    val response = ddb.createTable(request)
    ddb.waitUntilTableExists { // suspend call
        tableName = tableNameVal
    }
    val tableArn = response.tableDescription!!.tableArn.toString()
    println("Table $tableArn is ready")
    return tableArn
}
