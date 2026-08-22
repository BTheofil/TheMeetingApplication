package hu.tb.meet.domain.send

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(val token: String)
