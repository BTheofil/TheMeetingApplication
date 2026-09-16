package hu.tb.meet.data.repository

import hu.tb.meet.data.repository.helper.accountId
import hu.tb.meet.data.table.ScheduleTable
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.ScheduleDay
import hu.tb.meet.domain.send.SessionResult
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ScheduleRepository {

    fun saveDrafts(coachUsername: String, days: List<ScheduleDay>): Int? = transaction {
        val coach = accountId(AccountType.COACH, coachUsername) ?: return@transaction null

        days.sumOf { day ->
            ScheduleTable.batchInsert(day.drafts, ignore = true) {
                this[ScheduleTable.coachId] = coach
                this[ScheduleTable.date] = day.date
                this[ScheduleTable.start] = it.start
                this[ScheduleTable.end] = it.end
            }.size
        }
    }

    fun deleteSession(coachUsername: String, date: LocalDate, sessionStart: LocalTime, sessionEnd: LocalTime): Int =
        transaction {
            val coach = accountId(AccountType.COACH, coachUsername) ?: return@transaction 0

            ScheduleTable.deleteWhere {
                (ScheduleTable.coachId eq coach) and
                        (ScheduleTable.date eq date) and
                        (ScheduleTable.start eq sessionStart) and
                        (ScheduleTable.end eq sessionEnd)
            }
        }

    fun coachSessions(coachUsername: String, month: LocalDate): List<SessionResult>? = transaction {
        val coach = accountId(AccountType.COACH, coachUsername) ?: return@transaction null

        val first = month.minus(month.day - 1, DateTimeUnit.DAY)
        val from = first.minus(1, DateTimeUnit.MONTH)
        val until = first.plus(2, DateTimeUnit.MONTH)

        ScheduleTable
            .select(ScheduleTable.id, ScheduleTable.date, ScheduleTable.start, ScheduleTable.end)
            .where {
                (ScheduleTable.coachId eq coach) and
                        (ScheduleTable.date greaterEq from) and
                        (ScheduleTable.date less until)
            }
            .map {
                SessionResult(
                    it[ScheduleTable.id],
                    it[ScheduleTable.date],
                    it[ScheduleTable.start],
                    it[ScheduleTable.end]
                )
            }
    }
}
