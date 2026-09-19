package hu.tb.network.repository

import hu.tb.dashboard.domain.CoachItem
import hu.tb.dashboard.domain.FreeSession
import hu.tb.dashboard.domain.SessionItem
import hu.tb.data.dashboard.BookFreeSessionSend
import hu.tb.data.dashboard.FreeSessionSend
import hu.tb.data.dashboard.MyCoachResponse
import hu.tb.data.dashboard.SessionResponse
import hu.tb.data.schedule.SessionResponse as CoachSessionResponse
import hu.tb.data.schedule.SessionsSend
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.network.map
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.datetime.LocalDate

class DashboardRepository(
    private val httpClient: HttpClient
) {
    suspend fun getCoaches(): ApiResult<List<CoachItem>> =
        apiCall<List<MyCoachResponse>> {
            httpClient.get("/myCoaches")
        }.map { listOfCoach ->
            listOfCoach.map {
                CoachItem(
                    id = it.coachId.toInt(),
                    name = it.coachName
                )
            }
        }

    suspend fun getFreeSessions(coachId: Int, date: LocalDate): ApiResult<List<FreeSession>> =
        apiCall<List<SessionResponse>> {
            httpClient.post("/freeSessions") {
                setBody(FreeSessionSend(coachId, date))
            }
        }.map { responses ->
            responses.map {
                FreeSession(
                    id = it.id,
                    coachId = coachId,
                    date = it.date,
                    start = it.start,
                    end = it.end
                )
            }
        }

    suspend fun getBookedSessions(): ApiResult<List<SessionItem>> =
        apiCall<List<SessionResponse>> {
            httpClient.get("/myBookings")
        }.map { responses ->
            responses.map {
                SessionItem(
                    id = it.id.toString(),
                    counterpartName = it.counterpart.orEmpty(),
                    date = it.date,
                    start = it.start,
                    end = it.end
                )
            }
        }

    suspend fun getCoachSessions(date: LocalDate): ApiResult<List<SessionItem>> =
        apiCall<List<CoachSessionResponse>> {
            httpClient.post("/coachSessions") {
                setBody(SessionsSend(date))
            }
        }.map { responses ->
            responses.mapNotNull { response ->
                response.counterpart?.let {
                    SessionItem(
                        id = response.id.toString(),
                        counterpartName = it,
                        date = response.date,
                        start = response.start,
                        end = response.end
                    )
                }
            }
        }

    suspend fun bookASession(sessionId: Int): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.post("/bookSession") {
                setBody(BookFreeSessionSend(sessionId))
            }
        }
}
