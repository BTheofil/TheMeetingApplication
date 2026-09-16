package hu.tb.meet.data.repository

import hu.tb.meet.data.table.ScheduleTable
import hu.tb.meet.domain.receive.ScheduleDay
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ScheduleRepository {

    fun saveDrafts(days: List<ScheduleDay>): Int = transaction {
        days.sumOf { day ->
            ScheduleTable.batchInsert(day.drafts, ignore = true) {
                this[ScheduleTable.coachId] = day.coachId
                this[ScheduleTable.date] = day.date
                this[ScheduleTable.start] = it.start
                this[ScheduleTable.end] = it.end
            }.size
        }
    }

    fun deleteSession(coachId: Int, date: LocalDate, sessionStart: LocalTime, sessionEnd: LocalTime): Int =
        transaction {
            ScheduleTable.deleteWhere {
                (ScheduleTable.coachId eq coachId) and
                        (ScheduleTable.date eq date) and
                        (ScheduleTable.start eq sessionStart) and
                        (ScheduleTable.end eq sessionEnd)
            }
        }
}
