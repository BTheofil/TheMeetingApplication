package hu.tb.domain

data class RegisterForm(
    val username: String,
    val password: String,
    val type: ProfileType
)
