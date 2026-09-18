package hu.tb.meet.route

import hu.tb.meet.data.repository.ScheduleRepository
import hu.tb.meet.domain.error.ApiError
import hu.tb.meet.domain.error.fail
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.BookReceive
import hu.tb.meet.domain.receive.CoachMonthReceive
import hu.tb.meet.domain.receive.MonthReceive
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
            val (username, _) = requireAccount(AccountType.COACH)

            val plannedSchedule = call.receive<List<ScheduleDay>>()

            scheduleRepository.saveDrafts(username, plannedSchedule) ?: fail(ApiError.PROFILE_NOT_FOUND)

            call.respond(HttpStatusCode.OK)
        }

        post("/coachSessions") {
            val (username, _) = requireAccount(AccountType.COACH)

            val sessions = scheduleRepository.coachSessions(username, call.receive<MonthReceive>().date)
                ?: fail(ApiError.PROFILE_NOT_FOUND)

            call.respond(HttpStatusCode.OK, sessions)
        }

        delete("/deleteSession") {
            val (username, _) = requireAccount(AccountType.COACH)

            val targetSlot = call.receive<SlotReceive>()
            val count = scheduleRepository.deleteSession(
                username,
                targetSlot.date,
                targetSlot.startTime,
                targetSlot.endTime
            )
            if (count != 1) fail(ApiError.SLOT_DELETE_NOT_FOUND)

            call.respond(HttpStatusCode.OK)
        }

        post("/bookSession") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val booked = scheduleRepository.bookSession(username, call.receive<BookReceive>().sessionId)
                ?: fail(ApiError.SESSION_NOT_BOOKABLE)
            if (!booked) fail(ApiError.SESSION_ALREADY_BOOKED)

            call.respond(HttpStatusCode.OK)
        }

        post("/freeSessions") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            val receive = call.receive<CoachMonthReceive>()
            val sessions = scheduleRepository.freeSessions(username, receive.coachId, receive.date)
                ?: fail(ApiError.SESSION_NOT_BOOKABLE)

            call.respond(HttpStatusCode.OK, sessions)
        }

        get("/myBookings") {
            val (username, _) = requireAccount(AccountType.NORMAL)

            call.respond(HttpStatusCode.OK, scheduleRepository.myBookings(username))
        }
    }
}
