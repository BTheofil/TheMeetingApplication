package hu.tb.meet.data.model

import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.Table

abstract class AccountTable(name: String) : Table(name) {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val passwordHash = varchar("password_hash", 60)

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(username)
    }
}

fun AccountType.table(): AccountTable = when (this) {
    AccountType.COACH -> CoachTable
    AccountType.NORMAL -> NormalTable
}
