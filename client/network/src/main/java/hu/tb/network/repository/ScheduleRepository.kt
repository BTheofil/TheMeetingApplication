package hu.tb.network.repository

import io.ktor.client.HttpClient

class ScheduleRepository(
    private val httpClient: HttpClient
) {
    suspend fun getMonth() {}
}