package hu.tb.meet.data.repository

import hu.tb.meet.data.repository.helper.accountId
import hu.tb.meet.data.table.table
import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ProfileRepository {

    fun findProfileId(type: AccountType, username: String): Int? = transaction {
        accountId(type, username)
    }

    fun deleteProfile(type: AccountType, username: String): Int = transaction {
        val accountTable = type.table()
        accountTable.deleteWhere {
            accountTable.username eq username
        }
    }
}
