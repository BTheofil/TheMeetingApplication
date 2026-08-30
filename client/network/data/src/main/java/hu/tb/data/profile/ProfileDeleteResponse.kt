package hu.tb.data.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDeleteResponse(
    val message: String
)
