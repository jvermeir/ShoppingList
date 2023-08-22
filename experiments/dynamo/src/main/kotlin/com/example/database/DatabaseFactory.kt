package com.example.database

import com.example.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

interface DatabaseFactory {
    fun connect(): Connection
    fun close()
}

class DatabaseConnectionFactoryImpl(dbConfig: DatabaseConfig) : DatabaseFactory {

    private val config = HikariConfig()
    private val hikariDataSource: HikariDataSource

    init {
        config.driverClassName = dbConfig.driverClass
        config.jdbcUrl = dbConfig.url
        config.username = dbConfig.user
        config.password = dbConfig.password
        config.maximumPoolSize = 10
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        config.validate()
        hikariDataSource = HikariDataSource(config)
    }

    override fun close() {
        // not necessary
    }

    override fun connect(): Connection {
        return hikariDataSource.connection
    }
}

class DummyDatabaseConnectionFactoryImpl(dbConfig: DatabaseConfig) : DatabaseFactory {
    override fun close() {
        // not necessary
    }

    override fun connect(): Connection {
        // TODO: fix
        return null as Connection
    }
}