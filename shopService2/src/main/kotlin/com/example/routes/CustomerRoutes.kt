package com.example.routes

import com.example.models.Customer
import com.example.models.CustomerStore
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Route.customerRouting(customerStorage: CustomerStore) {
    route("/customer") {
        get {
            call.respond(customerStorage.list())
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val customer =
                customerStorage.find(id) ?: return@get call.respondText(
                    "No customer with id $id",
                    status = HttpStatusCode.NotFound
                )
            return@get call.respond(customer)
        }
        post {
            val customer = call.receive<Customer>()
            customerStorage.insert(customer)
            println("customer: $customer stored")
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }
        put {
            val customer = call.receive<Customer>()
            customerStorage.update(customer)
            println("customer: $customer stored")
            call.respondText("Customer updated correctly", status = HttpStatusCode.OK)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.delete(id)) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}