package hu.tb.network.repository

import hu.tb.dashboard.domain.CoachItem
import hu.tb.data.dashboard.MyCoachResponse
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.network.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class DashboardRepository(
    private val httpClient: HttpClient
) {
    suspend fun getCoaches(): ApiResult<List<CoachItem>> =
        apiCall<List<MyCoachResponse>> {
            httpClient.get("/myCoaches")
        }.map { listOfCoach -> listOfCoach.map { CoachItem(id = it.coachId, name = it.coachName) } }
}