package com.hussein.socialmedia.domain.util

/**
 * Sealed class representing the result state of an operation.
 * Used throughout the app for consistent state management.
 */
sealed class Resource<out T> {
    data object Idle : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

    data class Success<T>(val data: T) : Resource<T>()

    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<Nothing>()
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    fun getOrNull(): T? = if (this is Success) data else null
}