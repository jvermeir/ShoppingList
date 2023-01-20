package com.example

import com.example.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {



    @Test
    fun testRoot() = testApplication {
        Config.tableName = "myshop"
        application {
            configureRouting()
        }

        val x = this
        val client = createClient {
            x.install(ContentNegotiation) {
                json()
            }
        }

//        client.get("/category").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("""[{"id":"100","name":"vegetables","shopOrder":0},{"id":"1","name":"Category 1","shopOrder":0}]""", bodyAsText())
//        }

        val response = client.post("/category") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(""" { "id": "1", "name": "Category 1", "shopOrder": 1 } """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, response.status)
//
//        client.get("/category").apply {
//            assertEquals(HttpStatusCode.OK, status)
//            assertEquals("""[{"id":"100","name":"vegetables","shopOrder":0},{"id":"1","name":"Category 1","shopOrder":0}]""", bodyAsText())
//        }
    }
}