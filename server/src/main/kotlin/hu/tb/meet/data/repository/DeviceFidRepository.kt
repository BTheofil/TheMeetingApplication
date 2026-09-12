package hu.tb.meet.data.repository

import hu.tb.meet.data.model.DeviceFidTable
import hu.tb.meet.data.repository.helper.accountId
import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert

class DeviceFidRepository {

    fun register(type: AccountType, username: String, fid: String): Boolean = transaction {
        val ownerId = accountId(type, username) ?: return@transaction false

        DeviceFidTable.upsert {
            it[DeviceFidTable.fid] = fid
            it[accountType] = type
            it[accountId] = ownerId
        }
        true
    }

    fun unregister(type: AccountType, username: String, fid: String): Boolean = transaction {
        val ownerId = accountId(type, username) ?: return@transaction false

        DeviceFidTable.deleteWhere {
            (DeviceFidTable.fid eq fid) and
                    (accountType eq type) and
                    (accountId eq ownerId)
        }
        true
    }

    fun getFids(type: AccountType, ownerId: Int): List<String> = transaction {
        DeviceFidTable
            .select(DeviceFidTable.fid)
            .where { (DeviceFidTable.accountType eq type) and (DeviceFidTable.accountId eq ownerId) }
            .map { it[DeviceFidTable.fid] }
    }

    fun forget(fids: List<String>): Int = transaction {
        if (fids.isEmpty()) return@transaction 0

        DeviceFidTable.deleteWhere { DeviceFidTable.fid inList fids }
    }
}
