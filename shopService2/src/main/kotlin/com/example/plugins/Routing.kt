package com.example.plugins

import com.example.models.CustomerStore
import com.example.routes.customerRouting
import com.example.routes.getOrderRoute
import com.example.routes.listOrdersRoute
import com.example.routes.totalizeOrderRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(customerStore: CustomerStore) {
    routing {
        customerRouting(customerStore)
        listOrdersRoute()
        getOrderRoute()
        totalizeOrderRoute()
    }
}