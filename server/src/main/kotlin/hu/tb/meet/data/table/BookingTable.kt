package hu.tb.meet.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object BookingTable : Table("booking") {
    val sessionId = reference("session_id", ScheduleTable.id, onDelete = ReferenceOption.CASCADE)
    val normalId = reference("normal_id", NormalTable.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(sessionId)

    init {
        index(false, normalId)
    }
}
