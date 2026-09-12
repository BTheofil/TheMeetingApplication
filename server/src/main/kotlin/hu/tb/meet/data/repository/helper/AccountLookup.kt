package hu.tb.meet.data.repository.helper

import hu.tb.meet.data.model.table
import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select

internal fun accountId(type: AccountType, username: String): Int? {
    val accountTable = type.table()
    return accountTable
        .select(accountTable.id)
        .where { accountTable.username eq username }
        .singleOrNull()
        ?.get(accountTable.id)
}
