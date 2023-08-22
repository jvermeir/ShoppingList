package com.example.models.sql

import com.example.databaseConnectionFactory
import com.example.models.Order
import com.example.models.OrderItem
import com.example.models.Store
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import mu.KotlinLogging

object OrderStoreSQL : Store<Order, OrderItem> {
    private val logger = KotlinLogging.logger {}

    private val connection = databaseConnectionFactory.connect()
    override suspend fun insert(order: Order): Boolean {
        logger.info { "insert order: $order" }
        val orderStatement = connection.prepareStatement("insert into orders (ID) values (?)")
        orderStatement.setString(1, order.id)
        orderStatement.executeUpdate()
        order.contents.forEach {
            val orderItemsStatement =
                connection.prepareStatement("insert into order_items (ORDER_ID, ID, ITEM, AMOUNT, PRICE) values (?,?,?,?,?)")
            orderItemsStatement.setString(1, order.id)
            orderItemsStatement.setString(2, it.id)
            orderItemsStatement.setString(3, it.item)
            orderItemsStatement.setInt(4, it.amount)
            orderItemsStatement.setDouble(5, it.price)
            orderItemsStatement.executeUpdate()
        }
        return true
    }

    override suspend fun delete(id: String): Boolean {
        logger.info { "delete order with id: $id" }
        val deleteItemsStatement = connection.prepareStatement("delete from order_items where order_id = ?")
        deleteItemsStatement.setString(1, id)
        deleteItemsStatement.executeUpdate()

        val deleteOrderStatement = connection.prepareStatement("delete from orders where id = ?")
        deleteOrderStatement.setString(1, id)
        val result = deleteOrderStatement.executeUpdate()
        logger.info { "delete: $result" }
        return result == 1
    }

    override suspend fun update(order: Order): Boolean {
        logger.info { "update order: $order" }
        // Lazy delete/insert instead of update
        delete(order.id)
        runBlocking {insert(order)}
        // TODO: fix
        return true
    }

    override suspend fun find(id: String): Order? {
        logger.info { "find order with id: $id" }
        val orderItems = findDetails(id)
        val orderStatement =
            connection.prepareStatement("select * from orders where id = ?")
        orderStatement.setString(1, id)
        val result = orderStatement.executeQuery()
        val order = result.takeIf { it.next() }?.let {
            Order(it.getString("id"), orderItems ?: listOf())
        }
        logger.info { "find order by id: $order" }
        return order
    }

    override suspend fun isNotEmpty(): Boolean = false

    override suspend fun list(): List<Order> {
        logger.info { "list orders" }
        val result = connection.createStatement().executeQuery("select * from orders")
        val list = mutableListOf<Order>()
        while (result.next()) {
            list.add(Order(result.getString("id"), listOf()))
        }
        logger.info { "found ${list.size} orders, first: ${list.first()}" }
        return list
    }

    override suspend fun findDetails(orderId: String): List<OrderItem> {
        val orderItemsStatement =
            connection.prepareStatement("select * from order_items where order_id = ?")
        orderItemsStatement.setString(1, orderId)
        val orderItemsResult = orderItemsStatement.executeQuery()
        val orderItems = mutableListOf<OrderItem>()
        orderItemsResult.takeIf { it.next() }?.let {
            orderItems.add(
                OrderItem(
                    it.getString("id"),
                    it.getString("item"),
                    it.getInt("amount"),
                    it.getDouble("price")
                )
            )
        }
        return orderItems
    }
}
