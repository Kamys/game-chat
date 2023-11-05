package com.example.server

open class BaseException(final override val message: String) : RuntimeException(message)
open class NotFoundException(message: String) : BaseException(message)
open class ConflictException(message: String) : BaseException(message)
open class AccessDeniedException(message: String) : BaseException(message)