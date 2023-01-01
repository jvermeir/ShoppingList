package nl.vermeir.shop

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.http.Protocol
import aws.smithy.kotlin.runtime.http.Url
import aws.smithy.kotlin.runtime.util.net.Host

private const val theRegion = "eu-central-1"

suspend fun main(args: Array<String>) {
    listAllTables()
}

suspend fun listAllTables() {

    println("Tables: ")
    DynamoDbClient {
        region = theRegion
        endpointUrl = Url(
            Protocol.HTTP, Host.parse("localhost"), 8000
        )
        credentialsProvider = StaticCredentialsProvider(Credentials(
            "Dummy",
            "dummy"
        ))
    }.use { ddb ->
        val response = ddb.listTables(ListTablesRequest {})
        response.tableNames?.forEach { tableName ->
            println(tableName)
        }
    }
}
