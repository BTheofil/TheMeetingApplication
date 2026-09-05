package hu.tb.meet.data.repository

import hu.tb.meet.data.model.CoachTable
import hu.tb.meet.data.model.NormalTable
import hu.tb.meet.data.model.SubscriptionTable
import hu.tb.meet.domain.send.CoachResult
import hu.tb.meet.domain.send.SubscriptionStatus
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SearchRepository {

    fun searchCoach(normalUsername: String, nameLike: String): List<CoachResult> = transaction {
        val normalId = NormalTable
            .select(NormalTable.id)
            .where { NormalTable.username eq normalUsername }
            .singleOrNull()
            ?.get(NormalTable.id)
            ?: return@transaction emptyList()

        val statuses = SubscriptionTable
            .select(SubscriptionTable.coachId, SubscriptionTable.status)
            .where { SubscriptionTable.normalId eq normalId }
            .associate { it[SubscriptionTable.coachId] to it[SubscriptionTable.status] }

        CoachTable
            .select(CoachTable.id, CoachTable.username)
            .where { CoachTable.username like "%$nameLike%" }
            .map {
                val coachId = it[CoachTable.id]
                CoachResult(
                    coachId = coachId.toString(),
                    coachName = it[CoachTable.username],
                    status = statuses[coachId] ?: SubscriptionStatus.INIT
                )
            }
    }
}
