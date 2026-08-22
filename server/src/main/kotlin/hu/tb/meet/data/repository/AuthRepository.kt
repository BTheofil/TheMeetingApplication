package hu.tb.meet.data.repository

import hu.tb.meet.data.model.table
import hu.tb.meet.domain.AccountRecord
import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class AuthRepository {

    fun find(type: AccountType, username: String): AccountRecord? = transaction {
        val table = type.table()
        table.selectAll()
            .where { table.username eq username }
            .singleOrNull()
            ?.let {
                AccountRecord(
                    id = it[table.id],
                    username = it[table.username],
                    passwordHash = it[table.passwordHash]
                )
            }
    }

    fun exists(type: AccountType, username: String): Boolean =
        find(type, username) != null

    fun create(type: AccountType, username: String, passwordHash: String): Int = transaction {
        val table = type.table()
        table.insert {
            it[table.username] = username
            it[table.passwordHash] = passwordHash
        } get table.id
    }
}
