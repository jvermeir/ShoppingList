package com.example

import com.example.models.Customer
import com.example.models.CustomerStore
import com.example.routes.customerRouting
import com.example.routes.getOrderRoute
import com.example.routes.listOrdersRoute
import com.example.routes.totalizeOrderRoute
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

object testCustomerStore : CustomerStore {
    private val customerStorage = mutableListOf<Customer>()
    private val testCustomer = Customer("test", "test", "test", "test")
    override fun insert(customer: Customer) {
        println("v2")
        customerStorage.add(customer)
    }

    override fun delete(id: String): Boolean {
        println("v2")
        return customerStorage.removeIf { it.id == id }
    }

    override fun update(customer: Customer): Boolean {
        println("v2")
        customerStorage.add(customer)
        return true
    }

    override fun find(id: String): Customer? {
        return testCustomer
    }

    override fun isNotEmpty(): Boolean {
        println("v2")
        return customerStorage.isNotEmpty()
    }

    override fun list(): List<Customer> {
        println("v2")
        return customerStorage
    }
}

class OrderRouteTests {

    @Test
    fun testGetOrder() = testApplication {
        routing {
            customerRouting(testCustomerStore)
            listOrdersRoute()
            getOrderRoute()
            totalizeOrderRoute()
        }

        install(ContentNegotiation) {
            json()
        }

        val response = client.get("/order/2020-04-06-01")
        assertEquals(
            """{"number":"2020-04-06-01","contents":[{"item":"Ham Sandwich","amount":2,"price":5.5},{"item":"Water","amount":1,"price":1.5},{"item":"Beer","amount":3,"price":2.3},{"item":"Cheesecake","amount":1,"price":3.75}]}""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testOrderTotal() = testApplication {
        routing {
            customerRouting(testCustomerStore)
            listOrdersRoute()
            getOrderRoute()
            totalizeOrderRoute()
        }

        install(ContentNegotiation) {
            json()
        }
        val response = client.get("/order/2020-04-06-01/total")
        assertEquals(
            """23.15""", response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}

class CustomerRouteTests {

    @Test
    fun testGetCustomer() = testApplication {
        routing {
            customerRouting(testCustomerStore)
            listOrdersRoute()
            getOrderRoute()
            totalizeOrderRoute()
        }
        install(ContentNegotiation) {
            json()
        }

        val response = client.get("/customer/test")
        assertEquals("""{"id":"test","firstName":"test","lastName":"test","email":"test"}""", response.bodyAsText())
        assertEquals(HttpStatusCode.OK, response.status)
    }

}