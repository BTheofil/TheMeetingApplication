package hu.tb.meet.data.repository

import hu.tb.meet.data.model.table
import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ProfileRepository {

    fun deleteProfile(type: AccountType, username: String) = transaction {
        val accountTable = type.table()
        accountTable.deleteWhere {
            accountTable.username eq username
        }
    }
}