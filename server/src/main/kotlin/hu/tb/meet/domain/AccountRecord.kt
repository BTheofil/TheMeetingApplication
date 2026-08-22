package hu.tb.meet.domain

data class AccountRecord(
    val id: Int,
    val username: String,
    val passwordHash: String
)