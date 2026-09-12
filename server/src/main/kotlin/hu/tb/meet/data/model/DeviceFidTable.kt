package hu.tb.meet.data.model

import hu.tb.meet.domain.receive.AccountType
import org.jetbrains.exposed.v1.core.Table

object DeviceFidTable : Table("device_fid") {
    val fid = varchar("fid", 512)
    val accountType = enumerationByName<AccountType>("account_type", 10)
    val accountId = integer("account_id")

    override val primaryKey = PrimaryKey(fid)

    init {
        index(false, accountType, accountId)
    }
}
