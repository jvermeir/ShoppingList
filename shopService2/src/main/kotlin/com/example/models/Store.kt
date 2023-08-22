package com.example.models

interface Store<T, D> {
    fun insert(value: T): Boolean
    fun delete(id: String): Boolean
    fun update(value: T): Boolean
    fun find(id: String): T?
    fun findDetails(id: String): List<D>
    fun isNotEmpty(): Boolean
    fun list(): List<T>
}
