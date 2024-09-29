package com.spacey.newsbuddy

expect fun log(tag: String, message: String)

inline fun <T, R> Result<T>.convertCatching(block: (T) -> Result<R>): Result<R> {
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

const val GEMINI_1_5_PRO = "gemini-1.5-pro"