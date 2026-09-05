package hu.tb.meet.data.model

import hu.tb.meet.domain.send.SubscriptionStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object SubscriptionTable : Table("subscription") {
    val coachId = reference("coach_id", CoachTable.id, onDelete = ReferenceOption.CASCADE)
    val normalId = reference("normal_id", NormalTable.id, onDelete = ReferenceOption.CASCADE)
    val status = enumerationByName<SubscriptionStatus>("status", 10)

    override val primaryKey = PrimaryKey(coachId, normalId)
}
