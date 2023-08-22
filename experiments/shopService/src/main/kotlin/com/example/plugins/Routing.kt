package com.example.plugins

import com.example.customerStore
import com.example.orderStore
import com.example.routes.customerRouting
import com.example.routes.orderRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        customerRouting(customerStore)
        orderRoute(orderStore)
    }
}