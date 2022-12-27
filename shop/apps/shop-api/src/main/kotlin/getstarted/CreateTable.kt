package example.aws.getstarted

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
    dynamoDbClient.use { ddb ->
        describeTable("shop", ddb)
        dropTable("shop", ddb)
        createTable("shop", "key", ddb)
    }
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
        println("$tableNameParam exists")
        println(response)
    } catch (e: ResourceNotFoundException) {
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
