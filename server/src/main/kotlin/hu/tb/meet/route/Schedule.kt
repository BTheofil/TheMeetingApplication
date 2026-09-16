package hu.tb.meet.route

import hu.tb.meet.data.repository.ScheduleRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.ScheduleDay
import hu.tb.meet.domain.receive.SlotReceive
import hu.tb.meet.route.helper.requireAccount
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.schedule() {

    val scheduleRepository by inject<ScheduleRepository>()

    authenticate("auth-jwt") {
        post("/uploadDrafts") {
            val plannedSchedule = call.receive<List<ScheduleDay>>()

            scheduleRepository.saveDrafts(plannedSchedule)

            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate("auth-jwt") {
        delete("/deleteSession") {
            val targetSlot = call.receive<SlotReceive>()
            val count = scheduleRepository.deleteSession(
                targetSlot.coachId,
                targetSlot.date,
                targetSlot.startTime,
                targetSlot.endTime
            )
            if (count != 1) fail(ApiError.SLOT_DELETE_NOT_FOUND)

            call.respond(HttpStatusCode.OK)
        }
    }
}