package hu.tb.network.repository

import hu.tb.data.search.CoachDto
import hu.tb.data.search.CoachRequestSend
import hu.tb.data.search.CoachResultResponse
import hu.tb.data.search.SearchCoachSend
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.network.map
import hu.tb.search.domain.Coach
import hu.tb.search.domain.Status
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SearchRepository(private val httpClient: HttpClient) {

    suspend fun searchCoach(queryCoachName: String): ApiResult<List<Coach>> =
        apiCall<CoachResultResponse> {
            httpClient.post("/searchCoach") {
                setBody(SearchCoachSend(queryCoachName))
            }
        }.map { response -> response.coaches.map { it.toDomain() } }

    suspend fun requestCoach(coachId: Int): ApiResult<Unit> {
        return apiCall<Unit> {
            httpClient.post("/requestToCoach") {
                setBody(CoachRequestSend(coachId))
            }
        }
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
