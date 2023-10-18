package com.example

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository

@Document(collection = "user")
data class User(
    @Id val id: String? = null,
    val username: String,
    val password: String,
    val sessionId: String? = null
)

interface UserRepository : MongoRepository<User, String> {
    fun findById(id: ObjectId): User?
    fun findByUsername(name: String): User?
    fun findBySessionId(sessionId: String): User?
    fun existsByUsername(name: String): Boolean
}