package com.example.routes

import com.example.models.*
import com.example.models.sql.Order
import com.example.models.sql.OrderItem
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRoute(orderStorage: Store<Order, OrderItem>) {
    route("/order") {
        get {
            if (orderStorage.isNotEmpty()) {
                call.respond(orderStorage.list())
            }
        }
        get("{id?}") {
            println("getOrderRoute()")

            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad Request",
                status = HttpStatusCode.BadRequest
            )
            println("id: $id")
            val order = orderStorage.find(id) ?: return@get call.respondText(
                "Not Found",
                status = HttpStatusCode.NotFound
            )
            println("order: $order")
            call.respond(order)
        }
        get("{id?}/total") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Bad Request",
                status = HttpStatusCode.BadRequest
            )
            val order = orderStorage.find(id) ?: return@get call.respondText(
                "Not Found",
                status = HttpStatusCode.NotFound
            )
            val total = order.contents.sumOf { it.price * it.amount }
            call.respond(total)
        }
        post {
            val order = call.receive<Order>()
            orderStorage.insert(order)
            println("order: $order stored")
            call.respondText("Order stored correctly", status = HttpStatusCode.Created)
        }
        put {
            val order = call.receive<Order>()
            orderStorage.update(order)
            println("order: $order stored")
            call.respondText("Order updated correctly", status = HttpStatusCode.OK)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (orderStorage.delete(id)) {
                call.respondText("Order removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }

    }
}