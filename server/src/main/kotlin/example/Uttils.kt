package example

import org.springframework.data.repository.CrudRepository

fun <T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T =
    findById(id).orElseThrow { NoSuchElementException("No entity found for ID: $id") }