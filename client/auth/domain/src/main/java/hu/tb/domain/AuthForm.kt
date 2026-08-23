package hu.tb.domain

data class AuthForm(
    val username: String,
    val password: String,
    val type: ProfileType
)