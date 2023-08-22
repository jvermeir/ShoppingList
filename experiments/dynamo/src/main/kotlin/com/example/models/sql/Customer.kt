package com.example.models.sql

import com.example.databaseConnectionFactory
import com.example.models.Customer
import com.example.models.Store
import kotlinx.serialization.Serializable
import mu.KotlinLogging

object CustomerStoreSQL : Store<Customer, String> {
    private val logger = KotlinLogging.logger {}

    private val connection = databaseConnectionFactory.connect()
    override suspend fun insert(value: Customer): Boolean {
        logger.info { "insert customer: $value" }
        val insertCustomerStatement = connection.prepareStatement(
            "insert into customers (ID, FIRSTNAME, LASTNAME, EMAIL) values (?, ?, ?, ?)")
        insertCustomerStatement.setString(1, value.id)
        insertCustomerStatement.setString(2, value.firstName)
        insertCustomerStatement.setString(3, value.lastName)
        insertCustomerStatement.setString(4, value.email)
        val result = insertCustomerStatement.executeUpdate()
        logger.info { "insert: $result" }
        return result == 1
    }

    override suspend fun delete(id: String): Boolean {
        logger.info { "delete customer with id: $id" }
        val deleteCustomerStatement = connection.prepareStatement("delete from customers where id = ?")
        deleteCustomerStatement.setString(1, id)
        val result = deleteCustomerStatement.executeUpdate()
        logger.info { "delete: $result" }
        return result == 1
    }

    override suspend fun update(value: Customer): Boolean {
        logger.info { "update customer: $value" }
        val updateCustomerStatement = connection.prepareStatement(
            "update customers set FIRSTNAME = ?, LASTNAME = ?, EMAIL = ? where ID = ?"
        )
        updateCustomerStatement.setString(1, value.firstName)
        updateCustomerStatement.setString(2, value.lastName)
        updateCustomerStatement.setString(3, value.email)
        updateCustomerStatement.setString(4, value.id)
        val result = updateCustomerStatement.executeUpdate()
        logger.info { "update: $result" }
        return result == 1
    }

    override suspend fun find(id: String): Customer? {
        logger.info { "find customer with id: $id" }
        val findCustomerStatement = connection.prepareStatement("select * from customers where id = ?")
        findCustomerStatement.setString(1, id)
        val result = findCustomerStatement.executeQuery()
        val customer = result.takeIf { it.next() }?.let {
            Customer(
                it.getString("id"),
                it.getString("firstName"),
                it.getString("lastName"),
                it.getString("email")
            )
        }
        logger.info { "found customer with id: $customer" }
        return customer
    }

    override suspend fun isNotEmpty(): Boolean = false

    override suspend fun list(): List<Customer> {
        logger.info { "list customers" }
        val customers = connection.createStatement().executeQuery("select * from customers")
        val list = mutableListOf<Customer>()
        while (customers.next()) {
            list.add(
                Customer(
                    customers.getString("id"),
                    customers.getString("firstName"),
                    customers.getString("lastName"),
                    customers.getString("email")
                )
            )
        }
        logger.info { "found ${list.size} customers, first: ${list.first()}" }
        return list
    }

    override suspend fun findDetails(id: String): List<String> {
        return emptyList()
    }
}
