package hu.tb.meet.data.repository

import hu.tb.meet.data.model.CoachTable
import hu.tb.meet.data.model.NormalTable
import hu.tb.meet.data.model.SubscriptionTable
import hu.tb.meet.data.model.table
import hu.tb.meet.domain.receive.AccountType
import hu.tb.meet.domain.send.CoachResult
import hu.tb.meet.domain.send.SubscriberResult
import hu.tb.meet.domain.send.SubscriptionStatus
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.jdbc.upsert

class SubscriptionRepository {

    fun request(normalUsername: String, coachId: Int): SubscriptionStatus? = transaction {
        val normalId = accountId(AccountType.NORMAL, normalUsername) ?: return@transaction null

        val coachExists = !CoachTable
            .select(CoachTable.id)
            .where { CoachTable.id eq coachId }
            .empty()
        if (!coachExists) return@transaction null

        SubscriptionTable.upsert(
            SubscriptionTable.coachId, SubscriptionTable.normalId,
            where = { SubscriptionTable.status eq SubscriptionStatus.INIT }
        ) {
            it[SubscriptionTable.coachId] = coachId
            it[SubscriptionTable.normalId] = normalId
            it[status] = SubscriptionStatus.PENDING
        }

        SubscriptionTable
            .select(SubscriptionTable.status)
            .where { (SubscriptionTable.coachId eq coachId) and (SubscriptionTable.normalId eq normalId) }
            .single()[SubscriptionTable.status]
    }

    fun pendingRequests(coachUsername: String): List<SubscriberResult> = transaction {
        val coachId = accountId(AccountType.COACH, coachUsername) ?: return@transaction emptyList()

        SubscriptionTable
            .join(NormalTable, JoinType.INNER, SubscriptionTable.normalId, NormalTable.id)
            .select(NormalTable.id, NormalTable.username)
            .where {
                (SubscriptionTable.coachId eq coachId) and
                        (SubscriptionTable.status eq SubscriptionStatus.PENDING)
            }
            .map {
                SubscriberResult(
                    normalId = it[NormalTable.id].toString(),
                    normalName = it[NormalTable.username]
                )
            }
    }

    fun accept(coachUsername: String, normalId: Int): Boolean =
        resolve(coachUsername, normalId, SubscriptionStatus.ACCEPTED)

    fun reject(coachUsername: String, normalId: Int): Boolean =
        resolve(coachUsername, normalId, SubscriptionStatus.INIT)

    fun myCoaches(normalUsername: String): List<CoachResult> = transaction {
        val normalId = accountId(AccountType.NORMAL, normalUsername) ?: return@transaction emptyList()

        SubscriptionTable
            .join(CoachTable, JoinType.INNER, SubscriptionTable.coachId, CoachTable.id)
            .select(CoachTable.id, CoachTable.username)
            .where {
                (SubscriptionTable.normalId eq normalId) and
                        (SubscriptionTable.status eq SubscriptionStatus.ACCEPTED)
            }
            .map {
                CoachResult(
                    coachId = it[CoachTable.id].toString(),
                    coachName = it[CoachTable.username],
                    status = SubscriptionStatus.ACCEPTED
                )
            }
    }

    private fun resolve(
        coachUsername: String,
        normalId: Int,
        newStatus: SubscriptionStatus
    ): Boolean = transaction {
        val coachId = accountId(AccountType.COACH, coachUsername) ?: return@transaction false

        SubscriptionTable.update({
            (SubscriptionTable.coachId eq coachId) and
                    (SubscriptionTable.normalId eq normalId) and
                    (SubscriptionTable.status eq SubscriptionStatus.PENDING)
        }) { it[status] = newStatus } > 0
    }

    private fun accountId(type: AccountType, username: String): Int? {
        val accountTable = type.table()
        return accountTable
            .select(accountTable.id)
            .where { accountTable.username eq username }
            .singleOrNull()
            ?.get(accountTable.id)
    }
}
