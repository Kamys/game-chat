package com.example.server

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.security.core.AuthenticatedPrincipal

@Document(collection = "user")
data class User(
    @Id
    val id: String? = null,
    val username: String,
    val password: String,
    val sessionId: String? = null,
): AuthenticatedPrincipal { //TODO: delete AuthenticatedPrincipal
    override fun getName(): String {
        return username
    }
}

@Document(collection = "channel")
data class Channel(
    @Id
    val id: String? = null,
    val name: String,
    var members: Set<User>
) {
    fun isMemberOrError(username: String) {
        members.find { it.username == username }
            ?: throw AccessDeniedException("You are not a member of this channel.")
    }
}

interface UserRepository : MongoRepository<User, String> {
    fun findById(id: ObjectId): User?
    fun findByUsername(name: String): User?
    fun findBySessionId(sessionId: String): User?
    fun existsByUsername(name: String): Boolean
}

interface ChannelRepository : MongoRepository<Channel, String> {
    fun findById(id: ObjectId): Channel?
    fun findByMembersUsername(username: String): List<Channel>
}