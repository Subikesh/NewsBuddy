package com.spacey.newsbuddy.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

expect fun log(tag: String, message: String)

inline fun <T, R> Result<T>.safeConvert(block: (T) -> Result<R>): Result<R> {
    return try {
        if (this.isSuccess) {
            return block(this.getOrThrow())
        } else {
            Result.failure(this.exceptionOrNull()!!)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }
}

suspend fun Flow<String?>.foldAsString(): String {
    return this.fold("") { curr, next -> curr + next }
}

expect fun getCurrentTime(): Long

expect fun Result<*>.isAiServerException(): Boolean

object NoInternetException : Exception()
object AiFeaturesDisabled : Exception()