package com.example.models

interface Store<T, D> {
    suspend fun insert(value: T): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun update(value: T): Boolean
    suspend fun find(id: String): T?
    suspend fun findDetails(id: String): List<D>
    suspend fun isNotEmpty(): Boolean
    suspend fun list(): List<T>
}
