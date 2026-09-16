package hu.tb.network.repository

import hu.tb.data.schedule.DraftSend
import hu.tb.data.schedule.UploadDraftSend
import hu.tb.network.ApiResult
import hu.tb.network.apiCall
import hu.tb.schedule.domain.DraftSlot
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.datetime.LocalDate

class ScheduleRepository(
    private val httpClient: HttpClient
) {
    suspend fun uploadDrafts(
        drafts: Map<LocalDate, List<DraftSlot>>
    ): ApiResult<Unit> =
        apiCall<Unit> {
            httpClient.post("/uploadDrafts") {
                setBody(
                    drafts.map { (date, slots) ->
                        UploadDraftSend(
                            date = date,
                            drafts = slots.map { DraftSend(start = it.start, end = it.end) }
                        )
                    }
                )
            }
        }
}