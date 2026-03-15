package com.nikealarm.core.common

sealed class Result<out R> {
    data class ResultSuccess<out T>(val data: T) : Result<T>()
    data class ResultError(val exception: Exception) : Result<Nothing>()
}

inline fun <T, R> T.runCatching(transform: (T) -> R): Result<R> {
    return try {
        Result.ResultSuccess(transform(this))
    } catch (e: Throwable) {
        Result.ResultError(e as Exception)
    }
}

fun <T> Result<T>.getOrThrow(): T {
    return if (this is Result.ResultSuccess) data else throw (this as Result.ResultError).exception
}

suspend inline fun <T, R> Result<T>.flatMap(
    crossinline transform: suspend (T) -> Result<R>
): Result<R> {
    return when (this) {
        is Result.ResultSuccess -> transform(this.data)
        is Result.ResultError -> this
    }
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.ResultSuccess) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (Exception) -> Unit): Result<T> {
    if (this is Result.ResultError) action(exception)
    return this
}