package hu.tb.meet.data.repository

import hu.tb.meet.data.repository.helper.accountId
import hu.tb.meet.data.table.BookingTable
import hu.tb.meet.data.table.CoachTable
import hu.tb.meet.data.table.NormalTable
import hu.tb.meet.data.table.ScheduleTable
import hu.tb.meet.data.table.SubscriptionTable
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.receive.ScheduleDay
import hu.tb.meet.domain.send.SessionResult
import hu.tb.meet.domain.send.SubscriptionStatus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
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

        val (from, until) = monthWindow(month)

        ScheduleTable
            .join(BookingTable, JoinType.LEFT, ScheduleTable.id, BookingTable.sessionId)
            .join(NormalTable, JoinType.LEFT, BookingTable.normalId, NormalTable.id)
            .select(
                ScheduleTable.id, ScheduleTable.date, ScheduleTable.start, ScheduleTable.end,
                NormalTable.username
            )
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
                    it[ScheduleTable.end],
                    // left join: null for a session nobody booked
                    it.getOrNull(NormalTable.username)
                )
            }
    }

    fun bookSession(normalUsername: String, sessionId: Int): Boolean? = transaction {
        val normal = accountId(AccountType.NORMAL, normalUsername) ?: return@transaction null

        val bookable = !ScheduleTable
            .join(SubscriptionTable, JoinType.INNER, ScheduleTable.coachId, SubscriptionTable.coachId)
            .select(ScheduleTable.id)
            .where {
                (ScheduleTable.id eq sessionId) and
                        (SubscriptionTable.normalId eq normal) and
                        (SubscriptionTable.status eq SubscriptionStatus.ACCEPTED)
            }
            .empty()
        if (!bookable) return@transaction null

        BookingTable.insertIgnore {
            it[BookingTable.sessionId] = sessionId
            it[BookingTable.normalId] = normal
        }.insertedCount == 1
    }

    fun freeSessions(normalUsername: String, coachId: Int, month: LocalDate): List<SessionResult>? = transaction {
        val normal = accountId(AccountType.NORMAL, normalUsername) ?: return@transaction null
        if (!isAccepted(coachId, normal)) return@transaction null

        val (from, until) = monthWindow(month)

        ScheduleTable
            .join(BookingTable, JoinType.LEFT, ScheduleTable.id, BookingTable.sessionId)
            .select(ScheduleTable.id, ScheduleTable.date, ScheduleTable.start, ScheduleTable.end)
            .where {
                (ScheduleTable.coachId eq coachId) and
                        (ScheduleTable.date greaterEq from) and
                        (ScheduleTable.date less until) and
                        BookingTable.sessionId.isNull()
            }
            .orderBy(ScheduleTable.date to SortOrder.ASC, ScheduleTable.start to SortOrder.ASC)
            .map {
                SessionResult(
                    it[ScheduleTable.id],
                    it[ScheduleTable.date],
                    it[ScheduleTable.start],
                    it[ScheduleTable.end]
                )
            }
    }

    fun myBookings(normalUsername: String): List<SessionResult> = transaction {
        val normal = accountId(AccountType.NORMAL, normalUsername) ?: return@transaction emptyList()

        BookingTable
            .join(ScheduleTable, JoinType.INNER, BookingTable.sessionId, ScheduleTable.id)
            .join(CoachTable, JoinType.INNER, ScheduleTable.coachId, CoachTable.id)
            .select(
                ScheduleTable.id, ScheduleTable.date, ScheduleTable.start, ScheduleTable.end,
                CoachTable.username
            )
            .where { BookingTable.normalId eq normal }
            .orderBy(ScheduleTable.date to SortOrder.ASC, ScheduleTable.start to SortOrder.ASC)
            .map {
                SessionResult(
                    it[ScheduleTable.id],
                    it[ScheduleTable.date],
                    it[ScheduleTable.start],
                    it[ScheduleTable.end],
                    it[CoachTable.username]
                )
            }
    }

    private fun isAccepted(coachId: Int, normalId: Int): Boolean =
        !SubscriptionTable
            .select(SubscriptionTable.coachId)
            .where {
                (SubscriptionTable.coachId eq coachId) and
                        (SubscriptionTable.normalId eq normalId) and
                        (SubscriptionTable.status eq SubscriptionStatus.ACCEPTED)
            }
            .empty()

    private fun monthWindow(month: LocalDate): Pair<LocalDate, LocalDate> {
        val first = month.minus(month.day - 1, DateTimeUnit.DAY)
        return first.minus(1, DateTimeUnit.MONTH) to first.plus(2, DateTimeUnit.MONTH)
    }
}
