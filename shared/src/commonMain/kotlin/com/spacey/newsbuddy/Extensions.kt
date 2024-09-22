package com.spacey.newsbuddy

expect fun log(tag: String, message: String)

inline fun <T, R> Result<T>.convert(block: (T) -> Result<R>): Result<R> {
    return if (this.isSuccess) {
        return block(this.getOrThrow())
    } else {
        Result.failure(this.exceptionOrNull()!!)
    }
}