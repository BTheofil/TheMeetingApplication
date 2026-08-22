package hu.tb.meet.domain

data class JwtConfig(
    val issuer: String,
    val audience: String,
    val secret: String,
)
