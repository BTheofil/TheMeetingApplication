package hu.tb.network.repository

import hu.tb.data.search.CoachDto
import hu.tb.data.search.CoachRequestSend
import hu.tb.data.search.CoachResultResponse
import hu.tb.data.search.SearchCoachSend
import hu.tb.network.ApiResult
import hu.tb.network.DataError
import hu.tb.network.asText
import hu.tb.network.safeCall
import hu.tb.search.domain.Coach
import hu.tb.search.domain.RequestCoachResult
import hu.tb.search.domain.SearchResult
import hu.tb.search.domain.Status
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

class SearchRepository(private val httpClient: HttpClient) {

    suspend fun searchCoach(queryCoachName: String): SearchResult {
        val result = safeCall {
            httpClient.post("/searchCoach") {
                setBody(SearchCoachSend(queryCoachName))
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

    suspend fun requestCoach(coachId: String): RequestCoachResult {
        val result = safeCall {
            httpClient.post("/requestToCoach") {
                setBody(CoachRequestSend(coachId.toInt()))
            }
        }

        return when (result) {
            is ApiResult.Fail -> RequestCoachResult(errorMessage = result.dataError.asText())

            is ApiResult.Ok -> when (result.httpResponse.status) {
                HttpStatusCode.OK -> RequestCoachResult(isRequestSent = true)

                HttpStatusCode.Unauthorized ->
                    RequestCoachResult(errorMessage = DataError.UNAUTHORIZED.asText())

                else -> RequestCoachResult(errorMessage = DataError.UNKNOWN.asText())
            }
        }
    }

    private suspend fun HttpResponse.toSearchResult(): SearchResult =
        when (status.value) {
            in 200..299 -> try {
                SearchResult(coaches = body<CoachResultResponse>().coaches.map { it.toDomain() })
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

    private fun CoachDto.toDomain(): Coach =
        Coach(
            id = coachId,
            name = coachName,
            status = when (status.trim().lowercase()) {
                "pending" -> Status.PENDING
                "accepted", "added" -> Status.ADDED
                else -> Status.INIT
            }
        )
}
