package com.example.models

import com.example.databaseConnectionFactory
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.sql.ResultSet


@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)

interface CustomerStore {
    fun insert(customer: Customer)

    fun delete(id: String): Boolean

    fun update(customer: Customer): Boolean

    fun find(id: String): Customer?

    fun isNotEmpty(): Boolean

    fun list(): List<Customer>
}

object CustomerStoreSQL : CustomerStore {
    private val logger = KotlinLogging.logger {}

    private val connection = databaseConnectionFactory.connect()
    override fun insert(customer: Customer) {
        logger.info { "insert customer: $customer" }
        val result = connection.createStatement()
            .execute("insert into customer (ID, FIRSTNAME, LASTNAME, EMAIL) values ('${customer.id}', '${customer.firstName}', '${customer.lastName}', '${customer.email}')")
        logger.info { "insert customer withi id ${customer.id}: $result" }
    }

    override fun delete(id: String): Boolean {
        logger.info { "delete customer with id: $id" }
        val result = connection.createStatement()
            .execute("delete from customer where id = '$id'")
        logger.info { "delete: $result" }
        return result
    }

    override fun update(customer: Customer): Boolean {
        logger.info { "update customer: $customer" }
        val result = connection.createStatement()
            .execute("update customer set FIRSTNAME = '${customer.firstName}', LASTNAME = '${customer.lastName}', EMAIL = '${customer.email}' where id = '${customer.id}'")
        logger.info { "update customer $customer.id: $result" }
        return result
    }

    override fun find(id: String): Customer? {
        logger.info { "find customer with id: $id" }
        val result: ResultSet = connection.createStatement().executeQuery("select * from customer where id = '$id'")
        val customer = result.takeIf { it.next() }?.let {
            Customer(
                it.getString("id"),
                it.getString("firstName"),
                it.getString("lastName"),
                it.getString("email")
            )
        }
        logger.info { "find customer with id: $customer" }
        return customer
    }

    override fun isNotEmpty(): Boolean = false

    override fun list(): List<Customer> {
        logger.info { "list customers" }
        val result = connection.createStatement().executeQuery("select * from customer")
        val list = mutableListOf<Customer>()
        while (result.next()) {
            list.add(
                Customer(
                    result.getString("id"),
                    result.getString("firstName"),
                    result.getString("lastName"),
                    result.getString("email")
                )
            )
        }
        logger.info { "found ${list.size} customers, first: ${list.first()}" }
        return list
    }
}
