package hu.tb.network.repository

import hu.tb.data.schedule.DeleteSessionSend
import hu.tb.data.schedule.DraftDaySend
import hu.tb.data.schedule.DraftSend
import hu.tb.data.schedule.SessionResponse
import hu.tb.data.schedule.SessionsSend
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.network.map
import hu.tb.schedule.domain.TimeRange
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.collections.component1
import kotlin.collections.component2

class ScheduleRepository(
    private val httpClient: HttpClient
) {
    suspend fun uploadDrafts(
        drafts: Map<LocalDate, List<TimeRange.DraftSlot>>
    ): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.post("/uploadDrafts") {
                setBody(
                    drafts.map { (date, slots) ->
                        DraftDaySend(
                            date = date,
                            drafts = slots.map { DraftSend(start = it.start, end = it.end) }
                        )
                    }
                )
            }
        }

    suspend fun getPublishedSessions(date: LocalDate): ApiResult<Map<LocalDate, List<TimeRange.SessionTime>>> =
        apiCall<List<SessionResponse>> {
            httpClient.post("/coachSessions") {
                setBody(SessionsSend(date = date))
            }
        }.map { sessions ->
            sessions
                .groupBy { it.date }
                .mapValues { (_, daySessions) ->
                    daySessions.map {
                        TimeRange.SessionTime(
                            id = it.id,
                            start = it.start,
                            end = it.end
                        )
                    }
                }
        }

    suspend fun deletePublishedSession(
        date: LocalDate,
        start: LocalTime,
        end: LocalTime
    ): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.delete("/deleteSession") {
                setBody(
                    DeleteSessionSend(
                        date = date,
                        startTime = start,
                        endTime = end
                    )
                )
            }
        }
}
