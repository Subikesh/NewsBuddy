package com.spacey.newsbuddy

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
        Result.failure(e)
    }
}

suspend fun Flow<String?>.foldAsString(): String {
    return this.fold("") { curr, next -> curr + next }
}

const val GEMINI_1_5_PRO = "gemini-1.5-pro"