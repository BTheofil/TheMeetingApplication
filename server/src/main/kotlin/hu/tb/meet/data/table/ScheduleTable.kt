package hu.tb.meet.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.time

object ScheduleTable : Table("schedule") {
    val id = integer("id").autoIncrement()
    val coachId = reference("coach_id", CoachTable.id, onDelete = ReferenceOption.CASCADE)
    val date = date("date")
    val start = time("start")
    val end = time("end")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(coachId, date, start)
    }
}
