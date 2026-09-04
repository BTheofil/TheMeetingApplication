package hu.tb.network.repository

import hu.tb.data.search.CoachResult
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import hu.tb.search.domain.Coach
import hu.tb.search.domain.SearchResult
import hu.tb.search.domain.Status
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class SearchRepository(private val httpClient: HttpClient) {

    suspend fun searchCoach(name: String): SearchResult {
        val result = safeCall {
            httpClient.post("/searchCoach") {
                setBody(mapOf("name" to name))
            }
        }
        return when (result) {
            is ApiResult.Fail -> SearchResult(
                coaches = emptyList(),
                errorMessage = result.dataError.asText()
            )

            is ApiResult.Ok -> result.httpResponse.toSearchResult()
        }
    }

    private suspend fun HttpResponse.toSearchResult(): SearchResult =
        when (status.value) {
            in 200..299 -> try {
                SearchResult(coaches = body<List<CoachResult>>().map { it.toDomain() })
            } catch (e: Exception) {
                e.printStackTrace()
                SearchResult(
                    coaches = emptyList(),
                    errorMessage = DataError.UNKNOWN.asText()
                )
            }

            401 -> SearchResult(
                coaches = emptyList(),
                errorMessage = DataError.UNAUTHORIZED.asText()
            )

            else -> SearchResult(
                coaches = emptyList(),
                errorMessage = DataError.UNKNOWN.asText()
            )
        }

    private fun CoachResult.toDomain(): Coach =
        Coach(
            id = coachId,
            name = coachName,
            status = when (status.trim().lowercase()) {
                "pending" -> Status.PENDING
                "added" -> Status.ADDED
                else -> Status.INIT
            }
        )
}
