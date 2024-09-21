package com.spacey.newsbuddy.base

import com.spacey.newsbuddy.Dependencies
import com.spacey.newsbuddy.log
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json

expect fun getHttpClientEngine(): HttpClientEngine

abstract class BaseApiService(private val dependencies: Dependencies) {
    private val httpClient = HttpClient(getHttpClientEngine()) {
        install(Logging) {
            this.logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    log("HttpLog", message)
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getApiCall(url: String, builder: HttpRequestBuilder.() -> Unit): HttpResponse = httpClient.get(url) {
        builder()
        bearerAuth(dependencies.getNewsApiToken())
    }
}
