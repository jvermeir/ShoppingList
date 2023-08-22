package com.example

import com.example.models.sql.Customer
import com.example.models.sql.Order
import com.example.models.sql.OrderItem
import com.example.models.Store
import com.example.routes.customerRouting
import com.example.routes.orderRoute
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

fun Application.module() {
    customerStore = TestCustomerStore
    orderStore = TestOrderStore

    routing {
        customerRouting(customerStore)
        orderRoute(orderStore)
    }

    install(ContentNegotiation) { json() }
}

object TestCustomerStore : Store<Customer, String> {
    private val customerStorage = mutableListOf<Customer>()
    private val testCustomer = Customer("test", "test", "test", "test")
    override suspend fun insert(value: Customer): Boolean = customerStorage.add(value)
    override suspend fun delete(id: String): Boolean = customerStorage.removeIf { it.id == id }
    override suspend fun update(value: Customer): Boolean = customerStorage.add(value)
    override suspend fun find(id: String): Customer = testCustomer
    override suspend fun findDetails(id: String): List<String> = emptyList()
    override suspend fun isNotEmpty(): Boolean = customerStorage.isNotEmpty()
    override suspend fun list(): List<Customer> = customerStorage
}

object TestOrderStore : Store<Order, OrderItem> {
    private val orderStorage = mutableListOf<Order>()
    private val testOrder = Order("order1", listOf(OrderItem( "1.1", "test",1, 1.1)))
    override suspend fun insert(value: Order): Boolean = orderStorage.add(value)
    override suspend fun delete(id: String): Boolean = orderStorage.removeIf { it.id == id }
    override suspend fun update(value: Order): Boolean = orderStorage.add(value)
    override suspend fun find(id: String): Order? = testOrder
    override suspend fun findDetails(id: String): List<OrderItem> = emptyList()
    override suspend fun isNotEmpty(): Boolean = orderStorage.isNotEmpty()
    override suspend fun list(): List<Order> = orderStorage
}

class OrderRouteTests {

    @Test
    fun testGetOrder() = testApplication {
        val response = client.get("/order/2020-04-06-01")
        assertEquals(
            """{"id":"order1","contents":[{"id":"1.1","item":"test","amount":1,"price":1.1}]}""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testOrderTotal() = testApplication {
        val response = client.get("/order/2020-04-06-01/total")
        assertEquals(
            """1.1""", response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

class CustomerRouteTests {

    @Test
    fun testGetCustomer() = testApplication {
        val response = client.get("/customer/test")
        assertEquals("""{"id":"test","firstName":"test","lastName":"test","email":"test"}""", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }
}